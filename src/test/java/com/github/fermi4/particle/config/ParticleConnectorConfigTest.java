package com.github.fermi4.particle.config;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_PRODUCT;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.ACCESS_TOKEN_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.PRODUCT_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.TOPIC_CONFIG;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.config.ParticleConnectorConfig;

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
	
}
