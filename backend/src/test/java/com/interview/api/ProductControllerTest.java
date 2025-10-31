package com.interview.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.api.mapper.ProductMapper;
import com.interview.api.model.ProductDto;
import com.interview.dao.model.Product;
import com.interview.service.ProductService;
import com.interview.util.Money;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "spring.sql.init.mode=never")
class ProductControllerTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ProductService productService;

	@Test
	void CreateAndFetch() throws Exception {
		final ProductDto sourceDto = randomNewDto();

		final String rawCreateResponse = mvc
				.perform(post("/api/product").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(sourceDto)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		final ProductDto parsedCreateResponse = objectMapper.readValue(rawCreateResponse, ProductDto.class);

		final String rawGetResponse = mvc
				.perform(get("/api/product/{id}", parsedCreateResponse.getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		final ProductDto parsedGetResponse = objectMapper.readValue(rawGetResponse, ProductDto.class);

		// check that creation response and subsequent fetch are identical
		assertEquals(parsedCreateResponse, parsedGetResponse);
		assertNotNull(parsedGetResponse.getCreatedDate());
		assertNotNull(parsedGetResponse.getModifiedDate());

		// check that created entity matches source dto
		parsedGetResponse.setId(null);
		parsedGetResponse.setCreatedDate(null);
		parsedGetResponse.setModifiedDate(null);
		parsedGetResponse.setVersion(null);
		assertEquals(sourceDto, parsedGetResponse);
	}

	@Test
	void FetchPagedEmpty() throws Exception {
		mvc.perform(get("/api/product").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isEmpty()).andExpect(jsonPath("$.page.totalElements").value(0))
				.andExpect(jsonPath("$.page.totalPages").value(0));
	}

	@Test
	void FetchPagedOneProduct() throws Exception {
		final Product product = productService.create(ProductMapper.INSTANCE.dtoToProduct(randomNewDto()));
		productService.create(ProductMapper.INSTANCE.dtoToProduct(randomNewDto()));

		mvc.perform(get("/api/product?size=1").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(product.getId()))
				.andExpect(jsonPath("$.page.totalElements").value(2)).andExpect(jsonPath("$.page.totalPages").value(2));
	}

	@Test
	void FetchPagedDeletedIncluded() throws Exception {
		final Product product = productService.create(ProductMapper.INSTANCE.dtoToProduct(randomNewDto()));
		productService.deleteById(product.getId());

		mvc.perform(get("/api/product?includeDeleted=true").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(product.getId()))
				.andExpect(jsonPath("$.page.totalElements").value(1)).andExpect(jsonPath("$.page.totalPages").value(1));
	}

	@Test
	void FetchPagedDeletedExcluded() throws Exception {
		final Product product1 = productService.create(ProductMapper.INSTANCE.dtoToProduct(randomNewDto()));
		final Product product2 = productService.create(ProductMapper.INSTANCE.dtoToProduct(randomNewDto()));
		productService.deleteById(product2.getId());

		mvc.perform(get("/api/product").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(product1.getId()))
				.andExpect(jsonPath("$.page.totalElements").value(1)).andExpect(jsonPath("$.page.totalPages").value(1));
	}

	@Test
	void FetchMissingProduct() throws Exception {
		mvc.perform(
				get("/api/product/{id}", RandomUtils.insecure().randomLong(1, 10)).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void FetchDeletedProductIncluded() throws Exception {
		final Product product = productService.create(ProductMapper.INSTANCE.dtoToProduct(randomNewDto()));
		productService.deleteById(product.getId());

		mvc.perform(get("/api/product/{id}?includeDeleted=true", product.getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(product.getId()));
	}

	@Test
	void FetchDeletedProductExcluded() throws Exception {
		final Product product = productService.create(ProductMapper.INSTANCE.dtoToProduct(randomNewDto()));
		productService.deleteById(product.getId());

		mvc.perform(get("/api/product/{id}?includeDeleted=false", product.getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void UpdateProduct() throws Exception {
		final Product product = productService.create(ProductMapper.INSTANCE.dtoToProduct(randomNewDto()));
		product.setName("better name");

		mvc.perform(post("/api/product/{id}", product.getId()).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(product)))
				.andExpect(status().isOk());

		assertEquals("better name", productService.getById(product.getId()).getName());
	}

	@Test
	void DeleteProduct() throws Exception {
		final Product product = productService.create(ProductMapper.INSTANCE.dtoToProduct(randomNewDto()));

		mvc.perform(delete("/api/product/{id}", product.getId())).andExpect(status().isOk());

		assertNotNull(productService.getById(product.getId()).getDeletedDate());
	}

	private static ProductDto randomNewDto() {
		final String nonce = RandomStringUtils.insecure().nextAlphanumeric(5);
		final long price = RandomUtils.insecure().randomLong(100, 1000);
		return ProductDto.builder().name("produce name " + nonce).description("product description " + nonce)
				.price(new Money(price)).build();
	}
}