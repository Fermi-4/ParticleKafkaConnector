package com.github.fermi4.particle.convert;

import java.util.Optional;

import org.apache.kafka.connect.data.Schema;

public class EventDataExtraction {
	Schema schema;
	Object data;

	public EventDataExtraction(Schema schema, Object data) {
		this.schema = schema;
		this.data = data;
	}

	public Optional<Schema> getSchema() {
		return Optional.ofNullable(this.schema);
	}

	public Optional<Object> getData() {
		return Optional.ofNullable(this.data);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
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
		EventDataExtraction other = (EventDataExtraction) obj;
		if (schema == null) {
			if (other.schema != null)
				return false;
		} else if (!schema.equals(other.schema))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

};
