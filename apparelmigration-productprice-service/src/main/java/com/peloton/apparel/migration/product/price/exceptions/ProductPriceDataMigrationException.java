package com.peloton.apparel.migration.product.price.exceptions;

/**
 * This is the parent exception for all the custom exceptions of this service.
 *
 */
public class ProductPriceDataMigrationException extends Exception {

	private static final long serialVersionUID = 7302790810773969763L;

	public ProductPriceDataMigrationException(String message) {
		super(message);
	}
}
