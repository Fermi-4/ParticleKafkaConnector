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

import com.github.fermi4.particle.api.ParticleClient;
import com.github.fermi4.particle.api.domain.DeviceInformation;
import com.github.fermi4.particle.api.domain.resource.DeviceApiResponse;
import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.config.partition.DevicesInGroupsPartitioner;

public class DevicesInGroupsPartitionerTest {
    @Mock
    private ParticleClient mockClient;

    private DevicesInGroupsPartitioner partitioner;
    private final String productIdOrSlug = "test-product";
    private final List<String> productGroups = List.of("group1", "group2");

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        partitioner = new DevicesInGroupsPartitioner(mockClient, productIdOrSlug, productGroups);
    }

    @Test
    public void testPartitionWithValidDevices() throws IOException {
        // Mock the behavior of ParticleClient
        DeviceInformation device1 = new DeviceInformation();
        device1.setId("device1");
        DeviceInformation device2 = new DeviceInformation();
        device2.setId("device2");

        DeviceApiResponse response = new DeviceApiResponse();
        response.setDevices(List.of(device1, device2));

        when(mockClient.listDevicesInProductInGroups(productIdOrSlug, productGroups)).thenReturn(response);

        // Mock the ParticleConnectorConfig
        Map<String, String> mockConfigMap = Map.of(
                ParticleConnectorConfig.ACCESS_TOKEN_CONFIG, "test-token",
                ParticleConnectorConfig.TOPIC_CONFIG, "test-topic",
                ParticleConnectorConfig.API_VERSION_CONFIG, "1.0"
        );
        ParticleConnectorConfig mockConfig = new ParticleConnectorConfig(mockConfigMap);

        // Invoke the partition method
        List<Map<String, String>> partitions = partitioner.partition(mockConfig, 2);

        // Verify the result
        assertEquals(2, partitions.size());

        // Each partition should contain one device id
        for (Map<String, String> partition : partitions) {
            String deviceIds = partition.get(ParticleConnectorConfig.DEVICE_ID_CONFIG);
            assertNotNull(deviceIds);
            assertTrue(deviceIds.equals("device1") || deviceIds.equals("device2"));
        }
    }

    @Test
    public void testPartitionWithException() throws IOException {
        // Mock the behavior of ParticleClient
        String fakeExceptionString = "This is a fake exception";
        when(mockClient.listDevicesInProductInGroups(productIdOrSlug, productGroups)).thenThrow(new IOException(fakeExceptionString));

        // Mock the ParticleConnectorConfig
        Map<String, String> mockConfigMap = Map.of(
                ParticleConnectorConfig.ACCESS_TOKEN_CONFIG, "test-token",
                ParticleConnectorConfig.TOPIC_CONFIG, "test-topic",
                ParticleConnectorConfig.API_VERSION_CONFIG, "1.0"
        );
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
                ParticleConnectorConfig.API_VERSION_CONFIG, "1.0"
        );
        ParticleConnectorConfig mockConfig = new ParticleConnectorConfig(mockConfigMap);

        // Invoke the partition method and expect an exception
        Exception exception = assertThrows(ConnectException.class, () -> {
            partitioner.partition(mockConfig, 2);
        });

        // Verify the exception message
        assertEquals("Access token is null or empty -- check config " + ParticleConnectorConfig.ACCESS_TOKEN_CONFIG, exception.getMessage());
    }

    @Test
    public void testPartitionWithEmptyAccessToken() {
        // Mock the ParticleConnectorConfig with empty access token
        Map<String, String> mockConfigMap = Map.of(
                ParticleConnectorConfig.ACCESS_TOKEN_CONFIG, "",
                ParticleConnectorConfig.TOPIC_CONFIG, "test-topic",
                ParticleConnectorConfig.API_VERSION_CONFIG, "1.0"
        );
        
        // Invoke the partition method and expect an exception
        Exception exception = assertThrows(ConfigException.class, () -> {
            ParticleConnectorConfig mockConfig = new ParticleConnectorConfig(mockConfigMap);
            partitioner.partition(mockConfig, 2);
        });

        // Verify the exception message
        assertEquals("Invalid value  for configuration " + ParticleConnectorConfig.ACCESS_TOKEN_CONFIG + ": String must be non-empty", exception.getMessage());
    }
}
