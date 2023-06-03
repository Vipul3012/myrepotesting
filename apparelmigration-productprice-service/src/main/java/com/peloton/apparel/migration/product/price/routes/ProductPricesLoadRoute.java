
package com.peloton.apparel.migration.product.price.routes;

import java.util.concurrent.ExecutionException;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.commercetools.api.client.error.ConcurrentModificationException;
import com.peloton.apparel.migration.product.price.constants.ProductPriceServiceConstants;
import com.peloton.apparel.migration.product.price.ct.service.CtProductPriceService;
import com.peloton.apparel.migration.product.price.exceptions.CurrencyCodeMismatchException;
import com.peloton.apparel.migration.product.price.exceptions.InvalidInputCountryCodeException;
import com.peloton.apparel.migration.product.price.exceptions.ProductVariantNotFoundException;
import com.peloton.apparel.migration.product.price.pojo.ProductPriceRecord;

/**
 * This class primarily defines a camel route that accepts Aggregated Records
 * (List<ProductPriceRecord>) and send those to the CtProductPriceService which
 * updates the prices in CT. This class also defines few Exception clauses which
 * handles exceptions thrown for improper records.
 *
 */
@Component
public class ProductPricesLoadRoute extends RouteBuilder {

	@Autowired
	CtProductPriceService ctProductPriceService;

