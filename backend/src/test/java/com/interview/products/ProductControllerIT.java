package com.interview.products;

import com.interview.products.api.BulkProductResponse;
import com.interview.products.api.CreateProductParams;
import com.interview.products.api.ProductResponse;
import com.interview.products.api.ReserveProductParams;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class ProductControllerIT
{

    @Autowired
    private ProductController productController;
    @Test
    public void createServiceProduct() {
        ProductResponse response = createTireRotationProduct();
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getId());
        Assert.assertFalse(response.getQuantity().isPresent());
    }

    @Test
    public void createPhysicalProduct() {
        ProductResponse response = createMotorOilProduct();
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getId());
        Assert.assertTrue(response.getQuantity().isPresent());
    }

    @Test
    public void testGetProduct() {
        ProductResponse createResponse = createMotorOilProduct();
        ProductResponse getResponse = productController.getProduct(createResponse.getId());
        Assert.assertEquals(createResponse.getId(), getResponse.getId());
        Assert.assertEquals(createResponse.getName(), getResponse.getName());
        Assert.assertEquals(createResponse.getCurrency(), getResponse.getCurrency());
        Assert.assertEquals(0, createResponse.getPrice().compareTo(getResponse.getPrice()));
        Assert.assertEquals(createResponse.getQuantity(), getResponse.getQuantity());
    }

    @Test
    public void testReserveProduct() {
        ProductResponse createResponse = createMotorOilProduct();
        ReserveProductParams reserveProductParams = new ReserveProductParams(createResponse.getId(), 3);
        ProductResponse reserveProductResponse = productController.reserveProduct(reserveProductParams);
        Assert.assertEquals((long) createResponse.getQuantity().get() - 3, (long) reserveProductResponse.getQuantity().get());
    }


    @Test(expected = ProductNotFoundException.class)
    public void testGetProductNotFound() {
        productController.getProduct(UUID.randomUUID());
    }

    @Test
    public void testDeleteProduct() {
        ProductResponse productResponse = createMotorOilProduct();
        productController.deleteProduct(productResponse.getId());
        boolean deleteSucceeded = false;
        try {
            productController.getProduct(productResponse.getId());
        } catch (ProductNotFoundException e) {
            deleteSucceeded = true;
        }
        Assert.assertTrue(deleteSucceeded);
    }

    @Test
    public void testGetProductsBatch() {
        ProductResponse createResponse = createMotorOilProduct();
        ProductResponse createResponse2 = createTireRotationProduct();
        BulkProductResponse bulkResponse = productController.getPageOfProducts(null, 1);
        Assert.assertEquals(bulkResponse.getProducts().size(), 1);
        ProductResponse firstProduct = bulkResponse.getProducts().get(0);
        Assert.assertTrue(firstProduct.getId().equals(createResponse.getId()) || firstProduct.getId().equals(createResponse2.getId()));
        BulkProductResponse bulkResponse2 = productController.getPageOfProducts(firstProduct.getId(), 1);
        Assert.assertEquals(bulkResponse2.getProducts().size(), 1);
        ProductResponse secondProduct = bulkResponse2.getProducts().get(0);
        Assert.assertTrue(secondProduct.getId().equals(createResponse.getId()) || secondProduct.getId().equals(createResponse2.getId()));
        Assert.assertNotEquals(firstProduct.getId(), secondProduct.getId());
    }

    private ProductResponse createMotorOilProduct() {
        CreateProductParams createProductParams =
                new CreateProductParams("motor oil", Currency.getInstance("USD"),
                        BigDecimal.valueOf(35.00), 10);
        return productController.createProduct(createProductParams);
    }

    private ProductResponse createTireRotationProduct() {
        CreateProductParams createProductParams =
                new CreateProductParams("tire rotation", Currency.getInstance("USD"),
                        BigDecimal.valueOf(40.00), null);
        return productController.createProduct(createProductParams);
    }

}
