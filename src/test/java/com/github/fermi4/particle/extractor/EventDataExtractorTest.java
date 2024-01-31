package com.github.fermi4.particle.extractor;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.ACCESS_TOKEN_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.DEVICE_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_DEVICE;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.PRODUCT_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.TOPIC_CONFIG;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.convert.ConverterContext;
import com.github.fermi4.particle.convert.extract.EventDataExtractors;
import com.github.fermi4.particle.convert.extract.EventDataExtraction;
import com.github.fermi4.particle.sse.Event;

public class EventDataExtractorTest {

	// Event
	String mockSseJson = "{\"data\":\"23.34\",\"ttl\":\"60\",\"published_at\":\"2015-07-18T00:12:18.174Z\",\"coreid\":\"0123456789abcdef01234567\"}\n\n";
	String mockEventType = "mock_type";
	String mockEventId = "mock_id";
	String mockCoreId = "0123456789abcdef01234567"; // this is from above sse json string
	Event mockEvent = new Event(mockEventId, mockEventType, mockSseJson);

	// Context
	String mockDeviceId = "mock_device_id";
	String mockProductId = "mock_product_id";
	ParticleConnectorConfig mockConfig;
	ConverterContext mockContext;

	@BeforeEach
	public void setUp() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
		map.put(TOPIC_CONFIG, "test");
		map.put(ACCESS_TOKEN_CONFIG, "test");
		map.put(DEVICE_ID_CONFIG, mockDeviceId);
		map.put(PRODUCT_ID_CONFIG, mockProductId);
		mockConfig = new ParticleConnectorConfig(map);

		mockContext = new ConverterContext();
		mockContext.setEvent(mockEvent);
		mockContext.setConfig(mockConfig);
	}

	@Test
	public void testAllExtractors() {

		List<Function<ConverterContext, EventDataExtraction>> extractors = Arrays.asList(
				EventDataExtractors.extractConfigDeviceIdBytes(), 
				EventDataExtractors.extractConfigProductIdBytes(),
				EventDataExtractors.extractPayloadCoreIdBytes(), 
				EventDataExtractors.extractEventTypeBytes(),
				EventDataExtractors.extractEventDataBytes(), 
				EventDataExtractors.extractNull());

		List<Consumer<Object>> assertions = Arrays.asList(
				(d) -> Assertions.assertArrayEquals(mockDeviceId.getBytes(), (byte[]) ((EventDataExtraction) d).getData().get()),
				(d) -> Assertions.assertArrayEquals(mockProductId.getBytes(),(byte[]) ((EventDataExtraction) d).getData().get()),
				(d) -> Assertions.assertArrayEquals(mockCoreId.getBytes(), (byte[]) ((EventDataExtraction) d).getData().get()),
				(d) -> Assertions.assertArrayEquals(mockEventType.getBytes(), (byte[]) ((EventDataExtraction) d).getData().get()),
				(d) -> Assertions.assertArrayEquals(mockSseJson.getBytes(), (byte[]) ((EventDataExtraction) d).getData().get()),
				(d) -> Assertions.assertEquals(null, ((EventDataExtraction) d).getData().orElse(null)));

		Assertions.assertEquals(assertions.size(), extractors.size());
		
		// apply extractors
		List<EventDataExtraction> extractorOutput = extractors.stream().map(func -> func.apply(mockContext)).collect(Collectors.toList());
		
		// run assertions
		for (int i = 0; i < extractors.size(); i++) {
			Consumer<Object> assertion = assertions.get(i);
			EventDataExtraction output = extractorOutput.get(i);
			assertion.accept(output);
		}
	}
}
