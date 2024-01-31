package com.github.fermi4.particle.convert;

import java.util.Map;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.Headers;
import org.apache.kafka.connect.source.SourceRecord;

public class SourceRecordBuilder {
	
	private Map<String, ?> sourcePartition;
    private Map<String, ?> sourceOffset;
    private String topic;
    private Integer kafkaPartition;
    private Schema keySchema;
    private Object key;
    private Schema valueSchema;
    private Object value;
    private Long timestamp;
    private Headers headers;
    
	public void setSourcePartition(Map<String, ?> sourcePartition) {
		this.sourcePartition = sourcePartition;
	}

	public void setSourceOffset(Map<String, ?> sourceOffset) {
		this.sourceOffset = sourceOffset;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public void setKafkaPartition(Integer kafkaPartition) {
		this.kafkaPartition = kafkaPartition;
	}

	public void setKeySchema(Schema keySchema) {
		this.keySchema = keySchema;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public void setValueSchema(Schema valueSchema) {
		this.valueSchema = valueSchema;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public void setHeaders(Headers headers) {
		this.headers = headers;
	}

	public SourceRecord build() {
		return new SourceRecord(
				sourcePartition,
				sourceOffset,
				topic,
				kafkaPartition,
				keySchema,
				key,
				valueSchema,
				value,
				timestamp,
				headers);
	}
}
