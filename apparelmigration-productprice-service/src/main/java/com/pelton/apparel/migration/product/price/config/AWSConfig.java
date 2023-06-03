package com.pelton.apparel.migration.product.price.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.google.gson.Gson;
import com.peloton.apparel.migration.product.price.pojo.AwsCredentials;


/**
 * This class for retrieve aws credentials from AWS environment
 * 
 * @author shinu.pillai
 *
 */
@Configuration
public class AWSConfig {

	private Gson gson = new Gson();
	@Value("${aws.credentials.access-key}")
	private String accesskey;

	@Value("${aws.credentials.secret-key}")
	private String secretKey;

	@Value("${aws.secretname}")
	private String secretName;

	@Value("${aws.secretname}")
	private String region;

	/**
	 * This medthod get Secret details from AWS environment
	 * 
	 * @return AwsSecrets
	 */
	private AwsCredentials getSecret() {

		// Create a Secrets Manager client
		AWSSecretsManager awsSecretMgr = AWSSecretsManagerClientBuilder
				.standard().withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(
						new BasicAWSCredentials(accesskey, secretKey)))
				.build();
		GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
				.withSecretId(secretName);
		GetSecretValueResult getSecretValueResult = null;
		try {
			getSecretValueResult = awsSecretMgr
					.getSecretValue(getSecretValueRequest);

		} catch (Exception e) {
			e.getMessage();
			throw e;
		}
		if (getSecretValueResult.getSecretString() != null) {
			String secret = getSecretValueResult.getSecretString();
			return gson.fromJson(secret, AwsCredentials.class);
		}
		return null;

	}

}
