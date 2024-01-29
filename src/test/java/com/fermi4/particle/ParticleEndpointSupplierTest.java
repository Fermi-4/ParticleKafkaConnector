package com.fermi4.particle;

import static com.fermi4.particle.ParticleConnectorConfig.ACCESS_MODE_CONFIG;
import static com.fermi4.particle.ParticleConnectorConfig.ACCESS_MODE_DEVICE;
import static com.fermi4.particle.ParticleConnectorConfig.ACCESS_MODE_PRODUCT;
import static com.fermi4.particle.ParticleConnectorConfig.ACCESS_TOKEN_CONFIG;
import static com.fermi4.particle.ParticleConnectorConfig.DEVICE_ID_CONFIG;
import static com.fermi4.particle.ParticleConnectorConfig.PRODUCT_ID_CONFIG;
import static com.fermi4.particle.ParticleConnectorConfig.TOPIC_CONFIG;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.connect.errors.ConnectException;
import org.junit.jupiter.api.Test;

import okhttp3.HttpUrl;

public class ParticleEndpointSupplierTest {
	
	private final String TOPIC = "test_topic";
	private final String ACCESS_KEY = "test_topic";
	private final String FAKE_DEVICE_ID = "123456789";
	private final String FAKE_PRODUCT_ID = "123456789";
	
	@Test
	public void testSingleDevice() {
		// Test configuration
		Map<String,String> map = new HashMap<>();
		map.put(ACCESS_MODE_CONFIG, ACCESS_MODE_DEVICE);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);
		map.put(DEVICE_ID_CONFIG, FAKE_DEVICE_ID);
		
		ParticleConnectorConfig config = new ParticleConnectorConfig(map);
		HttpUrl endpoint = ParticleEndpointSupplier.get(config);
		System.out.println(endpoint.toString());
	}
	
	@Test
	public void testSingleProduct() {
		// Test configuration
		Map<String,String> map = new HashMap<>();
		map.put(ACCESS_MODE_CONFIG, ACCESS_MODE_PRODUCT);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);
		map.put(PRODUCT_ID_CONFIG, "photon");
		ParticleConnectorConfig config = new ParticleConnectorConfig(map);
		HttpUrl endpoint = ParticleEndpointSupplier.get(config);
		System.out.println(endpoint.toString());
	}
	
	@Test
	public void testSingleProduct_MissingProductId() {
		// Test configuration
		assertThrows(ConnectException.class, () -> {
			Map<String,String> map = new HashMap<>();
			map.put(ACCESS_MODE_CONFIG, ACCESS_MODE_PRODUCT);
			map.put(TOPIC_CONFIG, TOPIC);
			map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);
			ParticleConnectorConfig config = new ParticleConnectorConfig(map);
			HttpUrl endpoint = ParticleEndpointSupplier.get(config);			
		});
	}

		
	
}
