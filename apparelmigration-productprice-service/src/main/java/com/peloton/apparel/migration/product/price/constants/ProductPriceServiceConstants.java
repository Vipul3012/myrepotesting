package com.peloton.apparel.migration.product.price.constants;

/**
 * A class that is used to declare common constants being used
 * 
 * @author priyanka.wadekar@onepeloton.com
 *
 */
public class ProductPriceServiceConstants {

	private ProductPriceServiceConstants() {
	}

	public static final String BRACES = "{}";
	public static final String VAL = "val";
	public static final String BODY = "${body}";
	public static final String CHANNEL_KEY = "web";
	public static final String CURRENCY = "Currency- ";
	public static final String NOT_EXPECTED = " not expected for Country- ";
	public static final String LEGACY_OPTION_ID = "legacy-option-id";
	public static final String BODYGRID_IS_GRID = "${body.grid} == 'grid'";
	public static final String BODY_GRID = "${body.grid}";
	public static final String HEADER_RAWRECORD = "${header.rawRecord}";
	public static final String RAWRECORD = "rawRecord";
	public static final String RAWRECORD_BODY = "Raw Record = ${body}";
	public static final String WITH_WHERE_VALUE = "masterData(current(variants(attributes(name=\"legacy-option-id\" and value= :val)) or masterVariant(attributes(name=\"legacy-option-id\" and value= :val))))";

	public static final String ENDPOINTID_FAILEDRECORDCSV = "FailedRecordCSV";
	public static final String ENDPOINTID_CTPRODUCT = "ProductPriceCTLoadRoute";

	public static final String SEDA_ENDPOINT_RAW_RECORDS = "seda:rawProductPriceTraffic";
	public static final String SEDA_ENDPOINT_AGGREGATED_RECORDS = "seda:aggregatedProductPriceTraffic";
	public static final String SEDA_FAILED_CSV_ROUTE_RECORDS = "seda:ProductPricefailedRawTraffic";

	public static final String MICROMETER_INCREMENT_FAILED_RECORDS_COUNTER = "micrometer:counter:numberOfRecordsFailedProcessing(Total)?increment=1";
	public static final String MICROMETER_INCREMENT_FAILED_RECORDS_VARIANT_NOT_FOUND_COUNTER = "micrometer:counter:numberOfRecordsFailedProcessingDueToProductVariantNotFoundException?increment=1";
	public static final String MICROMETER_INCREMENT_FAILED_RECORDS_EXECUTION_EXCEPTION_COUNTER = "micrometer:counter:numberOfRecordsFailedProcessingDueToExecutionException?increment=1";
	public static final String MICROMETER_INCREMENT_FAILED_RECORDS_INVALID_COUNTRY_COUNTER = "micrometer:counter:numberOfRecordsFailedProcessingDueToInvalidInputCountryCodeException?increment=1";
	public static final String MICROMETER_INCREMENT_FAILED_RECORDS_CURRENCY_MISMATCH_COUNTER = "micrometer:counter:numberOfRecordsFailedProcessingDueToCurrencyCodeMismatchException?increment=1";
	public static final String MICROMETER_INCREMENT_FAILED_RECORDS_NUMBER_FORMAT_EXCEPTION_COUNTER = "micrometer:counter:numberOfRecordsFailedProcessingDueToNumberFormatException?increment=1";
	public static final String MICROMETER_INCREMENT_FAILED_RECORDS_CONCURRENT_MODIFICATION_COUNTER = "micrometer:counter:numberOfRecordsFailedProcessingDueToConcurrentModificationException?increment=1";
	public static final String MICROMETER_INCREMENT_FAILED_RECORDS_ROOT_EXCEPTION_COUNTER = "micrometer:counter:numberOfRecordsFailedProcessingDueToRootException?increment=1";
	public static final String MICROMETER_INCREMENT_SUCCESSFULL_RECORDS_COUNTER = "micrometer:counter:numberOfPriceRecordsProcessedSuccessfully?increment=1";
	public static final String MICROMETER_INCREMENT_VALIDATION_FAILED_RECORDS_COUNTER = "micrometer:counter:numberOfRecordsFailedValidation?increment=1";
	public static final String MICROMETER_INCREMENT_FILES_UPLOADED_COUNTER = "micrometer:counter:numberOfFilesUploaded?increment=1";
	public static final String MICROMETER_INCREMENT_RAWRECORDS_MARSHALLED_COUNTER = "micrometer:counter:numberOfrawRecordsMarshalled?increment=1";
	public static final String MICROMETER_INCREMENT_RAWRECORDS_READ_COUNTER = "micrometer:counter:numberOfrawRecordsRead?increment=1";
	public static final String MICROMETER_INCREMENT_FILES_FAILED_READING_COUNTER ="micrometer:counter:numberOfFilesFailedReading?increment=1";
	
