
package com.peloton.apparel.migration.product.price.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.peloton.apparel.migration.product.price.constants.ProductPriceServiceConstants;
import com.peloton.apparel.migration.product.price.util.ProductPriceRecordAggregationStrategy;

/**
 * This class defines a Camel route which aggregates the ProductPriceRecord
 * objects with same DataHub id/grid values into a list
 * ((List<ProductPriceRecord>)). If an exchange sits inactive for 5 second it
 * sends the exchange to the next Seda endpoint.
 * 
 *
 */
@Component
public class ProductPricesAggregationRoute extends RouteBuilder {

	@Autowired
	ProductPriceRecordAggregationStrategy productPriceRecordAggregationStrategy;

	@Override
	public void configure() throws Exception {

		from(ProductPriceServiceConstants.SEDA_ENDPOINT_RAW_RECORDS)
				.routeId(ProductPriceServiceConstants.ROUTEID_AGGREGATION_ROUTE)
				.log(ProductPriceServiceConstants.MSG_RAWRECORDS_READING)
				.aggregate(simple(ProductPriceServiceConstants.BODY_GRID),
						productPriceRecordAggregationStrategy)
				.completionTimeout(
						ProductPriceServiceConstants.LONG_TIMEOUT_5000L)
				// wait for 5 seconds to aggregate
				.to(ProductPriceServiceConstants.SEDA_ENDPOINT_AGGREGATED_RECORDS);

	}

}
