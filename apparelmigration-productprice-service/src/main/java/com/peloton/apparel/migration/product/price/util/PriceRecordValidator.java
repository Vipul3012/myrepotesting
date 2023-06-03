package com.peloton.apparel.migration.product.price.util;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.springframework.stereotype.Component;

import com.peloton.apparel.migration.product.price.constants.ProductPriceServiceConstants;
import com.peloton.apparel.migration.product.price.pojo.ProductPriceRecord;

/**
 * This utility class validates a given object of ProductPriceRecord
 * according to the javax constraints defined in the ProductPriceRecord class
 * and returns true if it is a valid record and false otherwise.
 * @author manmohan.shukla
 *
 */
@Component
public class PriceRecordValidator implements Predicate{
	private Validator validator;
	
	public PriceRecordValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Override
	public boolean matches(Exchange exchange) {
		ProductPriceRecord priceRecord = exchange.getIn().getBody(ProductPriceRecord.class);
		
		Set<ConstraintViolation<ProductPriceRecord>> violations = validator.validate(priceRecord);
		
		if(violations.isEmpty()) {
			priceRecord.setRecordStatus(ProductPriceServiceConstants.LITEREAL_VALIDATIONS_PASSED);
			return true;
		}
		else {
			priceRecord.setRecordStatus(ProductPriceServiceConstants.LITEREAL_VALIDATIONS_FAILED);
			priceRecord.setFailureReasons(
					violations.stream()
					.map(violation -> violation.getPropertyPath()
							+ ProductPriceServiceConstants.SYMBOL_SPACE 
							+ violation.getMessage())
					.collect(Collectors.joining(ProductPriceServiceConstants.SYMBOL_COMMA_WITHSPACE)));
			return false;
		}
	}
}
