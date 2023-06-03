package com.peloton.apparel.migration.product.price.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.peloton.apparel.migration.product.price.constants.ProductPriceServiceConstants;

class CurrencyCodeMismatchExceptionTest {

	@Test
	void CurrencyCodeMismatchTesting() {
		Throwable exception = assertThrows(CurrencyCodeMismatchException.class,
				() -> {
					throw new CurrencyCodeMismatchException(
							ProductPriceServiceConstants.TEST_CURRENCY_CODE_MISMATCH_EXPECTED_MESSAGE);
				});
		assertEquals(
				ProductPriceServiceConstants.TEST_CURRENCY_CODE_MISMATCH_EXPECTED_MESSAGE,
				exception.getMessage());
	}
}