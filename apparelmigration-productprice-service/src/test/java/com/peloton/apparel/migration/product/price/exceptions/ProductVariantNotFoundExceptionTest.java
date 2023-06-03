package com.peloton.apparel.migration.product.price.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.peloton.apparel.migration.product.price.constants.ProductPriceServiceConstants;

class ProductVariantNotFoundExceptionTest {
	@Test
	void ProductVariantNotFoundExceptionTesting() {
		Throwable exception = assertThrows(
				ProductVariantNotFoundException.class, () -> {
					throw new ProductVariantNotFoundException(
							ProductPriceServiceConstants.TEST_VARIANT_NOT_FOUND_EXPECTED_MESSAGE);
				});
		assertEquals(
				ProductPriceServiceConstants.TEST_VARIANT_NOT_FOUND_EXPECTED_MESSAGE,
				exception.getMessage());
	}
}