package com.github.fermi4.particle.extractor;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.Header;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.convert.EventDataExtraction;
import com.github.fermi4.particle.convert.SourceRecordConverter;
import com.github.fermi4.particle.sse.Event;

public class SourceRecordConverterBuilderTest {
        private Event mockEvent;

    @BeforeEach
    public void setUp() {
        mockEvent = mock(Event.class);
    }

    @Test
    public void testDefaultBuilder() {
        SourceRecordConverter converter = SourceRecordConverter.builder().build();

        SourceRecord record = converter.apply(mockEvent);

        // Verify default behavior
        assertNotNull(record);
        assertEquals(Schema.OPTIONAL_BYTES_SCHEMA, record.keySchema());
        assertEquals(Schema.OPTIONAL_BYTES_SCHEMA, record.valueSchema());
        assertEquals(null, record.key());
        assertEquals(null, record.value());
    }

    @Test
    public void testCustomBuilder() {
        Function<Event, Map<String, ?>> mockPartitionMapExtractor = e -> Collections.singletonMap("partitionKey", "partitionValue");
        Function<Event, Map<String, ?>> mockOffsetsMapExtractor = e -> Collections.singletonMap("offsetKey", "offsetValue");
        Function<Event, EventDataExtraction> mockKeyExtractor = e -> new EventDataExtraction(Schema.STRING_SCHEMA, "keyData");
        Function<Event, EventDataExtraction> mockValueExtractor = e -> new EventDataExtraction(Schema.STRING_SCHEMA, "valueData");
        Function<Event, Integer> mockPartitionExtractor = e -> 0;
        Function<Event, String> mockTopicExtractor = e -> "test-topic";
        Function<Event, Long> mockTimestampExtractor = e -> System.currentTimeMillis();
        Function<Event, Iterable<Header>> mockHeadersExtractor = e -> Collections.emptyList();

        SourceRecordConverter converter = SourceRecordConverter.builder()
                .partitionMapExtractor(mockPartitionMapExtractor)
                .offsetsMapExtractor(mockOffsetsMapExtractor)
                .keyExtractor(mockKeyExtractor)
                .valueExtractor(mockValueExtractor)
                .partitionExtractor(mockPartitionExtractor)
                .topicExtractor(mockTopicExtractor)
                .timestampExtractor(mockTimestampExtractor)
                .headersExtractor(mockHeadersExtractor)
                .build();

        SourceRecord record = converter.apply(mockEvent);

        // Verify custom behavior
        assertNotNull(record);
        assertEquals(Schema.STRING_SCHEMA, record.keySchema());
        assertEquals("keyData", record.key());
        assertEquals(Schema.STRING_SCHEMA, record.valueSchema());
        assertEquals("valueData", record.value());
        assertEquals("test-topic", record.topic());
        assertEquals(0, record.kafkaPartition());
        assertNotNull(record.timestamp());
        assertTrue(record.headers().isEmpty());
    }

    @Test
    public void testNullExtractors() {
        SourceRecordConverter converter = SourceRecordConverter.builder()
                .partitionMapExtractor(null)
                .offsetsMapExtractor(null)
                .keyExtractor(null)
                .valueExtractor(null)
                .partitionExtractor(null)
                .topicExtractor(null)
                .timestampExtractor(null)
                .headersExtractor(null)
                .build();

        // Ensure that apply method throws NullPointerException due to null extractors
        NullPointerException exception = null;
        try {
            converter.apply(mockEvent);
        } catch (NullPointerException e) {
            exception = e;
        }

        assertNotNull(exception);
    }
}
