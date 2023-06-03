package com.peloton.apparel.migration.product.price;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@RequestMapping
/**
 * This class doing the purpose of healthcheck 
 * @author shinu.pillai
 *
 */
public class HealthCheckControiller {

	private static Logger logger = LoggerFactory.getLogger(HealthCheckControiller.class);

	@GetMapping(path = "/ping", produces = "application/json")

	public ResponseEntity<?> create(
			@RequestParam(value = "name", defaultValue = "World") String name) {
		int customHttpStatusValue = 200;
		logger.info("inside the controller method Received the request" );

		return ResponseEntity.status(customHttpStatusValue).body("Hello");
	}
}
