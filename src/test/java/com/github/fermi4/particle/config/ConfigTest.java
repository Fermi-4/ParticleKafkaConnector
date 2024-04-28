package com.github.fermi4.particle.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ConfigTest {
	
	@Test
	@Disabled
	public void printHtml() {		
		System.out.println(ParticleConnectorConfig.conf().toHtmlTable(new HashMap<>()));
	}

	@Test
    void testDefaultConfig() {
		// required parameters
        Map<String, String> parsedConfig = Map.of(
			ParticleConnectorConfig.TOPIC_CONFIG, "topic"
		);

        ParticleConnectorConfig config = new ParticleConnectorConfig(parsedConfig);

        assertEquals(ParticleConnectorConfig.EVENT_MODE_ALL, config.getEventMode());
        assertEquals(ParticleConnectorConfig.PARTICLE_CONNECTOR_API_VERSION_1, config.getApiVersion());
        assertEquals(ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_NONE, config.getConnectorKeyMode());
        assertEquals(5000, config.getRetryDelay());
        assertEquals(10, config.getMaxReconnectAttempts());
        assertEquals(30000L, config.getHttpClientConfig().getConnectTimeout());
        assertEquals(30000L, config.getHttpClientConfig().getReadTimeout());
    }
}
