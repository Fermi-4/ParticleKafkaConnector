package com.github.fermi4.particle.api.domain.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.api.domain.ProductInformation;

public class ProductApiResponseTest {
    private ProductApiResponse response;
    private ProductInformation productInfo;

    @BeforeEach
    public void setUp() {
        response = new ProductApiResponse();
        productInfo = new ProductInformation();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(response);
        assertNotNull(response.getProducts());
        assertTrue(response.getProducts().isEmpty());
    }

    @Test
    public void testSettersAndGetters() {
        List<ProductInformation> products = new ArrayList<>();
        products.add(productInfo);

        response.setProducts(products);

        assertEquals(products, response.getProducts());
        assertEquals(1, response.getProducts().size());
        assertEquals(productInfo, response.getProducts().get(0));
    }

    @Test
    public void testModifyCollections() {
        productInfo.setId(1);
        response.getProducts().add(productInfo);

        assertEquals(1, response.getProducts().size());
        assertEquals(1, response.getProducts().get(0).getId());
    }

    @Test
    public void testProductApiResponseToString() {
        productInfo.setId(1);
        response.getProducts().add(productInfo);

        String toStringResult = response.toString();
        assertTrue(toStringResult.contains("products=["));
        assertTrue(toStringResult.contains("1"));
    }

    @Test
    public void testProductApiResponseEqualsAndHashCode() {
        ProductApiResponse response1 = new ProductApiResponse();
        ProductApiResponse response2 = new ProductApiResponse();

        productInfo.setId(1);
        response1.getProducts().add(productInfo);

        ProductInformation productInfo2 = new ProductInformation();
        productInfo2.setId(1);
        response2.getProducts().add(productInfo2);

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());

        response2.getProducts().clear();
        assertNotEquals(response1, response2);
        assertNotEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    public void testAddProducts() {
        ProductInformation product1 = new ProductInformation();
        product1.setId(1);
        ProductInformation product2 = new ProductInformation();
        product2.setId(2);

        response.getProducts().add(product1);
        response.getProducts().add(product2);

        assertEquals(2, response.getProducts().size());
        assertEquals(1, response.getProducts().get(0).getId());
        assertEquals(2, response.getProducts().get(1).getId());
    }

    @Test
    public void testRemoveProducts() {
        ProductInformation product1 = new ProductInformation();
        product1.setId(1);
        response.getProducts().add(product1);
        response.getProducts().remove(product1);
        assertTrue(response.getProducts().isEmpty());
    }

    @Test
    public void testEmptyProductsList() {
        response.setProducts(new ArrayList<>());
        assertTrue(response.getProducts().isEmpty());
    }

    @Test
    public void testProductApiResponseHashCodeWithEmptyList() {
        ProductApiResponse response1 = new ProductApiResponse();
        ProductApiResponse response2 = new ProductApiResponse();

        assertEquals(response1.hashCode(), response2.hashCode());
    }
}
