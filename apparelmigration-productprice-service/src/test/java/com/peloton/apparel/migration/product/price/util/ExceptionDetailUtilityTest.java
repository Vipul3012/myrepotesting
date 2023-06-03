package com.peloton.apparel.migration.product.price.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.peloton.apparel.migration.product.price.exceptions.ProductVariantNotFoundException;
import com.peloton.apparel.migration.product.price.pojo.ProductPriceRecord;

@TestInstance(Lifecycle.PER_CLASS)
class ExceptionDetailUtilityTest {
	
	private CamelContext camelContext = null;
	private Exchange exchange = null;
	private ExceptionDetailUtility exceptionDetailUtility = null;
	
	@BeforeAll
	void setupAndInitialize() {
		exceptionDetailUtility = new ExceptionDetailUtility();
		
		camelContext = new DefaultCamelContext();
		exchange = new DefaultExchange(camelContext);
		
		ProductPriceRecord productPriceRecord = new ProductPriceRecord();
		exchange.getIn().setBody(new ArrayList<>(Arrays.asList(productPriceRecord)));
	}

	@SuppressWarnings("unused")
	@Test
	void testAddErrorDetailInBody_whenExceptionRaised_shouldAddExceptionDetailsInRecord() throws Exception {
		given:{
			assertNotNull(exceptionDetailUtility);
			assertNotNull(exchange);
			
			exchange.setException(new ProductVariantNotFoundException("Test Message"));
			exchange.setProperty(Exchange.EXCEPTION_CAUGHT, new ProductVariantNotFoundException("Test Message"));
			
		}
		when:{
			exceptionDetailUtility.addErrorDetailInBody(exchange);
		}
		then:{
			@SuppressWarnings("unchecked")
			List<ProductPriceRecord> priceRecordList = exchange.getIn().getBody(List.class);
			assertEquals("Test Message", priceRecordList.get(0).getFailureReasons());
		}
	}
	
	@SuppressWarnings("unused")
	@Test
	void testAddErrorDetailInBody_whenNumberFormatExceptionRaised_shouldAddExceptionDetailsInRecord() throws Exception {
		given:{
			assertNotNull(exceptionDetailUtility);
			assertNotNull(exchange);
			
			exchange.setException(new NumberFormatException("Test Message - NumberFormatExc"));
			exchange.setProperty(Exchange.EXCEPTION_CAUGHT, new NumberFormatException("Test Message - NumberFormatExc"));
			
		}
		when:{
			exceptionDetailUtility.addErrorDetailInBody(exchange);
		}
		then:{
			@SuppressWarnings("unchecked")
			List<ProductPriceRecord> priceRecordList = exchange.getIn().getBody(List.class);
			assertTrue("Price - Test Message - NumberFormatExc".equalsIgnoreCase(
					priceRecordList.get(0).getFailureReasons()));
		}
	}

}
