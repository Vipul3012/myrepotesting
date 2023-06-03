package com.peloton.apparel.migration.product.price.exceptions;

/**
 * This exception is thrown if a currency code is provided in the input and that
 * currency code is not expected for the input country. Example- If currency
 * code "AUD" is given with country "US"
 *
 */
public class CurrencyCodeMismatchException extends ProductPriceDataMigrationException {

	private static final long serialVersionUID = -2325126844497888056L;

	public CurrencyCodeMismatchException(String message) {
		super(message);
	}

}
