package com.github.fermi4.particle;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.ACCESS_TOKEN_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.DEVICE_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_DEVICE;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_PRODUCT;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_DEVICE;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_PRODUCT;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.PRODUCT_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.TOPIC_CONFIG;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.convert.ConverterContext;
import com.github.fermi4.particle.convert.EventConverterFactory;
import com.github.fermi4.particle.convert.SourceRecordConverter;
import com.github.fermi4.particle.convert.extract.EventDataExtraction;
import com.github.fermi4.particle.sse.Event;

public class EventConverterFactoryTest {
	
	private final String TOPIC = "TOPIC";
	private final String ACCESS_KEY = "ACCESS_KEY";
	private final String FAKE_DEVICE_ID = "DEVICE_ID";
	private final String FAKE_PRODUCT_ID = "PRODUCT_ID";
	
	private final String EVENT_TYPE = "TYPE";
	private final String EVENT_ID = "ID";
	private final String EVENT_DATA = "DATA";
	private final String EXPECTED_VALUE = EVENT_DATA;

	@Test
	public void testDeviceConfig() {
		
		/* Device configuration */
		Map<String,String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);
		map.put(DEVICE_ID_CONFIG, FAKE_DEVICE_ID);
		map.put(PARTICLE_CONNECTOR_KEY_CONFIG, PARTICLE_CONNECTOR_KEY_DEVICE);
		
		ParticleConnectorConfig config = new ParticleConnectorConfig(map);
		Event event = new Event(EVENT_ID, EVENT_TYPE, EVENT_DATA);
		ConverterContext context = new ConverterContext();
		context.setConfig(config);
		context.setEvent(event);
		
		/* get converter - should return source record with key == device id */
		Function<ConverterContext, SourceRecord> sourceRecordConverter =  SourceRecordConverter
				.builder()
				.keyExtractor(c -> new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA, c.getConfig().getDeviceId().getBytes()))
				.valueExtractor(c -> new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA, c.getEvent().getData().getBytes()))
				.build();
		
		SourceRecord sourceRecord = sourceRecordConverter.apply(context);
		
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
		map.put(PARTICLE_CONNECTOR_KEY_CONFIG, PARTICLE_CONNECTOR_KEY_PRODUCT);
		ParticleConnectorConfig config = new ParticleConnectorConfig(map);
		
		/* get converter - should return source record with key == device id */
		Function<ConverterContext, SourceRecord> sourceRecordConverter =  SourceRecordConverter
				.builder()
				.keyExtractor(c -> new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA, c.getConfig().getProductId().getBytes()))
				.valueExtractor(c -> new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA, c.getEvent().getData().getBytes()))
				.build();
		
		Event event = new Event(EVENT_ID, EVENT_TYPE, EVENT_DATA);
		
		ConverterContext context = new ConverterContext();
		context.setConfig(config);
		context.setEvent(event);
		
		SourceRecord sourceRecord = sourceRecordConverter.apply(context);
		
		Assertions.assertArrayEquals(FAKE_PRODUCT_ID.getBytes(), (byte[]) sourceRecord.key());
		Assertions.assertArrayEquals(EXPECTED_VALUE.getBytes(), (byte[]) sourceRecord.value());
	}
	
	/**
	 * 
	 * No key mode is provided therefore source record key == null, meaning 
	 * connector will distribute round robin across partitions
	 */
	@Test
	public void testProductConfig_NoId() {
		
		/* Device configuration */
		Map<String,String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_PRODUCT);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);

		ParticleConnectorConfig config = new ParticleConnectorConfig(map);
		
		Event event = new Event(EVENT_ID, EVENT_TYPE, EVENT_DATA);
		ConverterContext context = new ConverterContext();
		context.setConfig(config);
		context.setEvent(event);
		
		/* get converter - should return source record with key == null */
		Function<ConverterContext, SourceRecord> sourceRecordConverter =  SourceRecordConverter
				.builder()
				.valueExtractor(c -> new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA, c.getEvent().getData().getBytes()))
				.build();
		
		SourceRecord sourceRecord = sourceRecordConverter.apply(context);

		/* Since key mode not provided, key == none */
		Assertions.assertNull(sourceRecord.key());
		Assertions.assertArrayEquals(EXPECTED_VALUE.getBytes(), (byte[]) sourceRecord.value());
	}
	
	// TODO:
	// Device no id
	// all
	
	
}
