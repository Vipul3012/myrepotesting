
package com.peloton.apparel.migration.product.price.util;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy;
import org.springframework.stereotype.Component;

import com.peloton.apparel.migration.product.price.pojo.ProductPriceRecord;
/**
 * This is a utility class which defines the Aggregation Strategy for the aggregation route.
 * In the current version all the matching records are added to a list.
 *
 */
@Component
public class ProductPriceRecordAggregationStrategy extends AbstractListAggregationStrategy<ProductPriceRecord> {

	@Override
	public ProductPriceRecord getValue(Exchange exchange) {
		return exchange.getIn().getBody(ProductPriceRecord.class);
	}

}
