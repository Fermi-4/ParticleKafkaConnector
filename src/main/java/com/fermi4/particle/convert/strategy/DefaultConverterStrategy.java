package com.fermi4.particle.convert.strategy;

import javax.management.RuntimeErrorException;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fermi4.particle.ParticleConnectorConfig;
import com.fermi4.particle.sse.SSEEvent;

// TODO docs
// TODO might need to explicitly convert to json here
public class DefaultConverterStrategy implements SSEEventSourceRecordConverterStrategy {

	private ParticleConnectorConfig config;
	private Schema valueSchema;
	private Schema keySchema;

	public DefaultConverterStrategy(ParticleConnectorConfig config, Schema keySchema, Schema valueSchema) {
		super();
		this.config = config;
		this.valueSchema = valueSchema;
		this.keySchema = keySchema;
	}

	/**
	 * Sets the key to the event type so events of same 
	 * type will go to same partition for unfiltered case
	 */
	@Override
	public SourceRecord convert(SSEEvent t) {
		try {
			return new SourceRecord(null, null, this.config.getTopic(), this.keySchema, t.getType(), this.valueSchema, t.toJson());
		} catch (JsonProcessingException e) {
			// TODO add formatted printout
			e.printStackTrace();
			throw new RuntimeErrorException(new Error("Error converting SSEEvent payload to json"));
		}
	}
	

}
