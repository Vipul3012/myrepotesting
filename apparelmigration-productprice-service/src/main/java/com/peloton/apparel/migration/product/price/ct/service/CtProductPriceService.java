package com.peloton.apparel.migration.product.price.ct.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.channel.ChannelResourceIdentifierBuilder;
import com.commercetools.api.models.common.CentPrecisionMoneyDraftBuilder;
import com.commercetools.api.models.common.Price;
import com.commercetools.api.models.common.PriceDraft;
import com.commercetools.api.models.common.PriceDraftBuilder;
import com.commercetools.api.models.product.Product;
import com.commercetools.api.models.product.ProductAddPriceAction;
import com.commercetools.api.models.product.ProductAddPriceActionBuilder;
import com.commercetools.api.models.product.ProductChangePriceActionBuilder;
import com.commercetools.api.models.product.ProductPagedQueryResponse;
import com.commercetools.api.models.product.ProductUpdate;
import com.commercetools.api.models.product.ProductUpdateAction;
import com.commercetools.api.models.product.ProductUpdateBuilder;
import com.commercetools.api.models.product.ProductVariant;
import com.peloton.apparel.migration.product.price.constants.CountryCurrencyCode;
import com.peloton.apparel.migration.product.price.constants.ProductPriceServiceConstants;
import com.peloton.apparel.migration.product.price.exceptions.CurrencyCodeMismatchException;
import com.peloton.apparel.migration.product.price.exceptions.InvalidInputCountryCodeException;
import com.peloton.apparel.migration.product.price.exceptions.ProductVariantNotFoundException;
import com.peloton.apparel.migration.product.price.pojo.ProductPriceRecord;

/**
 * This class accepts a list of ProductPriceRecord objects fetches the product
 * with the variant containing the specified Grid ID (in excel)/
 * legacy-option-id (in CT) and updates the prices for the matching variant
 * according to ProductPriceRecords.
 * 
 * This service assumes that legacy-option-id and Product Prices (if any) will
 * already be published i.e. Current Price Data = Staged Price Data
 * 
 * This class may throw following exceptions which are handled in the load
 * route- IOException, InvalidInputCountryCodeException,
 * ProductVariantNotFoundException, CurrencyCodeMismatchException
 *
 */

@Component
public class CtProductPriceService {

	private ProjectApiRoot apiRoot = null;
	
	public CtProductPriceService(CtClientService clientService) {
		this.apiRoot = clientService.createApiClient();
	}

	/**
	 * This method accepts the List<ProductPriceRecord>, fetches the product
	 * containing variant with specified Grid ID (in excel)/ legacy-option-id (in
	 * CT) and send it to updateVariantPrices method to add/modify the prices
	 * 
	 * @param exchange
	 * @throws InvalidInputCountryCodeException
	 * @throws ProductVariantNotFoundException
	 * @throws CurrencyCodeMismatchException
	 */
	@SuppressWarnings("unchecked")
	public void fetchProductAndUpdatePrices(Exchange exchange) throws InvalidInputCountryCodeException,
			ProductVariantNotFoundException, CurrencyCodeMismatchException {

		List<ProductPriceRecord> productPriceRecords = exchange.getIn().getBody(List.class);
		String dataHubID = null;
		List<String> duplicateRecordsLog = new ArrayList<>();
		
		/*
		 * Null check is not needed here as load route is reading after aggregation 
		 * so exchange will always have one ProductPriceRecord 
		 */
		dataHubID = productPriceRecords.get(0).getGrid();
		
		Product productContainingVariant = fetchProductByDatahubID(dataHubID);
		ProductVariant productVariant = findVariant(productContainingVariant, dataHubID);

		String successMessage = updateVariantPrices(productContainingVariant, productPriceRecords,
				productVariant, duplicateRecordsLog);

		if(ProductPriceServiceConstants.LITEREAL_VALIDATIONS_PASSED.equalsIgnoreCase(
				productPriceRecords.get(0).getRecordStatus())) {
			productPriceRecords.get(0).setRecordStatus("Successfully updated at "+ ZonedDateTime.now());
		}
		exchange.setProperty(ProductPriceServiceConstants.EXCHANGE_PROPERTY_SUCCESS_MESSAGE,
				successMessage + ProductPriceServiceConstants.LITEREAL_DATAHUBID_APPEND + dataHubID);
		
		if(! duplicateRecordsLog.isEmpty()) { //For duplicate ProductPriceRecords (if any)
			String duplicateLog = duplicateRecordsLog
					.stream()
					.collect(Collectors.joining("\n"));
			exchange.setProperty(ProductPriceServiceConstants.EXCHANGE_PROPERTY_DUPLICATE_RECORDS_LOG, duplicateLog);
		}
		
	}

