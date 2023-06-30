package com.wadeyuan.store;

import com.wadeyuan.store.constants.DiscountType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ElectronicsStoreApplicationTests {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	@Order(1)
	void testCreateProduct() throws Exception {
		// Arrange
		String requestBodyA = "{\"name\": \"Product A\", \"price\": 9.99}";
		String requestBodyB = "{\"name\": \"Product B\", \"price\": 29.99}";

		// Act
		mockMvc.perform(MockMvcRequestBuilders.post("/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBodyA))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.header().string("Location", Matchers.containsString("/products/")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Product A"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.price").value(9.99));

		// Create another product
		mockMvc.perform(MockMvcRequestBuilders.post("/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBodyB))
				.andReturn();
	}

	@Test
	@Order(2)
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
	@Order(3)
	void testGetProductByNonExistingId() throws Exception {
		// Arrange
		long productId = -999;

		// Act
		mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", productId))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@Order(4)
	void testUpdateProduct() throws Exception {
		// Arrange
		long productId = 1;
		String requestBody = "{\"name\": \"Updated Product A\", \"price\": 11.99}";

		// Act
		mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", productId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Product A"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.price").value(11.99));
	}

	@Test
	@Order(5)
	void testApplySimpleDiscount() throws Exception {
		// Arrange
		long productId = 1;
		int requiredQuantity = 1;
		double percentageOff = 50.0;

		// Act
		mockMvc.perform(MockMvcRequestBuilders.post("/discounts/simpleDiscount/product/{productId}", productId)
					.param("requiredQuantity", String.valueOf(requiredQuantity))
					.param("percentageOff", String.valueOf(percentageOff)))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.header().string("Location", Matchers.containsString("/discounts/")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.discountType").value(String.valueOf(DiscountType.PERCENTAGE)));
	}

	@Test
	@Order(6)
	void testCreateShoppingCart() throws Exception {
		// Arrange
		String requestBody = "{\"items\": []}";

		// Act
		mockMvc.perform(MockMvcRequestBuilders.post("/shopping-carts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.header().string("Location", Matchers.containsString("/shopping-carts/")));
	}

	@Test
	@Order(7)
	void testAddProductToCart() throws Exception {
		// Arrange
		long shoppingCartId = 1;
		long productId = 1;
		int quantity = 2;

		// Act
		mockMvc.perform(MockMvcRequestBuilders.put("/shopping-carts/{shoppingCartId}/add/{productId}", shoppingCartId, productId)
					.param("quantity", String.valueOf(quantity)))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.items[0].quantity").value(String.valueOf(quantity)));
	}

	@Test
	@Order(8)
	void testRemoveProductFromCart() throws Exception {
		// Arrange
		long shoppingCartId = 1;
		long productId = 1;
		int quantity = 1;

		// Act
		mockMvc.perform(MockMvcRequestBuilders.put("/shopping-carts/{shoppingCartId}/remove/{productId}", shoppingCartId, productId)
						.param("quantity", String.valueOf(quantity)))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.items[0].quantity").value(String.valueOf(1)));

		// Act
		mockMvc.perform(MockMvcRequestBuilders.put("/shopping-carts/{shoppingCartId}/remove/{productId}", shoppingCartId, productId)
						.param("quantity", String.valueOf(quantity)))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.items").isEmpty());
	}

	@Test
	@Order(9)
	void testCalculateShoppingCartAmount() throws Exception {
		// Arrange
		long shoppingCartId = 1;
		long productAId = 1;
		long productBId = 2;
		int quantityA = 2;
		int quantityB = 1;
		// Expected original total amount: 2 * 11.99 + 1 * 29.99 = 53.97
		// Expected discount amount: 11.99 / 2 = 5.995
		// Expected final amount: 53.97 - 5.995 = 47.975
		mockMvc.perform(MockMvcRequestBuilders.put("/shopping-carts/{shoppingCartId}/add/{productId}", shoppingCartId, productAId)
				.param("quantity", String.valueOf(quantityA)));
		mockMvc.perform(MockMvcRequestBuilders.put("/shopping-carts/{shoppingCartId}/add/{productId}", shoppingCartId, productBId)
				.param("quantity", String.valueOf(quantityB)));

		// Act
		mockMvc.perform(MockMvcRequestBuilders.get("/shopping-carts/{shoppingCartId}/calculate", shoppingCartId))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.totalAmount").value(53.97))
				.andExpect(MockMvcResultMatchers.jsonPath("$.discountAmount").value(5.995))
				.andExpect(MockMvcResultMatchers.jsonPath("$.finalAmount").value(47.975));
	}

	@Test
	@Order(10)
	void testCreateComplexDiscount() throws Exception {
		// Arrange
		String requestBody = """
				{
					"requiredProduct": {
						"id": 2
					},
					"requiredQuantity": 1,
					"targetProduct": {
						"id": 1
					},
					"discountType": "AMOUNT",
					"discountValue": 15.0,
					"enabled": true
				}""";

		// Act
		mockMvc.perform(MockMvcRequestBuilders.post("/discounts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.header().string("Location", Matchers.containsString("/discounts/")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.discountType").value(String.valueOf(DiscountType.AMOUNT)));
	}

	@Test
	@Order(11)
	void testCalculateShoppingCartAmountWithMultipleDiscounts() throws Exception {
		// Arrange
		long shoppingCartId = 1;
		// Expected original total amount: 2 * 11.99 + 1 * 29.99 = 53.97
		// Expected discount amount: 15.0 (in above test case we've applied another discount rule with more amount)
		// Expected final amount: 53.97 - 15.0 = 38.97

		// Act
		mockMvc.perform(MockMvcRequestBuilders.get("/shopping-carts/{shoppingCartId}/calculate", shoppingCartId))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.totalAmount").value(53.97))
				.andExpect(MockMvcResultMatchers.jsonPath("$.discountAmount").value(15.0))
				.andExpect(MockMvcResultMatchers.jsonPath("$.finalAmount").value(38.97));
	}

	@Test
	@Order(12)
	void testClearShoppingCart() throws Exception {
		// Arrange
		long shoppingCartId = 1;

		// Act
		mockMvc.perform(MockMvcRequestBuilders.put("/shopping-carts/{shoppingCartId}/clear", shoppingCartId))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.items").isEmpty());
	}

	@Test
	@Order(13)
	void testDeleteShoppingCart() throws Exception {
		// Arrange
		long shoppingCartId = 1;

		// Act
		mockMvc.perform(MockMvcRequestBuilders.delete("/shopping-carts/{shoppingCartId}", shoppingCartId))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@Order(14)
	void testDeleteDiscount() throws Exception {
		// Arrange
		long discountIdA = 1;
		long discountIdB = 2;

		// Act
		mockMvc.perform(MockMvcRequestBuilders.delete("/discounts/{discountId}", discountIdA))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isOk());
		// Act
		mockMvc.perform(MockMvcRequestBuilders.delete("/discounts/{discountId}", discountIdB))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@Order(Integer.MAX_VALUE)
	void testDeleteProduct() throws Exception {
		// Arrange
		long productId = 1;

		// Act
		mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", productId))
				// Assert
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
}

