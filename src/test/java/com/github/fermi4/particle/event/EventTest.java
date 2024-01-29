package com.github.fermi4.particle.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fermi4.particle.sse.Event;

public class EventTest {

	@Test
	public void testConstructorFull() {
		Event event = new Event("id", "type", "data");
	}

	@Test
	public void testGettersAndSetters() {
		Event event = new Event("id", "type", "data");

		event.setData("123");
		event.setId("123");
		event.setType("123");
		assertEquals("123", event.getData());
		assertEquals("123", event.getType());
		assertEquals("123", event.getId());
	}

	@Test
	public void testEqualsAndHashCode() {
		Event event1 = new Event("id", "type", "data");
		Event event2 = new Event("id", "type", "data");
		assertEquals(event1.hashCode(), event2.hashCode());
		assertTrue(event1.equals(event2));
		assertTrue(event2.equals(event1));
	}

	@Test
	public void testToJson() throws JsonProcessingException {
		Event event = new Event("id", "type", "data");
		assertEquals("{\"id\":\"id\",\"type\":\"type\",\"data\":\"data\"}", event.toJson());
	}

	@Test
	public void testToStruct() throws JsonProcessingException {
		Event event = new Event("id", "type", "data");
		Struct expected = new Struct(Event.PARTICLE_EVENT_SCHEMA)
				.put("data", event.getData())
				.put("type", event.getType())
				.put("id", event.getId());

		assertEquals(expected, event.toStruct());
	}

	@Test
	public void testToString() throws JsonProcessingException {
		Event event = new Event("id", "type", "data");
		String expected = "Event [id=id, type=type, data=data]";
		// Event [id=id, type=type, data=data]
		System.out.println(event.toString());
		assertEquals(expected, event.toString());
	}

}