	/**
	 * Fetches the product containing variant which has the matching
	 * legacy-option-id (in CT) attribute value It assumes the legacy-option-id of
	 * the matching product variant is in current (published) data
	 * 
	 * @param dataHubID
	 * @return Product
	 * @throws ProductVariantNotFoundException if CT does not have any variant with
	 *                                         provided dataHubID
	 */
	public Product fetchProductByDatahubID(String dataHubID)
			throws ProductVariantNotFoundException {
		
		ProductPagedQueryResponse productPagedQueryResponse = apiRoot
				.products()
				.get()
				.addLimit(1)
				.withWhere(ProductPriceServiceConstants.WITH_WHERE_VALUE, ProductPriceServiceConstants.VAL, dataHubID)
				.executeBlocking()
				.getBody();

		if (productPagedQueryResponse.getResults().isEmpty()) {
			throw new ProductVariantNotFoundException(
					ProductPriceServiceConstants.MSG_EXCEPTION_VARIANT_NOT_FOUND 
					+ dataHubID);
		}

		return productPagedQueryResponse.getResults().get(0);
	}

	/**
	 * This method accepts a Product and a DataHub/Grid ID and returns the
	 * ProductVariant with the specified DataHub/Grid ID It assumes the
	 * legacy-option-id (in CT) of the product is in current (published) data
	 * 
	 * @param productContainingVariant
	 * @param dataHubID
	 * @throws ProductVariantNotFoundException
	 */
	public ProductVariant findVariant(Product productContainingVariant, String dataHubID)
			throws ProductVariantNotFoundException {

		Optional<ProductVariant> productVariantResult = productContainingVariant.getMasterData()
				.getCurrent()
				.getAllVariants()
				.stream()
				.filter(productVariant -> productVariant.getAttributes()
						.stream()
						.anyMatch( attribute ->  
							attribute.getName().equals(ProductPriceServiceConstants.LEGACY_OPTION_ID) 
							&& attribute.getValue().equals(dataHubID)
							) 
						)
				.findAny();
		if(productVariantResult.isPresent()) {
			return productVariantResult.get();
		}
		else {
			throw new ProductVariantNotFoundException(
					ProductPriceServiceConstants.MSG_EXCEPTION_VARIANT_NOT_FOUND + dataHubID);
		}		
	}

	/**
	 * This method calls another method to prepare CT ProductUpdate and executes it
	 * 
	 * @param product
	 * @param productPriceRecords
	 * @param productVariant
	 * @return StringBuilder with a success message and variant SKU
	 * @throws InvalidInputCountryCodeException
	 * @throws CurrencyCodeMismatchException
	 */
	public String updateVariantPrices(Product product, List<ProductPriceRecord> productPriceRecords,
			ProductVariant productVariant, List<String> duplicateRecordsLog) 
					throws InvalidInputCountryCodeException, CurrencyCodeMismatchException {

		ProductUpdate productUpdate = createUpdatePricesProductUpdate(
				product, productPriceRecords, productVariant, duplicateRecordsLog);

		apiRoot
		.products()
		.withId(product.getId())
		.post(productUpdate)
		.executeBlocking()
		.getBody();

		return ProductPriceServiceConstants.STRING_PRICE_SUCCESS_MSG + productVariant.getSku();
	}

