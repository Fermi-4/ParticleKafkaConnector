package com.github.fermi4.particle.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.fermi4.particle.v1.ParticleClient;
import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.sse.EventProvider;
import com.github.fermi4.particle.sse.EventProviderFactory;
import com.github.fermi4.particle.sse.providers.CompositeEventProvider;
import com.github.fermi4.particle.sse.providers.device.AllDevicesEventProvider;
import com.github.fermi4.particle.sse.providers.device.DeviceEventProvider;
import com.github.fermi4.particle.sse.providers.product.ProductEventProvider;

public class EventProviderFactoryTest {

    @Test
    void testGetAllDevicesEventProvider() {
        // Prepare mock objects
        ParticleConnectorConfig config = Mockito.mock(ParticleConnectorConfig.class);
        ParticleClient client = Mockito.mock(ParticleClient.class);
        
        // Configure mock behavior
        Mockito.when(config.getEventMode()).thenReturn(ParticleConnectorConfig.EVENT_MODE_ALL);
        
        // Invoke the factory method
        EventProvider provider = EventProviderFactory.get(config, client);
        
        // Verify the correct provider type
        assertTrue(provider instanceof AllDevicesEventProvider);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    void testGetDeviceEventProvider() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        // Prepare mock objects
        ParticleConnectorConfig config = Mockito.mock(ParticleConnectorConfig.class);
        ParticleClient client = Mockito.mock(ParticleClient.class);
        
        // Configure mock behavior
        Mockito.when(config.getEventMode()).thenReturn(ParticleConnectorConfig.EVENT_MODE_DEVICE);
        Mockito.when(config.getDeviceId()).thenReturn("device1,device2"); // Example device IDs
        Mockito.when(config.getDelimiter()).thenReturn(",");

        // Invoke the factory method
        EventProvider provider = EventProviderFactory.get(config, client);
        
        // Verify the correct provider type and behavior
        assertTrue(provider instanceof CompositeEventProvider);

        // Use reflection to access the private providers field
        Field providersField = CompositeEventProvider.class.getDeclaredField("providers");
        providersField.setAccessible(true);
        List<EventProvider> providers = (List<EventProvider>) providersField.get(provider);

        // Verify the type of providers inside the CompositeEventProvider
        assertEquals(2, providers.size()); // Check if it has two providers inside
        
        for (EventProvider innerProvider : providers) {
            assertTrue(innerProvider instanceof DeviceEventProvider);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    void testGetProductEventProvider() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        // Prepare mock objects
        ParticleConnectorConfig config = Mockito.mock(ParticleConnectorConfig.class);
        ParticleClient client = Mockito.mock(ParticleClient.class);
        
        // Configure mock behavior
        Mockito.when(config.getEventMode()).thenReturn(ParticleConnectorConfig.EVENT_MODE_PRODUCT);
        Mockito.when(config.getProductId()).thenReturn("product1,product2");
        Mockito.when(config.getDelimiter()).thenReturn(",");
        
        // Invoke the factory method
        EventProvider provider = EventProviderFactory.get(config, client);
        
        // Verify the correct provider type and behavior
        assertTrue(provider instanceof CompositeEventProvider); // Check if it's a CompositeEventProvider
        
        // Use reflection to access the private providers field
        Field providersField = CompositeEventProvider.class.getDeclaredField("providers");
        providersField.setAccessible(true);
        List<EventProvider> providers = (List<EventProvider>) providersField.get(provider);
        
        // Verify the type of providers inside the CompositeEventProvider
        assertEquals(2, providers.size()); // Check if it has two providers inside
        
        for (EventProvider innerProvider : providers) {
            assertTrue(innerProvider instanceof ProductEventProvider);
        }
    }
    
    @Test
    void testUnknownEventMode() {
        // Prepare mock objects
        ParticleConnectorConfig config = Mockito.mock(ParticleConnectorConfig.class);
        ParticleClient client = Mockito.mock(ParticleClient.class);
        
        // Configure mock behavior
        Mockito.when(config.getEventMode()).thenReturn("unknown_event_mode");
        
        // Invoke the factory method and assert exception
        assertThrows(Exception.class, () -> {
            EventProviderFactory.get(config, client);
        });
    }

}
