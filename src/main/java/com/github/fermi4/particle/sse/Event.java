package com.github.fermi4.particle.sse;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
