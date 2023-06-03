package com.peloton.apparel.migration.product.price.ct.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.commercetools.api.client.ProjectApiRoot;
import com.google.gson.Gson;


@SpringBootTest(classes = { CtClientService.class, Gson.class })
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CTClientServiceTest {

	@Autowired
	private CtClientService clientService;
	
	@Test
    void testApiRoot() throws IOException {
        
        ProjectApiRoot root;
        
        root = clientService.createApiClient();
          assertNotNull(root);
    }
}
