package com.peloton.apparel.migration.product.price.pojo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@CsvRecord(separator = ",")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class ProductPriceRecord {

	@DataField(required = true, pos = 1)
	@JsonProperty(index = 1)
	@NotBlank
	private String grid;

	@DataField(required = true, pos = 2)
	@JsonProperty(value = "country_code", index = 2)
	@NotBlank
	private String countryCode;

	@DataField(pos = 3)
	@JsonProperty(index = 3)
	private String currency;

	@DataField(required = true, pos = 4)
	@NotBlank
	@JsonProperty(index = 4)
	private String price;

	@DataField(required = true, pos = 5)
	@JsonProperty(value = "last_approved_price", index = 5)
	@NotNull
	private String lastApprovedPrice;
	
	@JsonProperty(index = 6)
	private String recordStatus;
	
	@JsonProperty(index = 7)
	private String failureReasons;
}
