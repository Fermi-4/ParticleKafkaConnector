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

public class EventConverter implements Function<Event, SourceRecord> {

	private ParticleConnectorConfig config;

	public EventConverter() {
	}

	public ParticleConnectorConfig getConfig() {
		return config;
	}

	public void setConfig(ParticleConnectorConfig config) {
		this.config = config;
	}

	public EventConverter(ParticleConnectorConfig config) {
		this.config = config;
	}

	@Override
	public SourceRecord apply(Event t) {
		return this.getConverter().apply(t);
	}

	private Function<Event, SourceRecord> getConverter() {
		Objects.requireNonNull(this.config);

		/**
		 * when config dependencies get more complex we can move this into factory
		 * there is no input for source partition/offsets or anything else besides
		 * key/value
		 * so for now this is fine
		 */
		return SourceRecordConverter.builder()
				.topicExtractor(this.getTopicExtractor())
				.keyExtractor(this.getKeyExtractor())
				.valueExtractor(e -> new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA,
						e.getData().getBytes()))
				.build();
	}

	private Function<Event, String> getTopicExtractor() {
		switch (config.getTopicMode()) {
			case ParticleConnectorConfig.TOPIC_CONFIG_MODE_CONFIGURATION:
				return e -> config.getTopic();
			case ParticleConnectorConfig.TOPIC_CONFIG_MODE_PRODUCT_ID_CONFIG:
				return e -> config.getProductId();
			case ParticleConnectorConfig.TOPIC_CONFIG_MODE_EVENT_TYPE:
				return e -> e.getType();
			case ParticleConnectorConfig.TOPIC_CONFIG_MODE_DEVICE_ID_CONFIG:
				return e -> config.getDeviceId();
			case ParticleConnectorConfig.TOPIC_CONFIG_MODE_CORE_ID:
				return this::extractPayloadCoreId;
			default:
				throw new ConnectException(new IllegalArgumentException(
						"Unexpected value: " + config.getTopicMode()));
		}
	}

	private Function<Event, EventDataExtraction> getKeyExtractor() {
		switch (config.getConnectorKeyMode()) {
			case ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_PAYLOAD_COREID:
				return e -> new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA,
						extractPayloadCoreId(e).getBytes());
			case ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_DEVICE_ID_CONFIG:
				return e -> new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA,
						this.config.getDeviceId().getBytes());
			case ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_PRODUCT_ID_CONFIG:
				return e -> new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA,
						this.config.getProductId().getBytes());
			case ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_EVENT_TYPE:
				return e -> new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA,
						e.getType().getBytes());
			case ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_NONE:
				return e -> new EventDataExtraction(null, null);
			default:
				throw new ConnectException(new IllegalArgumentException(
						"Unexpected value: " + config.getConnectorKeyMode()));
		}
	}

	public String extractPayloadCoreId(Event event) {
		String eventJson = event.getData();
		JsonNode node = convertJsonString(eventJson);
		JsonNode coreId = node.get("coreid");
		return coreId.asText();
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
