package com.peloton.apparel.migration.product.price.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data

@AllArgsConstructor
public class AwsCredentials {

	private String clientId;
	private String clientSecret;
}
