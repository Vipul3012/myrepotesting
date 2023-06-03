package com.peloton.apparel.migration.product.price.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peloton.apparel.migration.product.price.pojo.ProductPriceRecord;

@TestInstance(Lifecycle.PER_CLASS)
class PriceRecordValidatorTest {
	
	PriceRecordValidator priceRecordValidator = null;
	
	Exchange exchange = null;
	ProductPriceRecord validPriceRecord = null;
	
	@BeforeAll
	void setupData() throws StreamReadException, DatabindException, IOException {
		priceRecordValidator = new PriceRecordValidator();
		
		InputStream priceInputStream = PriceRecordValidatorTest.class.getResourceAsStream("/ValidPriceRecord.json");
		ObjectMapper objectMapper = new ObjectMapper();
		validPriceRecord = objectMapper.readValue(priceInputStream, ProductPriceRecord.class);
		
		CamelContext camelContext = new DefaultCamelContext();
		exchange = new DefaultExchange(camelContext);
	}
	
	@SuppressWarnings("unused")
	@Test
	void testPriceRecordValidator_whenValidRecordProvided_shouldReturnTrue() {
		boolean validActual;
		
		given:{
			assertNotNull(priceRecordValidator);
			assertNotNull(exchange);
			assertNotNull(validPriceRecord);
		}
		when:{
			exchange.getIn().setBody(validPriceRecord);
			validActual = priceRecordValidator.matches(exchange);
		}
		then:{
			assertTrue(validActual);
		}
	}
	
	@SuppressWarnings("unused")
	@Test
	void testPriceRecordValidator_whenInvalidRecordProvided_shouldReturnFalse() {
		boolean validActual;
		
		given:{
			assertNotNull(priceRecordValidator);
			assertNotNull(exchange);
			assertNotNull(validPriceRecord);
		}
		when:{
			String price = validPriceRecord.getPrice(); //storing valid price in a variable to reset the record later
			validPriceRecord.setPrice(null);
			
			exchange.getIn().setBody(validPriceRecord);
			validActual = priceRecordValidator.matches(exchange);
			
			validPriceRecord.setPrice(price);
		}
		then:{
			assertFalse(validActual);
			assertEquals("price must not be blank", validPriceRecord.getFailureReasons());
		}
	}

}