	public static final String MICROMETER_INCREMENT_PRICE_SYNC_INPUT_RECORDS = "micrometer:counter:numberOfRecordsReceived-PriceSyncService?increment=1";
	public static final String MICROMETER_INCREMENT_PRICE_SYNC_SUCCESSFULL_RECORDS = "micrometer:counter:numberOfRecordsSuccessfullyUpdated-PriceSyncService?increment=1";
	public static final String MICROMETER_INCREMENT_PRICE_SYNC_FAILED_RECORDS = "micrometer:counter:numberOfRecordsFailed-PriceSyncService?increment=1";
	
	public static final String MSG_FAILED_RECORD = "Failed record = ${header.rawRecord}";
	public static final String MSG_EXCEPTION_HANDLED = "Exception Handled (MalformedInputException)";
	public static final String MSG_FILE_NOT_PRESENT = "File is not present";
	public static final String MSG_SDKCLIENT_EXCEPTION_OCCURED = "SdkClientException occured while reading from s3";
	public static final String MSG_UNKNOWNHOST_EXCEPTION_OCCURED="UnknownHostException occured while reading from s3";
	public static final String MSG_RAWRECORDS_READING = "Reading from SEDA_ENDPOINT_RAW_RECORDS ${body}";
	public static final String MSG_AGGREGATED_READING = "Reading from SEDA_ENDPOINT_AGGREGATED_RECORDS ${body}";
	public static final String MSG_PRODUCTVARIANT_NOTFOUND = "ProductVariantNotFoundException Failed record = ${body}";
	public static final String MSG_EXECUTION_EXCEPTION = "ExecutionException (record failed while updating in CT) = ${body}";
	public static final String MSG_INVALIDINPUTCOUNTRY_EXCEPTION = "InvalidInputCountryCodeException Failed record = ${body}";
	public static final String MSG_CURRENCYCODEMISMATCH_EXCEPTION = "CurrencyCodeMismatchException Failed record = ${body}";
	public static final String MSG_NUMBERFORMAT_EXCEPTION = "NumberFormatException Failed record = ${body}";
	public static final String MSG_CONCURRENT_MODIFICATION_EXCEPTION = "ConcurrentModificationException Failed record = ${body}";
	public static final String MSG_FAILED_EXCEPTION = "Failed record (Root Exception in LoadRoute) = ${body}";
	public static final String MSG_EXCEPTION_VARIANT_NOT_FOUND = "No variant found for following DataHub/Grid/Legacy Option ID- ";
	public static final String MSG_EXCEPTION_INVALID_INPUT_COUNTRY = "Invalid country code provided- ";
	public static final String MSG_RECORDS_SAME_ID = "Multiple records with same DataHub ID and Country. Ignoring the following record- {}";
	public static final String MSG_DUPLICATE_RECORDS = "Multiple records with same DataHub ID and Country. Ignoring the following record- ";
	
	public static final String SIMPLE_EXCHANGE_PROPERTY_SUCCESS_MESSAGE = "${exchangeProperty.successMessage}";
	public static final String SIMPLE_EXCHANGE_PROPERTY_DUPLICATE_RECORDS_LOG = "${exchangeProperty.duplicateRecordsLog}";
	
	public static final String LOG_PRICE_SYNC_PROCESSING_STARTED = "Processing started for the following message received from kafka price sync topic:-\n${body}";
	public static final String LOG_PRICE_SYNC_PROCESSING_COMPLETED = "Processing completed for the following record:-\n${body}";
	public static final String LOG_PRICE_SYNC_UNMARSHALLED_RECORD = "Unmarshalled Record:-\n${body}";
	
	public static final String ENDPOINT_KAFKA_PRICE_SYNC_INPUT = "kafka:{{kafka-topic-price-sync-input}}";
	public static final String ENDPOINT_KAFKA_PRICE_SYNC_FAILED_RECORDS = "kafka:{{kafka-topic-price-sync-failed-records}}";
	
