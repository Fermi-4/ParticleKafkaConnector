package com.github.fermi4.particle.sse;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Event {
	
	private String id;
	private String type;
	private String data;
	
	public static final Schema PARTICLE_KEY_SCHEMA = Schema.OPTIONAL_BYTES_SCHEMA;
	public static final Schema PARTICLE_EVENT_SCHEMA = Schema.OPTIONAL_BYTES_SCHEMA;
//	public static final Schema PARTICLE_EVENT_SCHEMA = SchemaBuilder.struct()
//            .name(SSEEvent.class.getSimpleName())
//            .field("id", Schema.OPTIONAL_STRING_SCHEMA)
//            .field("type", Schema.OPTIONAL_STRING_SCHEMA)
//            .field("data", Schema.OPTIONAL_STRING_SCHEMA)
//            .build();
	
	public Event(String id, String type, String data) {
		super();
		this.id = id;
		this.type = type;
		this.data = data;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	public Struct toStruct() {
		return new Struct(PARTICLE_EVENT_SCHEMA)
				.put("data", this.getData())
				.put("type", this.getType())
				.put("id", this.getId());
	}
	
	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
	
	
}
