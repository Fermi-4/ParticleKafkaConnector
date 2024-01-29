package com.github.fermi4.particle.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.apache.kafka.connect.data.Schema;
import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.convert.EventDataExtraction;

public class EventDataExtractorTest {

    @Test
    public void testConstructorAndGetters() {
        Schema schema = Schema.STRING_SCHEMA;
        String data = "test-data";

        EventDataExtraction eventDataExtraction = new EventDataExtraction(schema, data);

        // Test getSchema method
        Optional<Schema> schemaOptional = eventDataExtraction.getSchema();
        assertTrue(schemaOptional.isPresent());
        assertEquals(schema, schemaOptional.get());

        // Test getData method
        Optional<Object> dataOptional = eventDataExtraction.getData();
        assertTrue(dataOptional.isPresent());
        assertEquals(data, dataOptional.get());
    }

    @Test
    public void testConstructorWithNullValues() {
        EventDataExtraction eventDataExtraction = new EventDataExtraction(null, null);

        // Test getSchema method
        Optional<Schema> schemaOptional = eventDataExtraction.getSchema();
        assertFalse(schemaOptional.isPresent());

        // Test getData method
        Optional<Object> dataOptional = eventDataExtraction.getData();
        assertFalse(dataOptional.isPresent());
    }

    @Test
    public void testEqualsAndHashCode() {
        Schema schema1 = Schema.STRING_SCHEMA;
        String data1 = "data1";
        Schema schema2 = Schema.INT32_SCHEMA;
        String data2 = "data2";

        EventDataExtraction eventDataExtraction1 = new EventDataExtraction(schema1, data1);
        EventDataExtraction eventDataExtraction2 = new EventDataExtraction(schema1, data1);
        EventDataExtraction eventDataExtraction3 = new EventDataExtraction(schema2, data2);

        // Test equals method
        assertEquals(eventDataExtraction1, eventDataExtraction2);
        assertNotEquals(eventDataExtraction1, eventDataExtraction3);

        // Test hashCode method
        assertEquals(eventDataExtraction1.hashCode(), eventDataExtraction2.hashCode());
        assertNotEquals(eventDataExtraction1.hashCode(), eventDataExtraction3.hashCode());
    }
}
