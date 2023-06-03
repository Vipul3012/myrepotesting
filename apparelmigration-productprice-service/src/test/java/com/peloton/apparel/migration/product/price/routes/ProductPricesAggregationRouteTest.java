
package com.peloton.apparel.migration.product.price.routes;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import com.peloton.apparel.migration.product.price.pojo.ProductPriceRecord;
import com.peloton.apparel.migration.product.price.util.ProductPriceRecordAggregationStrategy;

@SpringBootTest (classes = {ProductPricesAggregationRoute.class, ProductPriceRecordAggregationStrategy.class})
@CamelSpringBootTest
@EnableAutoConfiguration
@MockEndpoints("seda:aggregatedProductPriceTraffic")

class ProductPricesAggregationRouteTest {

	@Autowired
	private ProducerTemplate template;

	@EndpointInject("mock:seda:aggregatedProductPriceTraffic")
	private MockEndpoint mock;

	@Disabled
	@Test
	void sendBody() throws InterruptedException {

		mock.expectedBodiesReceived("List<Exchange>(1 elements)");

		template.sendBody("seda:rawProductPriceTraffic", priceRecord);

		mock.assertIsSatisfied();

	}

	ProductPriceRecord priceRecord = new ProductPriceRecord(
			"a399c596-6e58-4fdf-b1bc-101a1a4693f3", "AU", "", "82.0000",
			"null", null, null);

}
