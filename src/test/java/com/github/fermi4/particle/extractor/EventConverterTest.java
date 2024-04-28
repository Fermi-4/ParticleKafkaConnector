package com.github.fermi4.particle.extractor;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.ACCESS_TOKEN_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.DEVICE_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_DEVICE;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.PRODUCT_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.TOPIC_CONFIG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.convert.EventConverter;
import com.github.fermi4.particle.convert.EventDataExtraction;
import com.github.fermi4.particle.sse.Event;

public class EventConverterTest {
	
	private EventConverter eventConverter;
    private ParticleConnectorConfig mockConfig;
	
	// Event
	String mockSseJson = "{\"data\":\"23.34\",\"ttl\":\"60\",\"published_at\":\"2015-07-18T00:12:18.174Z\",\"coreid\":\"0123456789abcdef01234567\"}\n\n";
	String mockEventType = "mock_type";
	String mockEventId = "mock_id";
	String mockCoreId = "0123456789abcdef01234567"; // this is from above sse json string
	Event mockEvent = new Event(mockEventId, mockEventType, mockSseJson);

	// Context
	String mockDeviceId = "mock_device_id";
	String mockProductId = "mock_product_id";

	@BeforeEach
	public void setUp() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
		map.put(TOPIC_CONFIG, "test");
		map.put(ACCESS_TOKEN_CONFIG, "test");
		map.put(DEVICE_ID_CONFIG, mockDeviceId);
		map.put(PRODUCT_ID_CONFIG, mockProductId);
		mockConfig = new ParticleConnectorConfig(map);
	}

	@Test
	public void testAllEventConverterMethods() {
		EventConverter converter = new EventConverter(mockConfig);

		List<Function<Event, EventDataExtraction>> extractors = Arrays.asList(
				converter::extractConfigDeviceIdBytes, 
				converter::extractConfigProductIdBytes,
				converter::extractPayloadCoreIdBytes, 
				converter::extractEventTypeBytes,
				converter::extractEventDataBytes, 
				converter::extractNull);

		List<Consumer<Object>> assertions = Arrays.asList(
				(d) -> Assertions.assertArrayEquals(mockDeviceId.getBytes(), (byte[]) ((EventDataExtraction) d).getData().get()),
				(d) -> Assertions.assertArrayEquals(mockProductId.getBytes(),(byte[]) ((EventDataExtraction) d).getData().get()),
				(d) -> Assertions.assertArrayEquals(mockCoreId.getBytes(), (byte[]) ((EventDataExtraction) d).getData().get()),
				(d) -> Assertions.assertArrayEquals(mockEventType.getBytes(), (byte[]) ((EventDataExtraction) d).getData().get()),
				(d) -> Assertions.assertArrayEquals(mockSseJson.getBytes(), (byte[]) ((EventDataExtraction) d).getData().get()),
				(d) -> Assertions.assertEquals(null, ((EventDataExtraction) d).getData().orElse(null)));

		Assertions.assertEquals(assertions.size(), extractors.size());
		
		// apply extractors
		List<EventDataExtraction> extractorOutput = extractors.stream().map(func -> func.apply(mockEvent)).collect(Collectors.toList());
		
		// run assertions
		for (int i = 0; i < extractors.size(); i++) {
			Consumer<Object> assertion = assertions.get(i);
			EventDataExtraction output = extractorOutput.get(i);
			assertion.accept(output);
		}
	}

    @Test
    public void testConstructorAndGettersSetters() {
        // Test constructor with no arguments
        EventConverter converter1 = new EventConverter();
        assertEquals(null, converter1.getConfig());

        // Test constructor with arguments
        // ParticleConnectorConfig config = new ParticleConnectorConfig();
        EventConverter converter2 = new EventConverter(mockConfig);
        assertEquals(mockConfig, converter2.getConfig());

        // Test getters and setters
        EventConverter converter3 = new EventConverter();
        converter3.setConfig(mockConfig);
        assertEquals(mockConfig, converter3.getConfig());
    }

    @Test
    public void testApplyWithNonNullConfig() {

		mockConfig = mock(ParticleConnectorConfig.class);
		eventConverter = new EventConverter();
		eventConverter.setConfig(mockConfig);

        // Mock configuration
        when(mockConfig.getTopic()).thenReturn("test-topic");
        when(mockConfig.getConnectorKeyMode()).thenReturn(ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_EVENT_TYPE);

        // Mock event
        Event mockEvent = new Event("id","eventType", "eventData");

        // Test apply method
        SourceRecord result = eventConverter.apply(mockEvent);

        // Verify result
        assertEquals("test-topic", result.topic());
        // Additional assertions can be added depending on the logic of your apply method
    }

    @Test
    public void testApplyWithNullConfig() {

		mockConfig = mock(ParticleConnectorConfig.class);
		eventConverter = new EventConverter();
		eventConverter.setConfig(mockConfig);

        // Set config to null
        eventConverter.setConfig(null);

        // Mock event
        Event mockEvent = new Event("id", "eventType", "eventData");

        // Test apply method and expect ConnectException
        assertThrows(NullPointerException.class, () -> {
            eventConverter.apply(mockEvent);
        });
    }
}
