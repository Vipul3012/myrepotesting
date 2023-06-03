package com.peloton.apparel.migration.product.price.routes;

import org.apache.camel.EndpointInject;
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
import org.springframework.test.context.ActiveProfiles;

import com.peloton.apparel.migration.product.price.pojo.ProductPriceRecord;


@SpringBootTest(classes = PriceFailedRecordRoute.class)
@EnableAutoConfiguration
@CamelSpringBootTest
@ActiveProfiles("test")
@MockEndpoints("*")
@UseAdviceWith
class PriceFailedRecordRouteTest {
    //Test
	@Autowired
	private ProducerTemplate template;

	@EndpointInject("mock:file")
	private MockEndpoint mock;


	@Autowired
	private ModelCamelContext context;

	@Test
	void PriceFailedRecordRoute() throws Exception {


		AdviceWith.adviceWith(context.getRouteDefinition("ProductpriceFailedRecordsRoute"), context,
				new AdviceWithRouteBuilder() {

					@Override
					public void configure() throws Exception {

						weaveById("FailedRecordCSV").replace().to("mock:file");

					}
				});
		context.start();
		mock.expectedMessageCount(1);

		template.sendBody("seda:ProductPricefailedRawTraffic", country_code);
		mock.assertIsSatisfied();
	}

	ProductPriceRecord country_code = new ProductPriceRecord("b5779ef0-1a26-4b72-8824-a213e48b14a7", null, "", "81",
			null, null, null);
}
