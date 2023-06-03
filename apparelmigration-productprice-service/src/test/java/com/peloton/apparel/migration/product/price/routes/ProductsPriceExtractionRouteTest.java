package com.peloton.apparel.migration.product.price.routes;

import java.io.InputStream;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = ProductsPriceExtractionRoute.class)
@ActiveProfiles("test")
@EnableAutoConfiguration
@CamelSpringBootTest
@MockEndpoints("*")
class ProductsPriceExtractionRouteTest {

	@EndpointInject("mock:seda:rawProductPriceTraffic")
	private MockEndpoint mock;

	@Autowired
	private ProducerTemplate template;

	@Autowired
	private ModelCamelContext context;

	@Autowired
	private ResourceLoader resourceLoader;

	@Disabled
	@Test
	void testRoutePositiveFlow() throws Exception {
		AdviceWith.adviceWith(context.getRouteDefinition("ProductPriceDataExtraction"), context,
				new AdviceWithRouteBuilder() {

					@Override
					public void configure() throws Exception {
						replaceFromWith("direct:start");

					}
				});

		InputStream body = resourceLoader.getResource("classpath:Pelohub_Prices_Sample.csv").getInputStream();
		
		context.start();
		mock.setMinimumExpectedMessageCount(1);

		template.sendBody("direct:start", body);
		mock.assertIsSatisfied();
	}

}
