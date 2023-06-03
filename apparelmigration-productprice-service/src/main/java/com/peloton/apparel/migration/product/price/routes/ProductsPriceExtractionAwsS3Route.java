package com.peloton.apparel.migration.product.price.routes;

import java.net.UnknownHostException;
import java.nio.charset.MalformedInputException;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.peloton.apparel.migration.product.price.constants.ProductPriceServiceConstants;
import com.peloton.apparel.migration.product.price.pojo.ProductPriceRecord;

import software.amazon.awssdk.core.exception.SdkClientException;

/**
 * A class that builds a camel route that extracts the product prices from CSV
 * files inside S3 bucket file
 * 
 * @author shinu.pillai@onepeloton.com
 *
 */

@Component
@Profile({"!stage & !sandbox"})
public class ProductsPriceExtractionAwsS3Route extends RouteBuilder {

	/**
	 * A method that configures the camel routes for reading from S3 bucket
	 */
	@Override
	public void configure() throws Exception {

		onException(IllegalArgumentException.class)
				.routeId(ProductPriceServiceConstants.ROUTEID_IAS3EXCEPTION)
				.handled(true)
				.log(ProductPriceServiceConstants.MSG_FAILED_RECORD)
				.setBody(simple(ProductPriceServiceConstants.HEADER_RAWRECORD))
				.transform(body().append("\n"))
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_VALIDATION_FAILED_RECORDS_COUNTER)
				.to(ProductPriceServiceConstants.SEDA_FAILED_CSV_ROUTE_RECORDS);

		onException(MalformedInputException.class).routeId(
				ProductPriceServiceConstants.ROUTEID_S3_INVALID_INPUTFILE_EXCEPTION)
				.handled(true)
				.log(LoggingLevel.INFO,
						ProductPriceServiceConstants.MSG_EXCEPTION_HANDLED)
				.log(ProductPriceServiceConstants.MSG_FILE_NOT_PRESENT)
				.to(ProductPriceServiceConstants.SEDA_FAILED_CSV_ROUTE_RECORDS);

		// Handle SdkClientException

		onException(SdkClientException.class).routeId(
				ProductPriceServiceConstants.ROUTEID_AWSS3_SDKCLIENT_EXCEPTION)
				.handled(true)
				.log(ProductPriceServiceConstants.MSG_SDKCLIENT_EXCEPTION_OCCURED)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FILES_FAILED_READING_COUNTER);

		// Handle UnknownHostException
		onException(UnknownHostException.class).routeId(
				ProductPriceServiceConstants.ROUTEID_AWSS3_UNKNOWNHOST_EXCEPTION)
				.handled(true)
				.log(ProductPriceServiceConstants.MSG_UNKNOWNHOST_EXCEPTION_OCCURED)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FILES_FAILED_READING_COUNTER);

		from("aws2-s3://{{awsS3BucketName}}" + "?"
				+ "useDefaultCredentialsProvider=true&"
				+ "deleteAfterRead=false&"
				//+ "prefix={{bucketFolder}}&"
				+ "fileName=testfile.txt"
				)
				//+ "amazonS3Client=#awsConfig.getS3()" + "&prefix=${FOLDER}"
				//+ "&fileName=*.CSV" + "&maxMessagesPerPoll=1"
				//+ "&moveAfterRead=true" + "&destinationBucket={{bucketName}}"
				//+ "&prefix=${proceesedFolderName}")
		//This route is currently under testing and modification
		//TODO - cleanup route after testing
				// The Route to handle the extraction of the raw records from
				// the S3 bucket
				.routeId(ProductPriceServiceConstants.S3_ROUTEID_CSV_TO_SEDA)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FILES_UPLOADED_COUNTER)
				.log("log-test-file ${body}")
				//TODO - remove above temporary log later
				.split(body()
						.tokenize(ProductPriceServiceConstants.ESCAPE_CHAR))
				.streaming()
				.setHeader(ProductPriceServiceConstants.RAWRECORD,
						simple(ProductPriceServiceConstants.BODY))
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_RAWRECORDS_READ_COUNTER)
				.log(ProductPriceServiceConstants.RAWRECORD_BODY).unmarshal()
				.bindy(BindyType.Csv, ProductPriceRecord.class, true).choice()
				.when(simple(ProductPriceServiceConstants.BODYGRID_IS_GRID))
				.stop().end()
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_RAWRECORDS_MARSHALLED_COUNTER)
				.to(ProductPriceServiceConstants.SEDA_ENDPOINT_RAW_RECORDS)
				;

	}
}
