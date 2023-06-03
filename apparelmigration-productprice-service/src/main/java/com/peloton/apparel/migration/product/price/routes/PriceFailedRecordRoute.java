package com.peloton.apparel.migration.product.price.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.peloton.apparel.migration.product.price.constants.ProductPriceServiceConstants;

@Component
public class PriceFailedRecordRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		from(ProductPriceServiceConstants.SEDA_FAILED_CSV_ROUTE_RECORDS)
				.routeId(
						ProductPriceServiceConstants.ROUTEID_FAILED_RECORDS_FILE)
				.log(ProductPriceServiceConstants.BODY)
				.to("file:{{failed-file-location}}?"
						+ "fileName={{failed-csv-file-to-read-name}}"
						+ "&fileExist=Append" + "&charset=utf-8")
				.id(ProductPriceServiceConstants.ENDPOINTID_FAILEDRECORDCSV);

	}

}
