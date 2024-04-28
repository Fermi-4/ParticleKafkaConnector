package com.github.fermi4.particle.config.partitioner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.apache.kafka.connect.errors.ConnectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.fermi4.particle.api.ParticleClient;
import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.config.partition.AllDevicesPartitioner;
import com.github.fermi4.particle.config.partition.AllProductsPartitioner;
import com.github.fermi4.particle.config.partition.DefaultPartitioner;
import com.github.fermi4.particle.config.partition.DeviceIdPartitioner;
import com.github.fermi4.particle.config.partition.DevicesInGroupsPartitioner;
import com.github.fermi4.particle.config.partition.DevicesInProductPartitioner;
import com.github.fermi4.particle.config.partition.ParticleTaskConfigPartitioner;
import com.github.fermi4.particle.config.partition.PartitionerFactory;
import com.github.fermi4.particle.config.partition.ProductIdPartitioner;

public class PartitionerFactoryTest {
  
    @Mock
    private ParticleConnectorConfig mockConfig;

    @Mock
    private ParticleClient mockClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetDefaultPartitioner() {
        when(mockConfig.getEventMode()).thenReturn(ParticleConnectorConfig.EVENT_MODE_ALL);
        when(mockConfig.getDiscover()).thenReturn(null);
        ParticleTaskConfigPartitioner partitioner = PartitionerFactory.get(mockConfig);
        assertEquals(DefaultPartitioner.class, partitioner.getClass());
    }

    @Test
    public void testGetDeviceIdPartitioner() {
        when(mockConfig.getEventMode()).thenReturn(ParticleConnectorConfig.EVENT_MODE_DEVICE);
        when(mockConfig.getDiscover()).thenReturn(null);
        ParticleTaskConfigPartitioner partitioner = PartitionerFactory.get(mockConfig);
        assertEquals(DeviceIdPartitioner.class, partitioner.getClass());
    }

    @Test
    public void testGetProductIdPartitioner() {
        when(mockConfig.getEventMode()).thenReturn(ParticleConnectorConfig.EVENT_MODE_PRODUCT);
        when(mockConfig.getDiscover()).thenReturn(null);
        ParticleTaskConfigPartitioner partitioner = PartitionerFactory.get(mockConfig);
        assertEquals(ProductIdPartitioner.class, partitioner.getClass());
    }

    @Test
    public void testGetAllDevicesPartitioner() {
        when(mockConfig.getDiscover()).thenReturn(ParticleConnectorConfig.DISCOVER_MODE_DISCOVER_ALL_DEVICES);
        when(mockConfig.getEventMode()).thenReturn(null);
        when(mockConfig.getApiVersion()).thenReturn(ParticleConnectorConfig.PARTICLE_CONNECTOR_API_VERSION_1);
        when(mockConfig.getHttpClientConfig()).thenReturn(null);
        ParticleTaskConfigPartitioner partitioner = PartitionerFactory.get(mockConfig);
        assertEquals(AllDevicesPartitioner.class, partitioner.getClass());
    }

    @Test
    public void testGetAllProductsPartitioner() {
        when(mockConfig.getDiscover()).thenReturn(ParticleConnectorConfig.DISCOVER_MODE_DISCOVER_ALL_PRODUCTS);
        when(mockConfig.getEventMode()).thenReturn(null);

        when(mockConfig.getApiVersion()).thenReturn(ParticleConnectorConfig.PARTICLE_CONNECTOR_API_VERSION_1);
        when(mockConfig.getDelimiter()).thenReturn(",");
        ParticleTaskConfigPartitioner partitioner = PartitionerFactory.get(mockConfig);
        assertEquals(AllProductsPartitioner.class, partitioner.getClass());
    }

    @Test
    public void testGetDevicesInProductPartitioner() {
        String productId = "test-product";
        when(mockConfig.getDiscover()).thenReturn(ParticleConnectorConfig.DISCOVER_MODE_DISCOVER_ALL_DEVICES_IN_PRODUCT);
        when(mockConfig.getEventMode()).thenReturn(null);
        when(mockConfig.getProductId()).thenReturn(productId);
        when(mockConfig.getApiVersion()).thenReturn(ParticleConnectorConfig.PARTICLE_CONNECTOR_API_VERSION_1);
        when(mockConfig.getDelimiter()).thenReturn(",");
        ParticleTaskConfigPartitioner partitioner = PartitionerFactory.get(mockConfig);
        assertEquals(DevicesInProductPartitioner.class, partitioner.getClass());
    }

    @Test
    public void testGetDevicesInProductAndGroupsPartitioner() {
        String productId = "test-product";
        String productGroups = "group1,group2";
        when(mockConfig.getDiscover()).thenReturn(ParticleConnectorConfig.DISCOVER_MODE_DISCOVER_ALL_DEVICES_IN_PRODUCT_AND_GROUPS);
        when(mockConfig.getEventMode()).thenReturn(null);
        when(mockConfig.getProductId()).thenReturn(productId);
        when(mockConfig.getProductGroups()).thenReturn(productGroups);
        when(mockConfig.getApiVersion()).thenReturn(ParticleConnectorConfig.PARTICLE_CONNECTOR_API_VERSION_1);
        when(mockConfig.getDelimiter()).thenReturn(",");
        ParticleTaskConfigPartitioner partitioner = PartitionerFactory.get(mockConfig);
        assertEquals(DevicesInGroupsPartitioner.class, partitioner.getClass());
    }

    @Test
    public void testInvalidDiscoverMode() {
        when(mockConfig.getDiscover()).thenReturn("invalid-mode");
        when(mockConfig.getEventMode()).thenReturn(null);
        assertThrows(ConnectException.class, () -> PartitionerFactory.get(mockConfig));
    }

    @Test
    public void testInvalidEventMode() {
        when(mockConfig.getEventMode()).thenReturn("invalid-mode");
        when(mockConfig.getDiscover()).thenReturn(null);
        assertThrows(ConnectException.class, () -> PartitionerFactory.get(mockConfig));
    }

    @Test
    public void testMissingConfig() {
        when(mockConfig.getDiscover()).thenReturn(null);
        when(mockConfig.getEventMode()).thenReturn(null);
        assertThrows(ConnectException.class, () -> PartitionerFactory.get(mockConfig));
    }
}