	/**
	 * Creates and returns ProductUpdate with ProductAddPriceAction(s) and/or
	 * ProductChangePriceAction(s)
	 * 
	 * @param product
	 * @param productPriceRecords
	 * @param productVariant
	 * @return ProductUpdate to be executed
	 * @throws InvalidInputCountryCodeException
	 * @throws CurrencyCodeMismatchException
	 */
	public ProductUpdate createUpdatePricesProductUpdate(Product product, List<ProductPriceRecord> productPriceRecords,
			ProductVariant productVariant, List<String> duplicateRecordsLog) throws InvalidInputCountryCodeException, CurrencyCodeMismatchException {

		/*
		 * Map of prices already existing in CT for the selected variant We'll add new
		 * entries to it for any ProductPriceRecord we process in the current batch Key
		 * = CountryISOCode of prices already present in CT and the ProductPriceRecords
		 * processed so far (in the current batch) Value = PriceID (if price already
		 * exists in CT with no ProductChangePriceAction associated as of yet) Value =
		 * Null (if price already exists in CT and has a ProductChangePriceAction
		 * associated to it) Value = Null (if a new price is to be added in CT
		 * (ProductAddPriceAction)) This is required to efficiently fetch PriceID for
		 * any ProductChangePriceAction and also to ignore multiple ProductPriceRecords
		 * with same DataHubID and Country (if any)
		 */

		Map<String, String> currentPricesMap = createExistingPricesMap(productVariant);

		List<ProductUpdateAction> productUpdateActions = new ArrayList<>();

		// TODO: not a very good implementation, use streaming and map-reduce / filter
		/*
		 * RESPONSE - The methods being called below throws checked exceptions and to convert it to streams
		 *  will likely require extra wrappers around the lambda functions
		 *  OR instead of extending Exception in ProductPriceDataMigrationException I can extend RuntimeException 
		 *  OR I can keep this as is
		 *  
		 *  Catching those exceptions and returning null will require major refactoring
		 */
		 
		for (ProductPriceRecord productPriceRecord : productPriceRecords) {

			// "GB" is the correct ISO 3166-1 alpha-2 code for "UK"
			if (productPriceRecord.getCountryCode().equalsIgnoreCase("UK")) {
				productPriceRecord.setCountryCode("GB");
			}

			String inputCountry = productPriceRecord.getCountryCode();
			String inputCountryUpperCase = inputCountry.toUpperCase();

			String expectedCurrencyISO = getCurrencyISOfromCountry(inputCountry);
			checkInputCurrencyCode(productPriceRecord.getCurrency(), expectedCurrencyISO, inputCountry);

			if (currentPricesMap.containsKey(inputCountryUpperCase)) {// Price already exist in CT OR there is a
																		// ProductAddPriceAction for the same country
																		// code already
				addModifyPriceAction(productPriceRecord, currentPricesMap, productUpdateActions, duplicateRecordsLog);
			} else {// New price to be added in CT
				productUpdateActions.add(createProductAddPriceAction(productVariant.getSku(), inputCountryUpperCase,
						expectedCurrencyISO, productPriceRecord.getPrice()));
				currentPricesMap.put(inputCountryUpperCase, null); // to check for any ProductPriceRecord which may come
																	// later with same DataHubID and country
			}

		} // for ends

		return ProductUpdateBuilder.of()
				.version(product.getVersion())
				.actions(productUpdateActions)
				.build();
	}

	/**
	 * This method creates and add ProductChangePriceAction to a list and also
	 * handle (log and ignore) multiple ProductPriceRecords with same country (if
	 * any)
	 * 
	 * @param productPriceRecord
	 * @param currentPricesMap
	 * @param productChangePriceActions
	 * @throws InvalidInputCountryCodeException
	 */

	public void addModifyPriceAction(ProductPriceRecord productPriceRecord, Map<String, String> currentPricesMap,
			List<ProductUpdateAction> productUpdateActions, List<String> duplicateRecordsLog)
					throws InvalidInputCountryCodeException {
		
		String inputCountryUpperCase = productPriceRecord.getCountryCode().toUpperCase();
		String existingPriceID = currentPricesMap.get(inputCountryUpperCase);

		if (existingPriceID != null) {// Price already exists in CT and has no ProductChangePriceAction
										//associated to it
			productUpdateActions.add(ProductChangePriceActionBuilder
					.of()
					.priceId(existingPriceID)
					.price(createPriceDraft(inputCountryUpperCase, 
							getCurrencyISOfromCountry(inputCountryUpperCase), 
							productPriceRecord.getPrice())
					)
					.staged(false)
					.build());
			
			currentPricesMap.put(inputCountryUpperCase, null); // to check for any ProductPriceRecord which may come
																// later with same DataHubID and country
		} else {
			/*
			 * Duplicate Product Price Record (Same DataHubID and Country). Either it's a new
			 * price and already has a ProductAddPriceAction associated for it OR it's a
			 * pre-existing price price in CT but already has ProductChangePriceAction
			 * associated to it
			 */
			duplicateRecordsLog.add(ProductPriceServiceConstants.MSG_DUPLICATE_RECORDS + productPriceRecord);
			
		}
	}

