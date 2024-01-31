package com.github.fermi4.particle;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_DEVICE;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_PRODUCT;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.ACCESS_TOKEN_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.DEVICE_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.PRODUCT_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.TOPIC_CONFIG;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.convert.SSEEventConverterFactory;
import com.github.fermi4.particle.convert.SourceRecordConverter;
import com.github.fermi4.particle.sse.SSEEvent;

public class SSEEventConverterFactoryTest {
	
	private final String TOPIC = "TOPIC";
	private final String ACCESS_KEY = "ACCESS_KEY";
	private final String FAKE_DEVICE_ID = "DEVICE_ID";
	private final String FAKE_PRODUCT_ID = "PRODUCT_ID";
	
	private final String EVENT_TYPE = "TYPE";
	private final String EVENT_ID = "ID";
	private final String EVENT_DATA = "DATA";
	private final String EXPECTED_VALUE = EVENT_DATA;
//			new Struct(SSEEvent.PARTICLE_EVENT_SCHEMA)
//			.put("id", EVENT_ID)
//			.put("type", EVENT_TYPE)
//			.put("data", EVENT_DATA);
	
	@Test
	public void testDeviceConfig() {
		
		/* Device configuration */
		Map<String,String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);
		map.put(DEVICE_ID_CONFIG, FAKE_DEVICE_ID);
		ParticleConnectorConfig config = new ParticleConnectorConfig(map);
		
		/* get converter - should return source record with key == device id */
		SourceRecordConverter<SSEEvent> sourceRecordConverter = SSEEventConverterFactory.get(config);
		
		SSEEvent event = new SSEEvent(EVENT_ID, EVENT_TYPE, EVENT_DATA);
		SourceRecord sourceRecord = sourceRecordConverter.convert(event);
		System.out.println(sourceRecord);
		Assertions.assertArrayEquals(FAKE_DEVICE_ID.getBytes(), (byte[]) sourceRecord.key());
		Assertions.assertArrayEquals(EXPECTED_VALUE.getBytes(), (byte[]) sourceRecord.value());
	}
	
	@Test
	public void testProductConfig() {
		/* Device configuration */
		Map<String,String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_PRODUCT);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);
		map.put(PRODUCT_ID_CONFIG, FAKE_PRODUCT_ID);
		ParticleConnectorConfig config = new ParticleConnectorConfig(map);
		
		/* get converter - should return source record with key == device id */
		SourceRecordConverter<SSEEvent> sourceRecordConverter = SSEEventConverterFactory.get(config);
		
		SSEEvent event = new SSEEvent(EVENT_ID, EVENT_TYPE, EVENT_DATA);
		SourceRecord sourceRecord = sourceRecordConverter.convert(event);
		
		Assertions.assertArrayEquals(FAKE_PRODUCT_ID.getBytes(), (byte[]) sourceRecord.key());
		Assertions.assertArrayEquals(EXPECTED_VALUE.getBytes(), (byte[]) sourceRecord.value());
	}
	
	@Test
	public void testProductConfig_NoId() throws JsonProcessingException {
		/* Device configuration */
		Map<String,String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_PRODUCT);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);

		ParticleConnectorConfig config = new ParticleConnectorConfig(map);
		
		/* get converter - should return source record with key == device id */
		SourceRecordConverter<SSEEvent> sourceRecordConverter = SSEEventConverterFactory.get(config);
		
		SSEEvent event = new SSEEvent(EVENT_ID, EVENT_TYPE, EVENT_DATA);
		SourceRecord sourceRecord = sourceRecordConverter.convert(event);

		/* Since product ID not provided, key == event type */
		Assertions.assertArrayEquals(EVENT_TYPE.getBytes(), (byte[]) sourceRecord.key());
		Assertions.assertArrayEquals(EXPECTED_VALUE.getBytes(), (byte[]) sourceRecord.value());
	}
	
	// TODO:
	// Device no id
	// all
	
	
}
