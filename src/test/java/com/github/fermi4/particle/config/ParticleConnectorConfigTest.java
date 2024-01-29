package com.github.fermi4.particle.config;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.ACCESS_TOKEN_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_PRODUCT;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.PRODUCT_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.TOPIC_CONFIG;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ParticleConnectorConfigTest {
	
	@Test
	public void testMinimalConfig() {
		assertDoesNotThrow(() -> {
			Map<String,String> map = new HashMap<>();
			map.put(TOPIC_CONFIG, "topic");
			map.put(ACCESS_TOKEN_CONFIG, "123456789");
			new ParticleConnectorConfig(map);			
		});
	}
	
	@Test
	public void testMinimalConfig_Product() {
		assertDoesNotThrow(() -> {
			Map<String,String> map = new HashMap<>();
			map.put(EVENT_MODE_CONFIG, EVENT_MODE_PRODUCT);
			map.put(TOPIC_CONFIG, "topic");
			map.put(ACCESS_TOKEN_CONFIG, "123456789");
			map.put(PRODUCT_ID_CONFIG, "photon");
			new ParticleConnectorConfig(map);			
		});
	}

	@Test
    public void testDefaultValues() {
        Map<String, String> map = new HashMap<>();
        map.put(ParticleConnectorConfig.TOPIC_CONFIG, "topic");
        map.put(ParticleConnectorConfig.ACCESS_TOKEN_CONFIG, "123456789");
        ParticleConnectorConfig config = new ParticleConnectorConfig(map);

        assertEquals(ParticleConnectorConfig.EVENT_MODE_ALL, config.getEventMode());
        assertEquals(ParticleConnectorConfig.PARTICLE_CONNECTOR_API_VERSION_1, config.getApiVersion());
        assertEquals(ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_NONE, config.getConnectorKeyMode());
        assertEquals(5000, config.getRetryDelay());
        assertEquals(10, config.getMaxReconnectAttempts());
        assertEquals(30000L, config.getHttpClientConfig().getConnectTimeout());
        assertEquals(30000L, config.getHttpClientConfig().getReadTimeout());
    }

	@Test
    public void testCustomConfig() {
        Map<String, String> map = new HashMap<>();
        map.put(ParticleConnectorConfig.EVENT_MODE_CONFIG, ParticleConnectorConfig.EVENT_MODE_DEVICE);
        map.put(ParticleConnectorConfig.API_VERSION_CONFIG, "1.0");
        map.put(ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_CONFIG, ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_PAYLOAD_COREID);
        map.put(ParticleConnectorConfig.RETRY_DELAY_CONFIG, "10000");
        map.put(ParticleConnectorConfig.MAX_RETRY_CONNECT_ATTEMPTS_CONFIG, "5");
        map.put(ParticleConnectorConfig.HTTP_CONNECT_TIMEOUT_CONFIG, "15000");
        map.put(ParticleConnectorConfig.HTTP_READ_TIMEOUT_CONFIG, "20000");
        map.put(ParticleConnectorConfig.TOPIC_CONFIG, "topic");
        map.put(ParticleConnectorConfig.ACCESS_TOKEN_CONFIG, "123456789");

        ParticleConnectorConfig config = new ParticleConnectorConfig(map);

        assertEquals(ParticleConnectorConfig.EVENT_MODE_DEVICE, config.getEventMode());
        assertEquals("1.0", config.getApiVersion());
        assertEquals(ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_PAYLOAD_COREID, config.getConnectorKeyMode());
        assertEquals(10000, config.getRetryDelay());
        assertEquals(5, config.getMaxReconnectAttempts());
        assertEquals(15000L, config.getHttpClientConfig().getConnectTimeout());
        assertEquals(20000L, config.getHttpClientConfig().getReadTimeout());
    }

	@Test
    public void testGetOriginalsAsStringWithDefaults() {
        Map<String, String> map = new HashMap<>();
        map.put(ParticleConnectorConfig.EVENT_MODE_CONFIG, ParticleConnectorConfig.EVENT_MODE_PRODUCT);
        map.put(ParticleConnectorConfig.API_VERSION_CONFIG, "1.0");
        map.put(ParticleConnectorConfig.TOPIC_CONFIG, "topic");
        map.put(ParticleConnectorConfig.ACCESS_TOKEN_CONFIG, "123456789");

        ParticleConnectorConfig config = new ParticleConnectorConfig(map);
        Map<String, String> combinedConfigs = config.getOriginalsAsStringWithDefaults();

        assertEquals(ParticleConnectorConfig.EVENT_MODE_PRODUCT, combinedConfigs.get(ParticleConnectorConfig.EVENT_MODE_CONFIG));
        assertEquals("1.0", combinedConfigs.get(ParticleConnectorConfig.API_VERSION_CONFIG));
        assertEquals(ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_NONE, combinedConfigs.get(ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_CONFIG));
        assertEquals("5000", combinedConfigs.get(ParticleConnectorConfig.RETRY_DELAY_CONFIG));
    }
}
