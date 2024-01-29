package com.fermi4.particle.convert.strategy;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fermi4.particle.sse.SSEEvent;

public class ProductConverterStrategy implements SSEEventSourceRecordConverterStrategy {
	
	private final String deviceId;
	private final Schema valueSchema;
	private final Schema keySchema;
	private final String topic;
	
	public ProductConverterStrategy(String deviceId, Schema keySchema, Schema valueSchema, String topic) {
		super();
		this.deviceId = deviceId;
		this.valueSchema = valueSchema;
		this.keySchema = keySchema;
		this.topic = topic;
	}

	@Override
	public SourceRecord convert(SSEEvent t) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return new SourceRecord(null, null, this.topic, this.keySchema, this.deviceId, this.valueSchema, mapper.writeValueAsString(t));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
