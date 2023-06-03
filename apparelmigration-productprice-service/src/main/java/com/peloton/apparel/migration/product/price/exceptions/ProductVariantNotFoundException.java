package com.peloton.apparel.migration.product.price.exceptions;

/**
 * This exception is thrown if no variant is found with the provided DataHub ID.
 *
 */
public class ProductVariantNotFoundException extends ProductPriceDataMigrationException {

	private static final long serialVersionUID = -7901377827915459233L;

	public ProductVariantNotFoundException(String message) {
		super(message);
	}
}
