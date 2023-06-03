package com.peloton.apparel.migration.product.price.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.peloton.apparel.migration.product.price.constants.ProductPriceServiceConstants;

class ProductPriceDataMigrationExceptionTest {
	@Test
	void ProductPriceDataMigrationExceptionTesting() {
		Throwable exception = assertThrows(
				ProductPriceDataMigrationException.class, () -> {
					throw new ProductPriceDataMigrationException(
							ProductPriceServiceConstants.TEST_PRODUCT_PRICE_DATA_MIGRATION_EXPECTED_MESSAGE);
				});
		assertEquals(
				ProductPriceServiceConstants.TEST_PRODUCT_PRICE_DATA_MIGRATION_EXPECTED_MESSAGE,
				exception.getMessage());
	}
}