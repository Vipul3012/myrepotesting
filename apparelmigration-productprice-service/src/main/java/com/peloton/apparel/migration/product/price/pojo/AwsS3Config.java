package com.peloton.apparel.migration.product.price.pojo;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import lombok.Data;

/**
 * A class that do the functionality of AWS s3client configuration
 * 
 * @author shinu.pillai@onepeloton.com
 *
 */
@Data
@Configuration
public class AwsS3Config {

	private AmazonS3 s3;

	@PostConstruct
	private void refresh() {
		setS3(AmazonS3ClientBuilder.standard()
				.withRegion(Regions.DEFAULT_REGION).build());

	}

}