	@Override
	public void configure() throws Exception {

		// No productVariant exist with given GRID(datahub Id)
		onException(ProductVariantNotFoundException.class).routeId(
				ProductPriceServiceConstants.ROUTEID_PRODUCTVARIANT_HANDLER)
				.handled(true).split(simple(ProductPriceServiceConstants.BODY))
				.log(ProductPriceServiceConstants.MSG_PRODUCTVARIANT_NOTFOUND)
				.marshal().bindy(BindyType.Csv, ProductPriceRecord.class)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FAILED_RECORDS_COUNTER)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FAILED_RECORDS_VARIANT_NOT_FOUND_COUNTER)
				.to(ProductPriceServiceConstants.SEDA_FAILED_CSV_ROUTE_RECORDS);

		// Handling the records that are failed while updating in CT
		onException(ExecutionException.class)
				.routeId(ProductPriceServiceConstants.ROUTEID_CTEXCEPTION)
				.handled(true).split(simple(ProductPriceServiceConstants.BODY))
				.log(ProductPriceServiceConstants.MSG_EXECUTION_EXCEPTION)
				.marshal().bindy(BindyType.Csv, ProductPriceRecord.class)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FAILED_RECORDS_COUNTER)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FAILED_RECORDS_EXECUTION_EXCEPTION_COUNTER)
				.to(ProductPriceServiceConstants.SEDA_FAILED_CSV_ROUTE_RECORDS);

		onException(InvalidInputCountryCodeException.class).routeId(
				ProductPriceServiceConstants.ROUTEID_INVALID_COUNTRY_CODE_EXCEPTION)
				.handled(true).split(simple(ProductPriceServiceConstants.BODY))
				.log(ProductPriceServiceConstants.MSG_INVALIDINPUTCOUNTRY_EXCEPTION)
				.marshal().bindy(BindyType.Csv, ProductPriceRecord.class)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FAILED_RECORDS_COUNTER)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FAILED_RECORDS_INVALID_COUNTRY_COUNTER)
				.to(ProductPriceServiceConstants.SEDA_FAILED_CSV_ROUTE_RECORDS);

		onException(CurrencyCodeMismatchException.class).routeId(
				ProductPriceServiceConstants.ROUTEID_CURRENCY_CODE_MISMATCH_EXCEPTION)
				.handled(true).split(simple(ProductPriceServiceConstants.BODY))
				.log(ProductPriceServiceConstants.MSG_CURRENCYCODEMISMATCH_EXCEPTION)
				.marshal().bindy(BindyType.Csv, ProductPriceRecord.class)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FAILED_RECORDS_COUNTER)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FAILED_RECORDS_CURRENCY_MISMATCH_COUNTER)
				.to(ProductPriceServiceConstants.SEDA_FAILED_CSV_ROUTE_RECORDS);

		// If input Price is not a number
		onException(NumberFormatException.class).routeId(
				ProductPriceServiceConstants.ROUTEID_NUMBER_FORMAT_EXCEPTION)
				.handled(true).split(simple(ProductPriceServiceConstants.BODY))
				.log(ProductPriceServiceConstants.MSG_NUMBERFORMAT_EXCEPTION)
				.marshal().bindy(BindyType.Csv, ProductPriceRecord.class)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FAILED_RECORDS_COUNTER)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FAILED_RECORDS_NUMBER_FORMAT_EXCEPTION_COUNTER)
				.to(ProductPriceServiceConstants.SEDA_FAILED_CSV_ROUTE_RECORDS);

		// some other thread updated the same product in between
		onException(ConcurrentModificationException.class)
				.routeId(
						ProductPriceServiceConstants.ROUTEID_CONCURRENT_MODIFICATION_EXCEPTION)
				.handled(true)
				.maximumRedeliveries(
						ProductPriceServiceConstants.INT_MAX_REDELIVERIES)
				// retry 10 times
				.redeliveryDelay(ProductPriceServiceConstants.LONG_DELAY_1000L)
				// 1 second interval between each retry
				.split(simple(ProductPriceServiceConstants.BODY))
				.log(ProductPriceServiceConstants.MSG_CONCURRENT_MODIFICATION_EXCEPTION)
				.marshal().bindy(BindyType.Csv, ProductPriceRecord.class)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FAILED_RECORDS_COUNTER)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FAILED_RECORDS_CONCURRENT_MODIFICATION_COUNTER)
				.to(ProductPriceServiceConstants.SEDA_FAILED_CSV_ROUTE_RECORDS);

		onException(Exception.class)
				.routeId(ProductPriceServiceConstants.ROUTEID_ROOT_EXCEPTION)
				.handled(true).split(simple(ProductPriceServiceConstants.BODY))
				.log(ProductPriceServiceConstants.MSG_FAILED_EXCEPTION)
				.marshal().bindy(BindyType.Csv, ProductPriceRecord.class)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FAILED_RECORDS_COUNTER)
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_FAILED_RECORDS_ROOT_EXCEPTION_COUNTER)
				.to(ProductPriceServiceConstants.SEDA_FAILED_CSV_ROUTE_RECORDS);

		// This routes accepts Aggregated Records (List<ProductPriceRecord>) and
		// send those to CtProductPriceService to update prices in CT

		from(ProductPriceServiceConstants.SEDA_ENDPOINT_AGGREGATED_RECORDS
				+ "?concurrentConsumers="
				+ ProductPriceServiceConstants.INT_CONCURRENT_CONSUMERS)
				.routeId(
						ProductPriceServiceConstants.ROUTEID_PRODUCT_PRICES_LOAD_ROUTE)
				.log(ProductPriceServiceConstants.MSG_AGGREGATED_READING)
				.process(
					exchange -> ctProductPriceService.fetchProductAndUpdatePrices(exchange)
				)
				.id(ProductPriceServiceConstants.ENDPOINTID_CTPRODUCT)
				.choice()
				.when(exchangeProperty(ProductPriceServiceConstants.EXCHANGE_PROPERTY_DUPLICATE_RECORDS_LOG).isNotNull())
					.log(ProductPriceServiceConstants.SIMPLE_EXCHANGE_PROPERTY_DUPLICATE_RECORDS_LOG)
				.end()
				.log(ProductPriceServiceConstants.SIMPLE_EXCHANGE_PROPERTY_SUCCESS_MESSAGE)
				.split(simple(ProductPriceServiceConstants.BODY))
				.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_SUCCESSFULL_RECORDS_COUNTER)
				.stop();

	}

}
