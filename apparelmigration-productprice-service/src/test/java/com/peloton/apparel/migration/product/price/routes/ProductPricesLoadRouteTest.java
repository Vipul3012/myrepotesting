package com.peloton.apparel.migration.product.price.routes;

import java.util.ArrayList;
import java.util.List;

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

import com.peloton.apparel.migration.product.price.ct.service.CtClientService;
import com.peloton.apparel.migration.product.price.ct.service.CtProductPriceService;
import com.peloton.apparel.migration.product.price.pojo.ProductPriceRecord;

@SpringBootTest(classes = {ProductPricesLoadRoute.class, CtProductPriceService.class, CtClientService.class})
@EnableAutoConfiguration
@ActiveProfiles("test")
@CamelSpringBootTest
@MockEndpoints("*")
@UseAdviceWith
class ProductPricesLoadRouteTest {

	@Autowired
	private ProducerTemplate template;

	@EndpointInject("mock:micrometer:counter:numberOfPriceRecordsProcessedSuccessfully")
	private MockEndpoint mock;

	@Autowired
	private ModelCamelContext context;

	@Autowired
	CtProductPriceService ctProductPriceService;

	@Test
	void testCtLoadProductPrice() throws Exception {

		AdviceWith.adviceWith(
				context.getRouteDefinition("ProductPricesLoadRoute"), context,
				new AdviceWithRouteBuilder() {
					@Override
					public void configure() throws Exception {

						weaveById("ProductPriceCTLoadRoute").replace()
								.process((exchange) -> {
									exchange.getIn().setBody(priceRecord);
								});
					}
				});

		context.start();

		mock.setMinimumExpectedMessageCount(1);

		template.sendBody("seda:aggregatedProductPriceTraffic", priceList());

		mock.assertIsSatisfied();
	}

	ProductPriceRecord priceRecord = new ProductPriceRecord(
			"b5779ef0-1a26-4b72-8824-a213e48b14a7", "CA", "", "81", null, null, null);

	private List<ProductPriceRecord> priceList() {
		List<ProductPriceRecord> list = new ArrayList<ProductPriceRecord>();
		list.add(priceRecord);
		return list;
	}
}
