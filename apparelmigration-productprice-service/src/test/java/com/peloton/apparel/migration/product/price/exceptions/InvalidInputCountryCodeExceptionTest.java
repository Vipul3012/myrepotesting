package com.peloton.apparel.migration.product.price.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.peloton.apparel.migration.product.price.constants.ProductPriceServiceConstants;

class InvalidInputCountryCodeExceptionTest {
	@Test
	void InvalidInputCountryCodeExceptionTesting() {
		Throwable exception = assertThrows(
				InvalidInputCountryCodeException.class, () -> {
					throw new InvalidInputCountryCodeException(
							ProductPriceServiceConstants.TEST_INVALID_INPUT_COUNTRY_CODE_EXPECTED_MESSAGE);
				});
		assertEquals(
				ProductPriceServiceConstants.TEST_INVALID_INPUT_COUNTRY_CODE_EXPECTED_MESSAGE,
				exception.getMessage());
	}
}