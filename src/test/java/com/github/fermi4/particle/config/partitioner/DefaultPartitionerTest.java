package com.github.fermi4.particle.config.partitioner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.config.partition.DefaultPartitioner;

public class DefaultPartitionerTest {
    
    @Mock
    private ParticleConnectorConfig mockConfig;

    private DefaultPartitioner partitioner;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        partitioner = new DefaultPartitioner();
    }

    @Test
    public void testPartitionWithSingleTask() {
        // Mock the behavior of ParticleConnectorConfig
        Map<String, String> mockConfigMap = Map.of(
                "particle.connector.token", "test-token",
                "particle.connector.topic", "test-topic",
                "particle.connector.api_version", "1.0"
        );

        when(mockConfig.getOriginalsAsStringWithDefaults()).thenReturn(mockConfigMap);
        when(mockConfig.originalsStrings()).thenReturn(mockConfigMap);

        // Invoke the partition method
        List<Map<String, String>> partitions = partitioner.partition(mockConfig, 1);

        // Verify the result
        assertEquals(1, partitions.size());
        assertEquals(mockConfigMap, partitions.get(0));
    }

    @Test
    public void testPartitionWithMultipleTasks() {
        // Mock the behavior of ParticleConnectorConfig
        Map<String, String> mockConfigMap = Map.of(
                "particle.connector.token", "test-token",
                "particle.connector.topic", "test-topic",
                "particle.connector.api_version", "1.0"
        );

        when(mockConfig.getOriginalsAsStringWithDefaults()).thenReturn(mockConfigMap);
        when(mockConfig.originalsStrings()).thenReturn(mockConfigMap);

        // Invoke the partition method
        List<Map<String, String>> partitions = partitioner.partition(mockConfig, 5);

        // Verify the result
        assertEquals(1, partitions.size());
        assertEquals(mockConfigMap, partitions.get(0));
    }
}
