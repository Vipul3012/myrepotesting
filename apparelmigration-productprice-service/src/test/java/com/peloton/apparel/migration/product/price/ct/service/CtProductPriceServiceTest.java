package com.peloton.apparel.migration.product.price.ct.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.commercetools.api.models.common.PriceDraft;
import com.commercetools.api.models.product.ProductAddPriceAction;
import com.peloton.apparel.migration.product.price.exceptions.CurrencyCodeMismatchException;
import com.peloton.apparel.migration.product.price.exceptions.InvalidInputCountryCodeException;
import com.peloton.apparel.migration.product.price.exceptions.ProductVariantNotFoundException;

class CtProductPriceServiceTest {

	@Mock
	CtProductPriceService productPriceService;
	
	private CtClientService ctClientService = Mockito.mock(CtClientService.class);

	@BeforeEach
	public void setUp() {
		productPriceService = new CtProductPriceService(ctClientService);
	}

	@Test
	void shouldGetCurrencyISOforUS() throws InvalidInputCountryCodeException {
		String actual = productPriceService.getCurrencyISOfromCountry("US");
		assertEquals("USD", actual);
	}

	@Test
	void shouldGetCurrencyISOforGB() throws InvalidInputCountryCodeException {
		String actual = productPriceService.getCurrencyISOfromCountry("GB");
		assertEquals("GBP", actual);
	}

	@Test
	void shouldGetCurrencyISOforCA() throws InvalidInputCountryCodeException {
		String actual = productPriceService.getCurrencyISOfromCountry("CA");
		assertEquals("CAD", actual);
	}

	@Test
	void shouldGetCurrencyISOforDE() throws InvalidInputCountryCodeException {
		String actual = productPriceService.getCurrencyISOfromCountry("DE");
		assertEquals("EUR", actual);
	}

	@Test
	void shouldGetCurrencyISOforAU() throws InvalidInputCountryCodeException {
		String actual = productPriceService.getCurrencyISOfromCountry("AU");
		assertEquals("AUD", actual);
	}

	@Test
	void shouldInvalidInputCountryCodeExceptionTesting() {
		Throwable exception = assertThrows(InvalidInputCountryCodeException.class, () -> {
			productPriceService.getCurrencyISOfromCountry("ZX");
		});
		assertEquals("Invalid country code provided- ZX", exception.getMessage());
	}

	@Test
	void shouldFindVariant() throws ProductVariantNotFoundException {
		PriceDraft priceDraft = productPriceService.createPriceDraft("US", "USD", "50");
		assertNotNull(priceDraft);
	}

	@Test
	void shouldCreatePriceDraft() throws ProductVariantNotFoundException {
		PriceDraft priceDraft = productPriceService.createPriceDraft("US", "USD", "50.200");
		assertNotNull(priceDraft);
	}

	@Test
	void shouldCreateProductAddPriceAction() {
		ProductAddPriceAction action = productPriceService.createProductAddPriceAction("FA222-GL-AW-BAND-BLK-40S", "US",
				"USD", "20.00");
		assertNotNull(action);
	}
	
	@Test
	void shouldCheckInputCurrencyCode() {
		String actual = "Currency- EUR not expected for Country- US";
		Throwable exception = assertThrows(CurrencyCodeMismatchException.class, () -> {
			productPriceService.checkInputCurrencyCode("EUR", "USD", "US");
		});
		assertEquals(actual, exception.getMessage());
	}

}
