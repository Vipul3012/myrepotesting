package com.peloton.apparel.migration.product.price;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductPriceApplicationTest {
	
	@Mock
	private SpringApplicationBuilder springApplicationBuilder;
	
	@SuppressWarnings("unused")
	@Test
	void contextLoads() {
		given:{
			ProductPriceApplication app = new ProductPriceApplication();
			//the Springboot application configuration is correct 
		}
		when:{
			// the Springboot Test Context is loaded successfully 
			
		}
		then:{
			//Test using the dummy assert to check if the control reaches here
			assertTrue(true);
		}
	}
	
	@Test
	void contextLoaded() {
		ProductPriceApplication.main(new String[] {});
		assertNotNull(getClass());
	}
}
