package com.github.fermi4.particle.extractor;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.ACCESS_TOKEN_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.DEVICE_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_DEVICE;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_PAYLOAD_COREID;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.TOPIC_CONFIG;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.convert.EventConverter;
import com.github.fermi4.particle.convert.SourceRecordConverter;
import com.github.fermi4.particle.sse.Event;

public class SourceRecordExtractorTest {
	
	/**
	 * Nulls, nulls everywhere...
	 * 
	 * don't want it to throw some nasty null pointers in the 
	 * case where nothing is initialized.. so this just test for that
	 * 
	 * null context parameters, builder sets nothing for extractor 
	 * (uses defaults many of which return null)
	 */
	@Test
	public void testSourceRecordExtractor_NullContextWithDefaults() {
		assertDoesNotThrow(() -> {
			Function<Event, SourceRecord> extractor;
			
			extractor = SourceRecordConverter
					.builder()
					.build();
			
			extractor.apply(null);
		});
	}
	
	/**
	 * Test that we can use the lombok generated SourceRecordExtractor builder class
	 * with the DataExtractors class
	 */
	@Test
	public void testSourceRecordExtractor_DataExtractorsInterop() {
			Function<Event, SourceRecord> extractor;
			
			Map<String, String> map = new HashMap<>();
			map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
			map.put(PARTICLE_CONNECTOR_KEY_CONFIG, PARTICLE_CONNECTOR_KEY_PAYLOAD_COREID);
			map.put(TOPIC_CONFIG, "test");
			map.put(ACCESS_TOKEN_CONFIG, "test");
			map.put(DEVICE_ID_CONFIG, "test");
			ParticleConnectorConfig config = new ParticleConnectorConfig(map);
			
			String value = "{\"coreid\":\"123\"}";
			String key = "123";
			Event event = new Event("id", "type", value);
			
			extractor = new EventConverter(config);
			
			// do extraction
			SourceRecord sourceRecord = extractor.apply(event);
			
			// assertions
			assertNotNull(sourceRecord);
			assertArrayEquals(key.getBytes(), (byte[])sourceRecord.key());
			assertArrayEquals(value.getBytes(), (byte[])sourceRecord.value());
			assertNull(sourceRecord.sourceOffset());
			assertNull(sourceRecord.sourcePartition());
			assertNull(sourceRecord.kafkaPartition());
	}

	
}
