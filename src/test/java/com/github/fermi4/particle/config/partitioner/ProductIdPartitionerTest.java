package com.github.fermi4.particle.config.partitioner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.config.partition.ProductIdPartitioner;

public class ProductIdPartitionerTest {
    @Mock
    private ParticleConnectorConfig mockConfig;

    private ProductIdPartitioner partitioner;
    
    @BeforeEach
    public void setUp() {
        Map<String, String> mockConfigMap = Map.of(
                ParticleConnectorConfig.PRODUCT_ID_CONFIG, "product1,product2",
                ParticleConnectorConfig.DELIMITER_CONFIG, ",",
                ParticleConnectorConfig.TOPIC_CONFIG, "test_topic",
                ParticleConnectorConfig.ACCESS_TOKEN_CONFIG, "api_token",
                ParticleConnectorConfig.API_VERSION_CONFIG, ParticleConnectorConfig.PARTICLE_CONNECTOR_API_VERSION_1
        );
        mockConfig = new ParticleConnectorConfig(mockConfigMap);

        partitioner = new ProductIdPartitioner();
    }

    @Test
    public void testPartitionWithValidConfig() {
        int maxTask = 5;
        int expectedPartitions = 2;
        List<Map<String, String>> actualPartitions = partitioner.partition(mockConfig, maxTask);
        assertEquals(expectedPartitions, actualPartitions.size());
    }

    @Test
    public void testPartitionWithNullConfig() {
        
        int maxTask = 5;

        Exception exception = assertThrows(NullPointerException.class, () -> {
            partitioner.partition(null, maxTask);
        });
        
        assertEquals("Cannot invoke \"com.github.fermi4.particle.config.ParticleConnectorConfig.getDelimiter()\" because \"config\" is null", exception.getMessage());
    }

    @Test
    public void testPartitionWithWrongDelimiter() {
        int expectedPartitions = 1;
        int maxTask = 5;
        Map<String, String> mockConfigMap = Map.of(
            ParticleConnectorConfig.PRODUCT_ID_CONFIG, "product1,product2",
            ParticleConnectorConfig.DELIMITER_CONFIG, "-", // Oops, should have been ,
            ParticleConnectorConfig.TOPIC_CONFIG, "test_topic",
            ParticleConnectorConfig.ACCESS_TOKEN_CONFIG, "api_token",
            ParticleConnectorConfig.API_VERSION_CONFIG, ParticleConnectorConfig.PARTICLE_CONNECTOR_API_VERSION_1
            );
        mockConfig = new ParticleConnectorConfig(mockConfigMap);
        assertEquals(expectedPartitions, partitioner.partition(mockConfig, maxTask).size());
    }
}
