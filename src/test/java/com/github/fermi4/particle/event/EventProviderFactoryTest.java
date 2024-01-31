package com.github.fermi4.particle.event;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.ACCESS_TOKEN_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.DEVICE_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_DEVICE;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.TOPIC_CONFIG;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.sse.EventProvider;
import com.github.fermi4.particle.sse.EventProviderFactory;
import com.github.fermi4.particle.sse.provider.CompositeEventProvider;

public class EventProviderFactoryTest {
	
	private final String TOPIC = "test_topic";
	private final String ACCESS_KEY = "test_access_key";
	
	@Test
	public void testMultipleDeviceEventProvider() {
		/* create device config */
		Map<String, String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);
		map.put(DEVICE_ID_CONFIG, "1234,0123,4567");
		
		EventProvider provider = EventProviderFactory.get(new ParticleConnectorConfig(map));
		Assertions.assertTrue(CompositeEventProvider.class.isAssignableFrom(provider.getClass()));
	}
	
}