	public static final String LITEREAL_DATAHUBID_APPEND = ", Datahub/Grid ID-";
	public static final String LITEREAL_VALIDATIONS_PASSED = "Validations passed";
	public static final String LITEREAL_VALIDATIONS_FAILED = "Validations failed";
	public static final String LITEREAL_UPDATE_FAILED = "Update failed";
	public static final String LITEREAL_PRICE = "Price";
	
	public static final String SYMBOL_COMMA_WITHSPACE = ", ";
	public static final String SYMBOL_HYPHEN_WITHSPACES = " - ";
	public static final String SYMBOL_SPACE = " ";
	
	public static final String EXCHANGE_PROPERTY_SUCCESS_MESSAGE = "successMessage";
	public static final String EXCHANGE_PROPERTY_DUPLICATE_RECORDS_LOG = "duplicateRecordsLog";

	public static final String ROUTEID_CSV_TO_SEDA = "ProductPriceDataExtraction";
	public static final String S3_ROUTEID_CSV_TO_SEDA = "ProductPriceExtractionAwsS3Route";
	public static final String ROUTEID_AGGREGATION_ROUTE = "ProductPricesAggregationRoute";
	public static final String ROUTEID_PRODUCT_PRICES_LOAD_ROUTE = "ProductPricesLoadRoute";
	public static final String ROUTEID_IAEXCEPTION = "ExceptionFailureHandler";
	public static final String ROUTEID_IAS3EXCEPTION = "ProductPriceExtractionAwsS3RouteFailureHandler";
	public static final String ROUTEID_FAILED_RECORDS_FILE = "ProductpriceFailedRecordsRoute";
	public static final String ROUTEID_INVALID_INPUTFILE_EXCEPTION = "invaldInputFileRouteID";
	public static final String ROUTEID_S3_INVALID_INPUTFILE_EXCEPTION = "invaldInputFileRouteIDS3";
	public static final String ROUTEID_AWSS3_SDKCLIENT_EXCEPTION = "AwsS3SdkClientFailurehandler";
	public static final String ROUTEID_AWSS3_UNKNOWNHOST_EXCEPTION ="AwsS3UnknownHostFailurehandler";
	public static final String ROUTEID_PRODUCTVARIANT_HANDLER = "ProductVariantNotFoundExceptionHandler";
	public static final String ROUTEID_CTEXCEPTION = "CTExecutionExceptionHandler";
	public static final String ROUTEID_INVALID_COUNTRY_CODE_EXCEPTION = "InvalidInputCountryCodeExceptionHandler";
	public static final String ROUTEID_CURRENCY_CODE_MISMATCH_EXCEPTION = "CurrencyCodeMismatchExceptionHandler";
	public static final String ROUTEID_NUMBER_FORMAT_EXCEPTION = "NumberFormatExceptionHandler";
	public static final String ROUTEID_CONCURRENT_MODIFICATION_EXCEPTION = "concurrentModificationExceptionHandler";
	public static final String ROUTEID_ROOT_EXCEPTION = "RootExceptionHandler";
	public static final String ROUTEID_PRODUCT_PRICE_SYNC_SERVICE = "ProductPriceSyncService";
	public static final String ESCAPE_CHAR = "\n";
	public static final String STRING_PRICE_SUCCESS_MSG = "Successfully set prices for variant SKU-";

	public static final String TEST_VARIANT_NOT_FOUND_EXPECTED_MESSAGE = "Exception is occured as no variant is found with the provided DataHub ID";
	public static final String TEST_CURRENCY_CODE_MISMATCH_EXPECTED_MESSAGE = "Exception is occured as currency code is not expected for the input country";
	public static final String TEST_INVALID_INPUT_COUNTRY_CODE_EXPECTED_MESSAGE = "Exception is occured as invalid or unexpected country code is provided in input";
	public static final String TEST_PRODUCT_PRICE_DATA_MIGRATION_EXPECTED_MESSAGE = "This is the parent exception for all the custom exceptions of this service";

	public static final long LONG_TIMEOUT_5000L = 5000L;
	public static final int INT_MAX_REDELIVERIES = 10;
	public static final long LONG_DELAY_1000L = 1000L;
	public static final int INT_CONCURRENT_CONSUMERS = 20;

}