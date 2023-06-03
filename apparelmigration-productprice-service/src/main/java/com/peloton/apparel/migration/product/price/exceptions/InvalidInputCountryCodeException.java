package com.peloton.apparel.migration.product.price.exceptions;

/**
 * This exception is thrown if any invalid or unexpected country code is
 * provided in input. Example- "RU" is provided as country code.
 *
 */
public class InvalidInputCountryCodeException extends ProductPriceDataMigrationException {

	private static final long serialVersionUID = 7101492767499315850L;

	public InvalidInputCountryCodeException(String message) {
		super(message);
	}

}