	/**
	 * Returns Currency code compliant to ISO 4217 based on input country code
	 * 
	 * @param country code as per ISO 3166-1 alpha-2
	 * @return CurrencyCode
	 * @throws InvalidInputCountryCodeException
	 */
	public String getCurrencyISOfromCountry(String country) throws InvalidInputCountryCodeException {

		String currency;
		try {
			currency = CountryCurrencyCode.valueOf(country.toUpperCase()).getCode();

		} catch (IllegalArgumentException e) {
			throw new InvalidInputCountryCodeException(
					ProductPriceServiceConstants.MSG_EXCEPTION_INVALID_INPUT_COUNTRY + country);
		}
		return currency;
	}

	/**
	 * Creates and returns a map of prices already existing in CT Key =
	 * CountryISOCode Value = CT PriceID
	 * 
	 * @param productVariant
	 * @return
	 */
	public Map<String, String> createExistingPricesMap(ProductVariant productVariant) {
		return productVariant.getPrices().stream()
				.collect(Collectors.toMap(price -> price.getCountry().toUpperCase(), Price::getId));
	}

	/**
	 * Creates and returns a ProductAddPriceAction based on passed parameters
	 * 
	 * @param productVariantSKU
	 * @param country
	 * @param currency
	 * @param price
	 * @return
	 */
	public ProductAddPriceAction createProductAddPriceAction(String productVariantSKU, String country,
			String currency, String price) {
		return ProductAddPriceActionBuilder.of()
				.sku(productVariantSKU)
				.price(createPriceDraft(country, currency, price))
				.staged(false)
				.build();
	}

	/**
	 * This method checks that the input currency code (if any) is the one expected
	 * for the input country. If input currency code is not given, it passes.
	 * 
	 * @param inputCurrencyCode
	 * @param expectedCurrencyCode
	 * @param inputCountry
	 * @throws CurrencyCodeMismatchException
	 */
	public void checkInputCurrencyCode(String inputCurrencyCode, String expectedCurrencyCode, String inputCountry)
			throws CurrencyCodeMismatchException {
		if (inputCurrencyCode != null && !(inputCurrencyCode.isEmpty()) 
				&& !(inputCurrencyCode.equalsIgnoreCase(expectedCurrencyCode))) {
			throw new CurrencyCodeMismatchException(ProductPriceServiceConstants.CURRENCY + inputCurrencyCode
					+ ProductPriceServiceConstants.NOT_EXPECTED + inputCountry);
		}
	}

	/**
	 * Creates and returns a price draft with suitable MoneyDraftBuilder depending
	 * upon the number of digits after decimal in input price.
	 * 
	 * @param countryISOCode
	 * @param currencyISOCode
	 * @param priceString
	 */
	public PriceDraft createPriceDraft(String countryISOCode, String currencyISOCode, String priceString) {
		PriceDraft priceDraft = null;
		BigDecimal priceAmount = new BigDecimal(priceString);
		
		priceAmount = priceAmount.setScale(2, RoundingMode.HALF_UP);
		priceDraft = PriceDraftBuilder.of().country(countryISOCode)
				.channel(
						ChannelResourceIdentifierBuilder.of()
						.key(ProductPriceServiceConstants.CHANNEL_KEY)
						.build())
				.value(
						CentPrecisionMoneyDraftBuilder.of()
						.currencyCode(currencyISOCode)
						.centAmount(priceAmount.movePointRight(2).longValue())
						.build())
				.build();
		
		return priceDraft;
	}

}