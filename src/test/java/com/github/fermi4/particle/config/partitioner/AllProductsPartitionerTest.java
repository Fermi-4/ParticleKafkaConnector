package com.github.fermi4.particle.config.partitioner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.errors.ConnectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.github.fermi4.particle.v1.ParticleClient;
import io.github.fermi4.particle.v1.domain.ProductInformation;
import io.github.fermi4.particle.v1.domain.resource.ProductApiResponse;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.config.partition.AllProductsPartitioner;

public class AllProductsPartitionerTest {
    @Mock
    private ParticleClient mockClient;

    private AllProductsPartitioner partitioner;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        partitioner = new AllProductsPartitioner(mockClient);
    }

    @Test
    public void testPartitionWithValidAccessToken() throws IOException {
        // Mock the behavior of ParticleClient
        ProductInformation product1 = new ProductInformation();
        product1.setId(1);
        ProductInformation product2 = new ProductInformation();
        product2.setId(2);
        ProductApiResponse productListResponse = new ProductApiResponse();
        productListResponse.setProducts(List.of(product1, product2));

        // Convert product list to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(productListResponse);

        Response mockedResponse = new Response.Builder()
                .request(new Request.Builder().url("https://example.com").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200) // HTTP Status Code
                .message("OK")
                .body(ResponseBody.create(jsonResponse, MediaType.get("application/json")))
                .build();
        when(mockClient.listProducts()).thenReturn(mockedResponse);

        // Mock the ParticleConnectorConfig
        Map<String, String> mockConfigMap = Map.of(
                ParticleConnectorConfig.ACCESS_TOKEN_CONFIG, "test-token",
                ParticleConnectorConfig.TOPIC_CONFIG, "test-topic",
                ParticleConnectorConfig.API_VERSION_CONFIG, "1.0");
        ParticleConnectorConfig mockConfig = new ParticleConnectorConfig(mockConfigMap);

        // Invoke the partition method
        List<Map<String, String>> partitions = partitioner.partition(mockConfig, 2);

        // Verify the result
        assertEquals(2, partitions.size());

        // Each partition should contain one product id
        for (Map<String, String> partition : partitions) {
            String productIds = partition.get(ParticleConnectorConfig.PRODUCT_ID_CONFIG);
            assertNotNull(productIds);
            assertTrue(productIds.equals("1") || productIds.equals("2"));
            assertEquals(ParticleConnectorConfig.EVENT_MODE_PRODUCT,
                    partition.get(ParticleConnectorConfig.EVENT_MODE_CONFIG));
        }
    }

    @Test
    public void testPartitionWithException() throws IOException {

        // Mock the behavior of ParticleClient
        String fakeExceptionString = "This is a fake exception";
        when(mockClient.listProducts()).thenThrow(new IOException(fakeExceptionString));

        // Mock the ParticleConnectorConfig
        Map<String, String> mockConfigMap = Map.of(
                ParticleConnectorConfig.ACCESS_TOKEN_CONFIG, "test-token",
                ParticleConnectorConfig.TOPIC_CONFIG, "test-topic",
                ParticleConnectorConfig.API_VERSION_CONFIG, "1.0");
        ParticleConnectorConfig mockConfig = new ParticleConnectorConfig(mockConfigMap);

        // Invoke the partition method
        Exception exception = assertThrows(ConnectException.class, () -> {
            partitioner.partition(mockConfig, 2);
        });

        // Verify the result
        assertEquals("java.io.IOException: " + fakeExceptionString, exception.getMessage());
    }

    @Test
    public void testPartitionWithNullAccessToken() {
        // Mock the ParticleConnectorConfig with null access token
        Map<String, String> mockConfigMap = Map.of(
                ParticleConnectorConfig.TOPIC_CONFIG, "test-topic",
                ParticleConnectorConfig.API_VERSION_CONFIG, "1.0");
        ParticleConnectorConfig mockConfig = new ParticleConnectorConfig(mockConfigMap);

        // Invoke the partition method and expect an exception
        Exception exception = assertThrows(ConnectException.class, () -> {
            partitioner.partition(mockConfig, 2);
        });

        // Verify the exception message
        assertEquals("Access token is null or empty -- check config " + ParticleConnectorConfig.ACCESS_TOKEN_CONFIG,
                exception.getMessage());
    }

    @Test
    public void testPartitionWithEmptyAccessToken() {
        // Mock the ParticleConnectorConfig with empty access token
        Map<String, String> mockConfigMap = Map.of(
                ParticleConnectorConfig.ACCESS_TOKEN_CONFIG, "",
                ParticleConnectorConfig.TOPIC_CONFIG, "test-topic",
                ParticleConnectorConfig.API_VERSION_CONFIG, "1.0");

        // Invoke the partition method and expect an exception
        Exception exception = assertThrows(ConfigException.class, () -> {
            ParticleConnectorConfig mockConfig = new ParticleConnectorConfig(mockConfigMap);
            partitioner.partition(mockConfig, 2);
        });

        // Verify the exception message
        String expected = "Invalid value  for configuration particle.connector.token: String must be non-empty";
        assertEquals(expected, exception.getMessage());
    }
}
