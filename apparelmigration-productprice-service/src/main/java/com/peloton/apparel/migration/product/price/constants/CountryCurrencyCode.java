package com.peloton.apparel.migration.product.price.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CountryCurrencyCode {

	US("USD"),
	GB("GBP"),
	CA("CAD"),
	DE("EUR"),
	AU("AUD");
	
	private final String code;
}
