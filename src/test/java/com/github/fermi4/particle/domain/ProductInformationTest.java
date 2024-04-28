package com.github.fermi4.particle.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.fermi4.particle.api.domain.ProductInformation;
import com.github.fermi4.particle.api.domain.resource.ProductApiResponse;
import com.github.fermi4.particle.api.serde.ProductApiResponseDeserializer;
import com.github.fermi4.particle.api.serde.ProductInformationDeserializer;

public class ProductInformationTest {

    @SuppressWarnings("unchecked")
	@Test
    public void testProductInformationJsonDeserialization() throws JsonMappingException, JsonProcessingException {
        String json = "{\"id\":12345,\"platform_id\":10,\"name\":\"My Product\",\"slug\":\"my-product\"," +
                "\"description\":\"My test product\",\"subscription_id\":1234,\"user\":\"me@example.com\"," +
                "\"groups\":[\"one\",\"two\"]," +
                "\"settings\":{\"location\":{},\"known_application\":{\"opt_in\":true},\"quarantine\":false}}";

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addDeserializer(ProductInformation.class, new ProductInformationDeserializer()));

        ProductInformation productInfo = mapper.readValue(json, ProductInformation.class);
        assertNotNull(productInfo);
        assertEquals(12345, productInfo.getId());
        assertEquals(10, productInfo.getPlatformId());
        assertEquals("My Product", productInfo.getName());
        assertEquals("my-product", productInfo.getSlugOrProductId());
        assertEquals("My test product", productInfo.getDescription());
        assertEquals(1234, productInfo.getSubscriptionId());
        assertEquals("me@example.com", productInfo.getUser());
        assertEquals(2, productInfo.getGroups().size());
        assertEquals("one", productInfo.getGroups().get(0));
        assertEquals("two", productInfo.getGroups().get(1));
        assertEquals(3, productInfo.getSettings().size());
        assertEquals(true, Boolean.valueOf((String)((Map<String, Object>) productInfo.getSettings().get("known_application")).get("opt_in")));
        assertEquals(false, Boolean.valueOf((String)productInfo.getSettings().get("quarantine")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProductApiResponseJsonDeserialization() throws JsonMappingException, JsonProcessingException {
        
        String json = "{\n" +
                "  \"products\": [{\n" +
                "    \"id\": 12345,\n" +
                "    \"platform_id\": 10,\n" +
                "    \"name\": \"My Product\",\n" +
                "    \"slug\": \"my-product\",\n" +
                "    \"description\": \"My test product\",\n" +
                "    \"subscription_id\": 1234,\n" +
                "    \"user\": \"me@example.com\",\n" +
                "    \"groups\": [\n" +
                "      \"one\",\n" +
                "      \"two\"\n" +
                "    ],\n" +
                "    \"settings\": {\n" +
                "        \"location\": {},\n" +
                "        \"known_application\": {\n" +
                "            \"opt_in\": true\n" +
                "        },\n" +
                "        \"quarantine\": false\n" +
                "    }\n" +
                "  }]\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addDeserializer(ProductApiResponse.class, new ProductApiResponseDeserializer()));

        ProductApiResponse apiResponse = mapper.readValue(json, ProductApiResponse.class);
        ProductInformation productInfo = apiResponse.getProducts().get(0);

        assertNotNull(productInfo);
        assertEquals(12345, productInfo.getId());
        assertEquals(10, productInfo.getPlatformId());
        assertEquals("My Product", productInfo.getName());
        assertEquals("my-product", productInfo.getSlugOrProductId());
        assertEquals("My test product", productInfo.getDescription());
        assertEquals(1234, productInfo.getSubscriptionId());
        assertEquals("me@example.com", productInfo.getUser());
        assertEquals(2, productInfo.getGroups().size());
        assertEquals("one", productInfo.getGroups().get(0));
        assertEquals("two", productInfo.getGroups().get(1));
        assertEquals(3, productInfo.getSettings().size());
        assertEquals(true, Boolean.valueOf((String)((Map<String, Object>) productInfo.getSettings().get("known_application")).get("opt_in")));
        assertEquals(false, Boolean.valueOf((String)productInfo.getSettings().get("quarantine")));
    }

}
