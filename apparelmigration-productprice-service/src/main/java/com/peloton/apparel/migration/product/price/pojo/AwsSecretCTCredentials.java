package com.peloton.apparel.migration.product.price.pojo;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class AwsSecretCTCredentials {
	
	@SerializedName(value = "ctp.projectKey")
	private String projectKey;
	
	@SerializedName(value = "ctp.clientId")
	private String clientId;
	
	@SerializedName(value = "ctp.clientSecret")
	private String clientSecret;
	
	@SerializedName(value = "ctp.authUrl")
	private String authUrl;
	
	@SerializedName(value = "ctp.apiUrl")
	private String apiUrl;
	
	@SerializedName(value = "ctp.scopes")
	private String scopes;

}
