package com.peloton.apparel.migration.product.price.routes;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peloton.apparel.migration.product.price.constants.ProductPriceServiceConstants;
import com.peloton.apparel.migration.product.price.ct.service.CtProductPriceService;
import com.peloton.apparel.migration.product.price.pojo.ProductPriceRecord;
import com.peloton.apparel.migration.product.price.util.ExceptionDetailUtility;
import com.peloton.apparel.migration.product.price.util.PriceRecordValidator;

@SpringBootTest (classes = {PriceSyncService.class, PriceRecordValidator.class, ExceptionDetailUtility.class})
@EnableAutoConfiguration
@CamelSpringBootTest
@MockEndpoints
@UseAdviceWith
class PriceSyncServiceTest {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ProducerTemplate producerTemplate;
	
	@Autowired
	ModelCamelContext modelCamelContext;
	
	@MockBean
	CtProductPriceService mockCtProductPriceService;
	
	@EndpointInject("mock:micrometer:counter:numberOfRecordsSuccessfullyUpdated-PriceSyncService")
	private MockEndpoint mockMicrometer;
	
	@SuppressWarnings("unused")
	@Test
	void priceSyncServiceTest_whenPriceRecordReceived_SuccessMicrometerShouldBeIncremented() throws Exception {
		ProductPriceRecord productPriceRecord = null;
		//InputStream priceRecordInputStream = null;
		String productPriceRecordString = null;
		
		given:{
			assertNotNull(objectMapper);
			assertNotNull(producerTemplate);
			
			try(InputStream priceRecordInputStream = PriceSyncServiceTest.class.getResourceAsStream("/ValidPriceRecord.json")){
				productPriceRecordString = new  String(priceRecordInputStream.readAllBytes());
			} catch (IOException | NullPointerException e) {
				e.printStackTrace();
			}
			assertNotNull(productPriceRecordString);
			System.out.println("string input " +productPriceRecordString);
		}
		when:{
			AdviceWith.adviceWith(
					modelCamelContext.getRouteDefinition(ProductPriceServiceConstants.ROUTEID_PRODUCT_PRICE_SYNC_SERVICE), 
					modelCamelContext, 
					new AdviceWithRouteBuilder() {
						@Override
						public void configure() throws Exception {
							replaceFromWith("direct:start-mock");
						}
					});

			doAnswer( invocation -> {
				Exchange exchange = invocation.getArgument(0);
				@SuppressWarnings("unchecked")
				List<ProductPriceRecord> listPriceRecords = exchange.getIn().getBody(List.class);
				listPriceRecords.get(0).setRecordStatus("Success Test");
				return null;
			})
			.when(mockCtProductPriceService).fetchProductAndUpdatePrices(any(Exchange.class));
			
			modelCamelContext.start();
			producerTemplate.sendBody("direct:start-mock", productPriceRecordString);
		}
		then:{
			mockMicrometer.setExpectedCount(1);
			mockMicrometer.assertIsSatisfied();
			
			modelCamelContext.close();
		}	
	}
}
