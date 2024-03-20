package com.interview.controller;

import com.interview.domain.Invoice;
import com.interview.domain.Shop;
import com.interview.domain.Supplier;
import com.interview.domain.SupplierType;
import com.interview.exception.ResourceNotFoundException;
import com.interview.service.ShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ShopControllerTest {

    private static final SimpleGrantedAuthority API_USER_AUTHORITY = new SimpleGrantedAuthority("SCOPE_api:user");

    @MockBean
    private ShopService shopService;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private Shop shop;

    private Invoice invoice;
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        invoice = Invoice.builder().id(1L).description("Invoice description").amount(123.0).paid(true).build();
        supplier = Supplier.builder().id(1L).name("Supplier name").supplierType(SupplierType.AUTO_PARTS).build();
    }

    @Test
    void givenMissingJwtToken_whenGetRequestToShops_thenStatusIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/shops"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenInvalidJwtToken_whenGetRequestToShops_thenStatusIsForbidden() throws Exception {
        mockMvc.perform(get("/api/shops")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidJwtToken_whenGetRequestToShops_thenCorrectResponseReturned() throws Exception {
        var shop1 = createTestShop(1L, "Shop 1", "Location 1", Set.of(invoice), Set.of(supplier));
        var shop2 = createTestShop(2L, "Shop 2", "Location 2", Set.of(invoice), Set.of(supplier));
        var expectedContent = "[{\"id\":1,\"name\":\"Shop 1\",\"location\":\"Location 1\",\"invoices\":[{\"id\":1,\"description\":\"Invoice description\",\"amount\":123.0,\"paid\":true}],\"suppliers\":[{\"id\":1,\"name\":\"Supplier name\",\"supplierType\":\"AUTO_PARTS\"}]},{\"id\":2,\"name\":\"Shop 2\",\"location\":\"Location 2\",\"invoices\":[{\"id\":1,\"description\":\"Invoice description\",\"amount\":123.0,\"paid\":true}],\"suppliers\":[{\"id\":1,\"name\":\"Supplier name\",\"supplierType\":\"AUTO_PARTS\"}]}]";
        when(shopService.getAllShops()).thenReturn(List.of(shop1, shop2));
        var actualContent = mockMvc.perform(buildGetRequestWithJwtToken("/api/shops"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ShopController.class))
                .andExpect(handler().methodName("getAllShops"))
                .andReturn().getResponse().getContentAsString();
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void givenValidJwtTokenAndValidShopId_whenGetRequestToShopsWithId_thenCorrectResponseReturned() throws Exception {
        Shop shop = Shop.builder().id(1L).name("Test Shop").location("Location").invoices(Set.of(invoice)).suppliers(Set.of(supplier)).build();
        var expectedContent = "{\"id\":1,\"name\":\"Test Shop\",\"location\":\"Location\",\"invoices\":[{\"id\":1,\"description\":\"Invoice description\",\"amount\":123.0,\"paid\":true}],\"suppliers\":[{\"id\":1,\"name\":\"Supplier name\",\"supplierType\":\"AUTO_PARTS\"}]}";
        when(shopService.getShopById(1L)).thenReturn(shop);
        var actualContent = mockMvc.perform(buildGetRequestWithJwtToken("/api/shops/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ShopController.class))
                .andExpect(handler().methodName("getShopById"))
                .andReturn().getResponse().getContentAsString();
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void givenValidJwtTokenAndInvalidShopId_whenGetRequestToShopsWithId_thenInternalServerError() throws Exception {
        mockMvc.perform(buildGetRequestWithJwtToken("/api/shops/asf"))
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(ShopController.class))
                .andExpect(handler().methodName("getShopById"));
    }

    @Test
    void givenValidJwtToken_whenGetRequestToShopsWithInvalidId_thenStatusIsNotFound() throws Exception {
        when(shopService.getShopById(21L)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(buildGetRequestWithJwtToken("/api/shops/21"))
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(ShopController.class))
                .andExpect(handler().methodName("getShopById"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    @Test
    void givenValidJwtToken_whenGetRequestToShopsWithPagination_thenCorrectResponseReturned() throws Exception {
        var shop1 = createTestShop(1L, "Shop 1", "Location 1", Set.of(invoice), Set.of(supplier));
        var shop2 = createTestShop(2L, "Shop 2", "Location 2", Set.of(invoice), Set.of(supplier));
        var expectedContent = "[{\"id\":1,\"name\":\"Shop 1\",\"location\":\"Location 1\",\"invoices\":[{\"id\":1,\"description\":\"Invoice description\",\"amount\":123.0,\"paid\":true}],\"suppliers\":[{\"id\":1,\"name\":\"Supplier name\",\"supplierType\":\"AUTO_PARTS\"}]},{\"id\":2,\"name\":\"Shop 2\",\"location\":\"Location 2\",\"invoices\":[{\"id\":1,\"description\":\"Invoice description\",\"amount\":123.0,\"paid\":true}],\"suppliers\":[{\"id\":1,\"name\":\"Supplier name\",\"supplierType\":\"AUTO_PARTS\"}]}]";

        var page = mock(Page.class);
        when(shopService.getAllShops(any())).thenReturn(page);
        when(page.hasPrevious()).thenReturn(true);
        when(page.hasNext()).thenReturn(true);
        when(page.getNumber()).thenReturn(1);
        when(page.getSize()).thenReturn(10);
        when(page.getContent()).thenReturn(List.of(shop1, shop2));

        var actualContent = mockMvc.perform(buildGetRequestWithJwtToken("/api/shops/page?number=1&size=10"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ShopController.class))
                .andExpect(handler().methodName("getShopsPaginated"))
                .andExpect(header().stringValues("Link", "<http://localhost:8080/api/shops/page?number=0&size=10>; rel=\"prev\",<http://localhost:8080/api/shops/page?number=2&size=10>; rel=\"next\""))
                .andReturn().getResponse().getContentAsString();
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void givenValidJwtToken_whenCreateShop_thenCorrectResponseReturned() throws Exception {
        when(shopService.saveShop(any())).thenReturn(shop);
        when(shop.getId()).thenReturn(2L);

        mockMvc.perform(buildPostRequestWithJwtToken("/api/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Shop 1\",\"location\":\"Location 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "http://localhost:8080/api/shops/2"))
                .andExpect(handler().handlerType(ShopController.class))
                .andExpect(handler().methodName("createShop"));
    }

    @Test
    void givenValidJwtTokenAndInvalidContent_whenCreateShop_thenStatusIsBadRequest() throws Exception {
        when(shopService.saveShop(any())).thenReturn(shop);
        when(shop.getId()).thenReturn(2L);
        var expectedResponse = "{\"name\":\"Name is mandatory\",\"location\":\"Location is mandatory\"}";
        var actualResponse = mockMvc.perform(buildPostRequestWithJwtToken("/api/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"price\":\"123\",\"description\":\"Location 1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(ShopController.class))
                .andExpect(handler().methodName("createShop"))
                .andReturn().getResponse().getContentAsString();
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void givenValidJwtToken_whenUpdateShop_thenCorrectResponseReturned() throws Exception {
        when(shopService.updateShop(any())).thenReturn(shop);
        when(shop.getId()).thenReturn(1L);
        when(shop.getName()).thenReturn("Autovalex");
        when(shop.getLocation()).thenReturn("Dallas");
        var expectedContent = "{\"id\":1,\"name\":\"Autovalex\",\"location\":\"Dallas\",\"invoices\":[],\"suppliers\":[]}";
        var actualContent = mockMvc.perform(buildPutRequestWithJwtToken("/api/shops/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Autovalex\",\"location\":\"Dallas\",\"invoices\":[],\"suppliers\":[]}"))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ShopController.class))
                .andExpect(handler().methodName("updateShop"))
                .andReturn().getResponse().getContentAsString();
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void givenValidJwtTokenAndInvalidId_whenUpdateShop_thenStatusIsBadRequest() throws Exception {
        var expectedContent = "{\"errorMessage\":\"Missing ID or IDs don't match\"}";
        var actualContent = mockMvc
                .perform(buildPutRequestWithJwtToken("/api/shops/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":2,\"name\":\"Autovalex\",\"location\":\"Dallas\",\"invoices\":[],\"suppliers\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(ShopController.class))
                .andExpect(handler().methodName("updateShop"))
                .andReturn().getResponse().getContentAsString();
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void givenValidJwtTokenAndInvalidRequestBodyContent_whenUpdateShop_thenStatusIsBadRequest() throws Exception {
        var expectedContent = "{\"errorMessage\":\"Invoices and suppliers list should be provided\"}";
        var actualContent = mockMvc
                .perform(buildPutRequestWithJwtToken("/api/shops/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Autovalex\",\"location\":\"Dallas\",\"suppliers\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(ShopController.class))
                .andExpect(handler().methodName("updateShop"))
                .andReturn().getResponse().getContentAsString();
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void givenValidJwtTokenAndInvalidId_whenPartialUpdateShop_thenStatusIsBadRequest() throws Exception {
        var expectedContent = "{\"errorMessage\":\"Missing ID or IDs don't match\"}";
        var actualContent = mockMvc
                .perform(buildPatchRequestWithJwtToken("/api/shops/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":2,\"name\":\"Autovalex\",\"location\":\"Dallas\",\"invoices\":[],\"suppliers\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(ShopController.class))
                .andExpect(handler().methodName("partialUpdateShop"))
                .andReturn().getResponse().getContentAsString();
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void givenValidJwtToken_whenPartialUpdateShop_thenCorrectResponseReturned() throws Exception {
        when(shopService.getShopById(1L)).thenReturn(shop);
        when(shopService.saveShop(any())).thenReturn(shop);
        when(shop.getId()).thenReturn(1L);
        when(shop.getName()).thenReturn("Autovalex2");
        when(shop.getLocation()).thenReturn("New York");
        var expectedContent = "{\"id\":1,\"name\":\"Autovalex2\",\"location\":\"New York\",\"invoices\":[],\"suppliers\":[]}";

        var actualContent = mockMvc
                .perform(buildPatchRequestWithJwtToken("/api/shops/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\": \"Autovalex2\",\"location\":\"New York\",\"invoices\": null,\"suppliers\": null}"))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ShopController.class))
                .andExpect(handler().methodName("partialUpdateShop"))
                .andReturn().getResponse().getContentAsString();
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void givenValidJwtToken_whenDeleteShop_thenStatusIsNoContent() throws Exception {
        mockMvc.perform(delete("/api/shops/1")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(API_USER_AUTHORITY)))
                .andExpect(status().isNoContent())
                .andExpect(handler().handlerType(ShopController.class))
                .andExpect(handler().methodName("deleteShop"));
    }

    private MockHttpServletRequestBuilder buildGetRequestWithJwtToken(String url) {
        return get(url)
                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(API_USER_AUTHORITY));
    }

    private MockHttpServletRequestBuilder buildPostRequestWithJwtToken(String url) {
        return post(url)
                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(API_USER_AUTHORITY));
    }

    private MockHttpServletRequestBuilder buildPutRequestWithJwtToken(String url) {
        return put(url)
                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(API_USER_AUTHORITY));
    }

    private MockHttpServletRequestBuilder buildPatchRequestWithJwtToken(String url) {
        return patch(url)
                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(API_USER_AUTHORITY));
    }

    private Shop createTestShop(Long id, String name, String location, Set<Invoice> invoices, Set<Supplier> suppliers) {
        return Shop.builder().id(id).name(name).location(location).invoices(invoices).suppliers(suppliers).build();
    }
}
