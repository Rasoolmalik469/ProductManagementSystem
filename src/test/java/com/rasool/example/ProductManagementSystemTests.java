package com.rasool.example;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import com.rasool.example.entity.Product;

/**
 * @author Rasool Malik Vempalli
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductManagementSystemTests {

	@LocalServerPort
	private int port;

	private String baseUrl = "http://localhost";

	private static RestTemplate restTemplate;

	@Autowired
	private TestH2Repository h2Repository;

	@BeforeAll
	public static void init() {
		restTemplate = new RestTemplate();
	}

	@BeforeEach
	public void setup() {
		baseUrl = baseUrl.concat(":").concat(port + "");

	}

	@Test
	public void testAddProduct() {
		Product product = new Product("headset", 2, 7999);
		Product response = restTemplate.postForObject(baseUrl + "/addProduct", product, Product.class);

		assertAll(() -> assertNotNull(response), () -> assertEquals("headset", response.getName()),
				() -> assertEquals(2, response.getQuantity()), () -> assertEquals(7999, response.getPrice()),
				() -> assertNotNull(response.getId()));
		assertEquals(1, h2Repository.findAll().size());
	}

	@Test
	@Sql(statements = "INSERT INTO PRODUCT_TBL (id, name, quantity, price) VALUES (4, 'AC', 1, 34000)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM PRODUCT_TBL WHERE name='AC'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testGetProducts() {
		List<Product> products = restTemplate.getForObject(baseUrl + "/products", List.class);
		assertEquals(1, products.size());

		Map<String, Object> productMap = (Map<String, Object>) products.get(0);
		assertAll(() -> assertEquals(4, productMap.get("id")), () -> assertEquals("AC", productMap.get("name")),
				() -> assertEquals(1, productMap.get("quantity")),
				() -> assertEquals(34000.0, productMap.get("price")));
	}

	@Test
	@Sql(statements = "INSERT INTO PRODUCT_TBL (id, name, quantity, price) VALUES (1, 'CAR', 1, 334000)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM PRODUCT_TBL WHERE id=1", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testFindProductById() {
		Product product = restTemplate.getForObject(baseUrl + "/productById/{id}", Product.class, 1);
		assertNotNull(product);
		assertAll(() -> assertEquals(1, product.getId()), () -> assertEquals("CAR", product.getName()),
				() -> assertEquals(1, product.getQuantity()), () -> assertEquals(334000, product.getPrice()));
	}

	@Test
	@Sql(statements = "INSERT INTO PRODUCT_TBL (id, name, quantity, price) VALUES (2, 'shoes', 1, 999)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM PRODUCT_TBL WHERE id=2", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testUpdateProduct() {
		Product newProductDetails = new Product("new shoes", 2, 1999);
		restTemplate.put(baseUrl + "/update/{id}", newProductDetails, 2);

		Product updatedProduct = h2Repository.findById(2).orElse(null);
		assertNotNull(updatedProduct);
		assertAll(() -> assertEquals("new shoes", updatedProduct.getName()),
				() -> assertEquals(2, updatedProduct.getQuantity()),
				() -> assertEquals(1999, updatedProduct.getPrice()));
	}

	@Test
	@Sql(statements = "INSERT INTO PRODUCT_TBL (id, name, quantity, price) VALUES (8, 'books', 5, 1499)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	public void testDeleteProduct() {
		assertEquals(2, h2Repository.findAll().size());
		restTemplate.delete(baseUrl + "/delete/{id}", 8);
		assertEquals(1, h2Repository.findAll().size());

		Optional<Product> deletedProduct = h2Repository.findById(8);
		assertFalse(deletedProduct.isPresent());
	}

}
