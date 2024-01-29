package com.github.fermi4.particle.sse;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Event {

	private String id;
	private String type;
	private String data;

	public static final Schema PARTICLE_KEY_SCHEMA = Schema.OPTIONAL_BYTES_SCHEMA;

	public static final Schema PARTICLE_EVENT_SCHEMA = SchemaBuilder.struct()
			.name("com.github.fermi4.particle.api.domain.ParticleEvent")
			.field("data", Schema.OPTIONAL_STRING_SCHEMA)
			.field("type", Schema.OPTIONAL_STRING_SCHEMA)
			.field("id", Schema.OPTIONAL_STRING_SCHEMA)
			.build();

	public Event(String id, String type, String data) {
		this.id = id;
		this.type = type;
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

	public static Schema getParticleKeySchema() {
		return PARTICLE_KEY_SCHEMA;
	}

	public static Schema getParticleEventSchema() {
		return PARTICLE_EVENT_SCHEMA;
	}

	@Override
	public String toString() {
		return "Event [id=" + id + ", type=" + type + ", data=" + data + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

}
