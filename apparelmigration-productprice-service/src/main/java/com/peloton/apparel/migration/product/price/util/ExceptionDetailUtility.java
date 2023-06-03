package com.peloton.apparel.migration.product.price.util;

import java.util.List;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import com.peloton.apparel.migration.product.price.constants.ProductPriceServiceConstants;
import com.peloton.apparel.migration.product.price.pojo.ProductPriceRecord;

/**
 * This utility class extracts the exception message and 
 * sets it as an instance variable inside the ProductPriceRecord object.
 * @author manmohan.shukla
 *
 */
@Component
public class ExceptionDetailUtility{
	@SuppressWarnings("unchecked")
	public void addErrorDetailInBody(Exchange exchange) {
		List<ProductPriceRecord> priceRecords = exchange.getIn().getBody(List.class);
		
		Throwable errorThrown = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
		
		priceRecords
		.stream()
		.forEach( 
				priceRecord -> {
					priceRecord.setRecordStatus(ProductPriceServiceConstants.LITEREAL_UPDATE_FAILED);
					if(errorThrown instanceof NumberFormatException) {
						priceRecord.setFailureReasons(ProductPriceServiceConstants.LITEREAL_PRICE
								+ProductPriceServiceConstants.SYMBOL_HYPHEN_WITHSPACES
								+errorThrown.getMessage().toLowerCase());
					}
					else {
						priceRecord.setFailureReasons(errorThrown.getMessage());
					}
				}
		);		
	}
}
