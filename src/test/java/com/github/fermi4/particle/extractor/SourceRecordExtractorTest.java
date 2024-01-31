package com.github.fermi4.particle.extractor;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.ACCESS_TOKEN_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.DEVICE_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_DEVICE;
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
import com.github.fermi4.particle.convert.ConverterContext;
import com.github.fermi4.particle.convert.SourceRecordConverter;
import com.github.fermi4.particle.convert.extract.EventDataExtractors;
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
			Function<ConverterContext, SourceRecord> extractor;
			
			ConverterContext fakeContext = new ConverterContext();
			fakeContext.setConfig(null);
			fakeContext.setEvent(null);
			
			extractor = SourceRecordConverter
					.builder()
					.build();
			
			extractor.apply(fakeContext);			
		});
	}
	
	/**
	 * Test that we can use the lombok generated SourceRecordExtractor builder class
	 * with the DataExtractors class
	 */
	@Test
	public void testSourceRecordExtractor_DataExtractorsInterop() {
			Function<ConverterContext, SourceRecord> extractor;
			
			Map<String, String> map = new HashMap<>();
			map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
			map.put(TOPIC_CONFIG, "test");
			map.put(ACCESS_TOKEN_CONFIG, "test");
			map.put(DEVICE_ID_CONFIG, "test");
			
			ConverterContext fakeContext = new ConverterContext();
			fakeContext.setConfig(new ParticleConnectorConfig(map));
			
			String value = "{\"coreid\":\"123\"}";
			String key = "123";
			fakeContext.setEvent(new Event("id", "type", value));
			
			extractor = SourceRecordConverter
					.builder()
					.keyExtractor(EventDataExtractors.extractPayloadCoreIdBytes())
					.valueExtractor(EventDataExtractors.extractEventDataBytes())
					.build();
			
			// do extraction
			SourceRecord sourceRecord = extractor.apply(fakeContext);
			
			// assertions
			assertNotNull(sourceRecord);
			assertArrayEquals(key.getBytes(), (byte[])sourceRecord.key());
			assertArrayEquals(value.getBytes(), (byte[])sourceRecord.value());
			assertNull(sourceRecord.sourceOffset());
			assertNull(sourceRecord.sourcePartition());
			assertNull(sourceRecord.kafkaPartition());
	}
}
