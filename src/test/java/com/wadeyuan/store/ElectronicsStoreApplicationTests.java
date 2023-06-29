package com.wadeyuan.store;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class ElectronicsStoreApplicationTests {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void testCreateProduct() throws Exception {
		// Arrange
		String requestBody = "{\"name\": \"New Product\", \"price\": 9.99}";

		// Act
		mockMvc.perform(MockMvcRequestBuilders.post("/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.header().string("Location", Matchers.containsString("/products/")));
	}

	@Test
	void testGetProductById() throws Exception {
		// Arrange
		long productId = 1;

		// Act
		mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", productId))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(productId));
	}

	@Test
	void testGetProductByNonExistingId() throws Exception {
		// Arrange
		long productId = -999;

		// Act
		mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", productId))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	void testUpdateProduct() throws Exception {
		// Arrange
		long productId = 1;
		String requestBody = "{\"name\": \"Updated Product\", \"price\": 19.99}";

		// Act
		mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", productId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Product"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.price").value(19.99));
	}

	@Test
	void testDeleteProduct() throws Exception {
		// Arrange
		long productId = 1;

		// Act
		mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", productId))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
}

