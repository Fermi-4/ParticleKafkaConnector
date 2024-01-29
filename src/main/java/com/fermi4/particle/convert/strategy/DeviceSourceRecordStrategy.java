package com.fermi4.particle.convert.strategy;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fermi4.particle.sse.SSEEvent;

public class DeviceSourceRecordStrategy implements SSEEventSourceRecordConverterStrategy {

	private final String deviceId;
	private final Schema valueSchema;
	private final Schema keySchema;
	private final String topic;

	public DeviceSourceRecordStrategy(String deviceId, Schema valueSchema, Schema keySchema, String topic) {
		super();
		this.deviceId = deviceId;
		this.valueSchema = valueSchema;
		this.keySchema = keySchema;
		this.topic = topic;
	}
	
	/**
	 * All events from the same device should go to the same partition
	 * 
	 * Keys the record using passed in deviceId
	 */
	@Override
	public SourceRecord convert(SSEEvent t) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return new SourceRecord(null, null, this.topic, this.keySchema, this.deviceId, this.valueSchema, mapper.writeValueAsString(t));
		} catch (JsonProcessingException e) {
			// TODO logging
			e.printStackTrace();
		}
		return null;
	}

}
