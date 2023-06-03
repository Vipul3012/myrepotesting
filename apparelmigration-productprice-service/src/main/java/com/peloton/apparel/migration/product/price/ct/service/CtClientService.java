package com.peloton.apparel.migration.product.price.ct.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.commercetools.api.client.ConcurrentModificationMiddleware;
import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import com.commercetools.api.defaultconfig.ServiceRegion;
import com.google.gson.Gson;
import com.peloton.apparel.migration.product.price.pojo.AwsSecretCTCredentials;

import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import lombok.AllArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleWithWebIdentityCredentialsProvider;
import software.amazon.awssdk.services.sts.auth.StsWebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.awssdk.services.sts.model.StsException;

/**
 * A class that is used to create a java client for commercetools platform
 * @author priyanka.wadekar@onepeloton.com
 *
 */
@Component
@AllArgsConstructor
public class CtClientService {
	
	private Environment environment;
	private Gson gson;
	private static Logger logger = LoggerFactory.getLogger(CtClientService.class);

	/**
	 * A method that creates a client object to interact with commercetools platform
	 * @return - API root client object 
	 * @throws IOException
	 */
	public ProjectApiRoot createApiClient()  {
		
		
		final String clientId;
		final String projectKey;
		final String clientSecret;
		final AwsSecretCTCredentials awsSercretCTCredentials;
		
		
		if(environment.getActiveProfiles()[0].equalsIgnoreCase("sandbox") || 
				environment.getActiveProfiles()[0].equalsIgnoreCase("test")) {
			logger.info("log-test-env-sandbox");
			
			clientId = environment.getRequiredProperty("ctp.clientId");
			projectKey = environment.getRequiredProperty("ctp.projectKey");
			clientSecret = environment.getRequiredProperty("ctp.clientSecret");
			
		}
		//Under testing
		else if(environment.getActiveProfiles()[0].equalsIgnoreCase("stage")) {
			logger.info("log-test-env-stage");
			
			Region region = Region.of(environment.getRequiredProperty("aws.secret-manager.region", String.class));
			
			if(logger.isInfoEnabled()) {
				logger.info(String.format("log-test-environment-SM-Endpoint \"%s\"", environment.getProperty("APP_AWS_SECRETS_MANAGER_ENDPOINT", String.class)));
				logger.info(String.format("log-test-secret id \"%s\"", environment.getRequiredProperty("aws.secret-manager.secret.id", String.class)));
				logger.info(String.format("log-test-region id \"%s\" region metadata id \"%s\"", region.id(),  region.metadata().id()));				
			}
			/*
			logger.info("log-test-building-sts-client");
			StsClient stsClient = StsClient.builder()
		            .region(region)
		            .build();
			logger.info("log-test-building-completed-sts-client");
			
			SecretsManagerClient secretsManagerClient = null;
			
			try {
				if(logger.isInfoEnabled()) {
					logger.info(String.format("log-test-aws-temp.role.arn \"%s\"", environment.getRequiredProperty("aws-temp.role.arn", String.class)));
					logger.info(String.format("log-test-aws-temp.role.session.name \"%s\"", environment.getRequiredProperty("aws-temp.role.session.name", String.class)));
				}
				AssumeRoleRequest roleRequest = AssumeRoleRequest.builder()
		                .roleArn(environment.getRequiredProperty("aws-temp.role.arn", String.class))
		                .roleSessionName(environment.getRequiredProperty("aws-temp.role.session.name", String.class))
		                .build();

		        AssumeRoleResponse roleResponse = stsClient.assumeRole(roleRequest);
		        Credentials tempRoleCredentials = roleResponse.credentials();
		        
		        logger.info("log-test-building-SM-client");
		        secretsManagerClient = SecretsManagerClient.builder()
						.credentialsProvider(
								StaticCredentialsProvider.create(
										AwsSessionCredentials.create(
												tempRoleCredentials.accessKeyId(),
												tempRoleCredentials.secretAccessKey(),
												tempRoleCredentials.sessionToken()))
								)
						.region(region)
						.build();
		        logger.info("log-test-building-completed-SM-client");
			}
			catch (StsException e) {
				logger.info(e.awsErrorDetails().errorMessage());
	            logger.info("log-test-Above was awsErrorDetails().errorMessage(), below is e.getMessage()");
	            logger.info(e.getMessage());
	            logger.warn("log-test-secrets-manager-exception", e);
	            logger.info("log-test-Exiting Catch StsException");
	            System.exit(1);
		    }
			finally {
				stsClient.close();
			}
			*/
			
			
			SecretsManagerClient secretsManagerClient = SecretsManagerClient.builder()
//					.credentialsProvider(
//							StsWebIdentityTokenFileCredentialsProvider
//							.builder()
//							.stsClient(
//									StsClient.builder()
//									.region(region)
//									.build())
//							.roleSessionName("PriceServiceStageSessionRoleName")
//							.build())
							
//							WebIdentityTokenFileCredentialsProvider
//							.builder()
//							.roleSessionName("PriceServiceStageSessionRoleName")
//							.build())
					.region(region)
					.build();
			
			try {
				GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
						.secretId(environment.getRequiredProperty("aws.secret-manager.secret.id", String.class))
						.build();
				
				GetSecretValueResponse secretValueResponse = secretsManagerClient.getSecretValue(getSecretValueRequest);
				if(logger.isInfoEnabled()) {
					logger.info(String.format("log-test-return_value %s", secretValueResponse.secretString()));
				}
				
				awsSercretCTCredentials = gson.fromJson(secretValueResponse.secretString(), AwsSecretCTCredentials.class);
				
				if(logger.isInfoEnabled()) {
					logger.info(String.format("projectKey \"%s\" client ID \"%s\""
							+ " client Secret \"%s\" auth Url \"%s\" api Url \"%s\" scopes \"%s\"",
							awsSercretCTCredentials.getProjectKey(), 
							awsSercretCTCredentials.getClientId(),
							awsSercretCTCredentials.getClientSecret(),
							awsSercretCTCredentials.getAuthUrl(),
							awsSercretCTCredentials.getApiUrl(),
							awsSercretCTCredentials.getScopes())); 
				}
			}
			catch (SecretsManagerException e) {
	            logger.info(e.awsErrorDetails().errorMessage());
	            logger.info("log-test-Above was awsErrorDetails().errorMessage(), below is e.getMessage()");
	            logger.info(e.getMessage());
	            logger.warn("log-test-secrets-manager-exception", e);
	            logger.info("log-test-Exiting Catch SecretsManagerException");
	            System.exit(1);
	        }
			finally {
				secretsManagerClient.close();
			}
			
			clientId = "Client Dummy";
			projectKey = "Key Dummy";
			clientSecret = "Client Dummy";
		}
		
		else {
			throw new InvalidConfigurationPropertyValueException("spring.profiles.active", environment.getActiveProfiles()[0], "Unexpected active spring profile.");
		}
		
		
		return  ApiRootBuilder.of()
				.withMiddleware(ConcurrentModificationMiddleware.of(20))
				.withRetryMiddleware(10)
				.defaultClient(ClientCredentials.of()
						.withClientId(clientId).withClientSecret(clientSecret)
						.build(), ServiceRegion.GCP_US_CENTRAL1)
				.build(projectKey);
	}
}