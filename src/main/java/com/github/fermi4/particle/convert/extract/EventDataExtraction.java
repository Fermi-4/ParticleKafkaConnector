package com.github.fermi4.particle.convert.extract;

import java.util.Optional;

import org.apache.kafka.connect.data.Schema;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class EventDataExtraction {
	Schema schema; 
	Object data;
	
	public Optional<Schema> getSchema() {
		return Optional.ofNullable(this.schema);
	}
	
	public Optional<Object> getData() {
		return Optional.ofNullable(this.data);
	}
};
