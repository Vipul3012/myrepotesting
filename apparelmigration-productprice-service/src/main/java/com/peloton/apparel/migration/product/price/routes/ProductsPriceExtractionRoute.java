package com.peloton.apparel.migration.product.price.routes;

import java.nio.charset.MalformedInputException;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.springframework.stereotype.Component;

import com.peloton.apparel.migration.product.price.constants.ProductPriceServiceConstants;
import com.peloton.apparel.migration.product.price.pojo.ProductPriceRecord;

/**
 * A class that builds a camel route that extracts the product prices from CSV
 * file
 * 
 * @author priyanka.wadekar@onepeloton.com
 *
 */
@Component
public class ProductsPriceExtractionRoute extends RouteBuilder {

	/**
	 * A method that configures the camel routes
	 */
	@Override
	public void configure() throws Exception {

		onException(IllegalArgumentException.class)
				.routeId(ProductPriceServiceConstants.ROUTEID_IAEXCEPTION)
				.handled(true)
				.log(ProductPriceServiceConstants.MSG_FAILED_RECORD)
				.setBody(simple(ProductPriceServiceConstants.HEADER_RAWRECORD))
				.transform(
						body().append(ProductPriceServiceConstants.ESCAPE_CHAR))
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_VALIDATION_FAILED_RECORDS_COUNTER)
				.to(ProductPriceServiceConstants.SEDA_FAILED_CSV_ROUTE_RECORDS);

		onException(MalformedInputException.class).routeId(
				ProductPriceServiceConstants.ROUTEID_INVALID_INPUTFILE_EXCEPTION)
				.handled(true)
				.log(LoggingLevel.INFO,
						ProductPriceServiceConstants.MSG_EXCEPTION_HANDLED)
				.log(ProductPriceServiceConstants.MSG_FILE_NOT_PRESENT)
				.to(ProductPriceServiceConstants.SEDA_FAILED_CSV_ROUTE_RECORDS);

		// The Route to handle the extraction of the raw records from the CSV
		from("file:{{productPrice-csv-file-to-read-location}}??" 
				+ "?"
				+ "maxMessagesPerPoll=1&" 
				+ "idempotent=true&"
				+ "include={{csv-file-name-regex}}&" 
				+ "includeExt=csv,CSV&"
				+ "startingDirectoryMustHaveAccess=true&"
				+ "startingDirectoryMustExist=true&"
				+ "move=.processed/${date:now:MMddyyyy'T'HH-mm-ssZ}/${file:name}&"
				+ "moveFailed=.error/${date:now:MMddyyyy'T'HH-mm-ssZ}/${file:name}")
				.routeId(ProductPriceServiceConstants.ROUTEID_CSV_TO_SEDA)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FILES_UPLOADED_COUNTER)
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
				.to(ProductPriceServiceConstants.SEDA_ENDPOINT_RAW_RECORDS);

	}
}
