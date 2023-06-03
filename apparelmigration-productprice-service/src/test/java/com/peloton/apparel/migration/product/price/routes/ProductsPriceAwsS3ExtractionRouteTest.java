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
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;

import com.peloton.apparel.migration.product.price.constants.ProductPriceServiceConstants;

@SpringBootTest(classes = { ProductsPriceExtractionAwsS3Route.class })
@CamelSpringBootTest
@EnableAutoConfiguration
@MockEndpoints(ProductPriceServiceConstants.SEDA_ENDPOINT_RAW_RECORDS)
@UseAdviceWith
@ActiveProfiles("test")
class ProductsPriceAwsS3ExtractionRouteTest {

	@EndpointInject("mock:seda:rawProductPriceTraffic")
	private MockEndpoint mock;

	@Autowired
	private ProducerTemplate template;

	@Autowired
	private ModelCamelContext context;

	@Autowired
	private ResourceLoader resourceLoader;

	@Test
	@Disabled("disabling this test case as file is not being properly read in build pipeline")
	void testRoutePositiveFlow() throws Exception {
		AdviceWith.adviceWith(context.getRouteDefinition(ProductPriceServiceConstants.S3_ROUTEID_CSV_TO_SEDA), context,
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
