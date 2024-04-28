package com.github.fermi4.particle.convert;

import java.util.Objects;
import java.util.function.Function;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.sse.Event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventConverter implements Function<Event, SourceRecord> {

	private ParticleConnectorConfig config;
	
	@Override
	public SourceRecord apply(Event t) {
		return this.getConverter().apply(t);
	}
	
	private Function<Event, SourceRecord> getConverter() {
		Objects.requireNonNull(this.config);

		/**
		 * when config dependencies get more complex we can move this into factory
		 * there is no input for source partition/offsets or anything else besides key/value 
		 * so for now this is fine
		 */ 
		return SourceRecordConverter.builder()
				.topicExtractor(event -> config.getTopic())
				.keyExtractor(this.getKeyExtractor(this.config))
				.valueExtractor(this::extractEventDataBytes)
				.build();
	}
	
	private Function<Event, EventDataExtraction>  getKeyExtractor(ParticleConnectorConfig config) {
		switch (config.getConnectorKeyMode()) {
		case ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_PAYLOAD_COREID: {
			System.out.println("PARTICLE_CONNECTOR_KEY_COREID");
			return this::extractPayloadCoreIdBytes;
		}
		case ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_DEVICE_ID_CONFIG: {
			System.out.println("PARTICLE_CONNECTOR_KEY_DEVICE");
			return this::extractConfigDeviceIdBytes;
		}
		case ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_PRODUCT_ID_CONFIG: {
			System.out.println("PARTICLE_CONNECTOR_KEY_PRODUCT");
			return this::extractConfigProductIdBytes;
		}
		case ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_EVENT_TYPE: {
			System.out.println("PARTICLE_CONNECTOR_KEY_EVENT");
			return this::extractEventTypeBytes;
		}
		case ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_NONE: {
			System.out.println("PARTICLE_CONNECTOR_KEY_NONE");
			return this::extractNull;
		}
		default:
			throw new ConnectException(new IllegalArgumentException("Unexpected value: " + config.getConnectorKeyMode()));
		}
	}
	
	public EventDataExtraction extractNull(Event event) {
		return new EventDataExtraction(null, null);
	}
	
	public EventDataExtraction extractEventTypeBytes(Event event) {
		return new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA, event.getType().getBytes());
	}
	
	public EventDataExtraction extractEventDataBytes(Event event) {
		return new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA, event.getData().getBytes());
	}
	
	public EventDataExtraction extractConfigDeviceIdBytes(Event event) {
		return new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA, this.config.getDeviceId().getBytes());
	}
	
	public EventDataExtraction extractConfigProductIdBytes(Event event) {
		return new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA, this.config.getProductId().getBytes());
	}
	
	public EventDataExtraction extractPayloadCoreIdBytes(Event event) {
		System.out.println("extractPayloadCoreIdBytes: " + event.toString());
		Objects.requireNonNull(event);
		String eventJson = event.getData();
		Objects.requireNonNull(eventJson);
		JsonNode node = convertJsonString(eventJson);
		JsonNode coreId = node.get("coreid");
		Objects.requireNonNull(coreId);
		return new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA, coreId.asText().getBytes());
	}
	
	private JsonNode convertJsonString(String jsonString) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readTree(jsonString); 
		} catch (JsonProcessingException e) {
			throw new ConnectException(e);
		}
	}
	
}
