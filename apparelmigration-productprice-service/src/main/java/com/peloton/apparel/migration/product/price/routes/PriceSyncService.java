package com.peloton.apparel.migration.product.price.routes;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.processor.PredicateValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.commercetools.api.client.error.ConcurrentModificationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.peloton.apparel.migration.product.price.constants.ProductPriceServiceConstants;
import com.peloton.apparel.migration.product.price.ct.service.CtProductPriceService;
import com.peloton.apparel.migration.product.price.exceptions.CurrencyCodeMismatchException;
import com.peloton.apparel.migration.product.price.exceptions.InvalidInputCountryCodeException;
import com.peloton.apparel.migration.product.price.exceptions.ProductVariantNotFoundException;
import com.peloton.apparel.migration.product.price.pojo.ProductPriceRecord;
import com.peloton.apparel.migration.product.price.util.ExceptionDetailUtility;
import com.peloton.apparel.migration.product.price.util.PriceRecordValidator;

/**
 * This class defines the Product Price Sync Service which listens on a Kafka topic for incoming Price Records
 * And calls CtProductPriceService to upsert those records into Commercetools.
 * Any failed records are sent to a Kafka failure topic.
 * @author manmohan.shukla
 *
 */
@Component
public class PriceSyncService extends RouteBuilder{
	
	@Autowired
	PriceRecordValidator priceRecordValidator;
	
	@Autowired
	CtProductPriceService ctProductPriceService;
	
	@Autowired
	ExceptionDetailUtility exceptionDetailUtility;

	@SuppressWarnings("unchecked")
	@Override
	public void configure() throws Exception {
		
		onException(JsonParseException.class, MismatchedInputException.class)
			.handled(true)
			.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_PRICE_SYNC_FAILED_RECORDS)
			.log("Unable to parse the following message in Json:-\nException caught - ${exception}\nBody - ${body}")
			.stop();
		
		onException(PredicateValidationException.class)
			.handled(true)
			.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_PRICE_SYNC_FAILED_RECORDS)
			.log("Record validation failed because of following reasons:-\nException caught - ${exception}\n${body.failureReasons}\nBody - ${body}")
			.marshal().json(JsonLibrary.Jackson, true)
			.to(ProductPriceServiceConstants.ENDPOINT_KAFKA_PRICE_SYNC_FAILED_RECORDS)
			.stop();
		
		onException(ConcurrentModificationException.class)
			.handled(true)
			.maximumRedeliveries(
					ProductPriceServiceConstants.INT_MAX_REDELIVERIES)
			// retry 10 times
			.redeliveryDelay(ProductPriceServiceConstants.LONG_DELAY_1000L)
			.process(exchange -> exceptionDetailUtility.addErrorDetailInBody(exchange))
			.split(simple(ProductPriceServiceConstants.BODY))
			.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_PRICE_SYNC_FAILED_RECORDS)
			.log("Update failed because of following reasons:-\nException caught - ${exception}\nBody - ${body}")
			.marshal().json(JsonLibrary.Jackson, true)
			.to(ProductPriceServiceConstants.ENDPOINT_KAFKA_PRICE_SYNC_FAILED_RECORDS)
			.stop();
			
		onException(ProductVariantNotFoundException.class, NumberFormatException.class, 
				ExecutionException.class, InvalidInputCountryCodeException.class, 
				CurrencyCodeMismatchException.class, Exception.class)
			.handled(true)
			.process(exchange -> exceptionDetailUtility.addErrorDetailInBody(exchange))
			.split(simple(ProductPriceServiceConstants.BODY))
			.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_PRICE_SYNC_FAILED_RECORDS)
			.log("Update failed because of following reasons:-\nException caught - ${exception}\nBody - ${body}")
			.marshal().json(JsonLibrary.Jackson, true)
			.to(ProductPriceServiceConstants.ENDPOINT_KAFKA_PRICE_SYNC_FAILED_RECORDS)
			.stop();
		
				
		from(ProductPriceServiceConstants.ENDPOINT_KAFKA_PRICE_SYNC_INPUT)
		.routeId(ProductPriceServiceConstants.ROUTEID_PRODUCT_PRICE_SYNC_SERVICE)
		.log(ProductPriceServiceConstants.LOG_PRICE_SYNC_PROCESSING_STARTED)
		.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_PRICE_SYNC_INPUT_RECORDS)
		.unmarshal().json(JsonLibrary.Jackson, ProductPriceRecord.class)
		.log(ProductPriceServiceConstants.LOG_PRICE_SYNC_UNMARSHALLED_RECORD)
		.validate(priceRecordValidator)
		.convertBodyTo(List.class)
		.process(exchange -> ctProductPriceService.fetchProductAndUpdatePrices(exchange))
		.split(simple(ProductPriceServiceConstants.BODY))
		.marshal().json(JsonLibrary.Jackson, true)
		.log(ProductPriceServiceConstants.LOG_PRICE_SYNC_PROCESSING_COMPLETED)
		.to(ProductPriceServiceConstants.MICROMETER_INCREMENT_PRICE_SYNC_SUCCESSFULL_RECORDS)
		.stop();
	}

}
