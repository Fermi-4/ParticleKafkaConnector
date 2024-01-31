package com.github.fermi4.particle.convert.extract;

import java.util.function.Function;

import org.apache.kafka.connect.errors.ConnectException;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.convert.ConverterContext;

public class EventDataExtractorFactory {
	
	public static Function<ConverterContext, EventDataExtraction>  getKeyExtractor(ParticleConnectorConfig config) {
		switch (config.getConnectorKeyMode()) {
		case ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_COREID: {
			return EventDataExtractors.extractConfigDeviceIdBytes();
		}
		case ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_DEVICE: {
			return EventDataExtractors.extractConfigDeviceIdBytes();
		}
		case ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_PRODUCT: {
			return EventDataExtractors.extractConfigProductIdBytes();
		}
		case ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_EVENT: {
			return EventDataExtractors.extractEventTypeBytes();
		}
		case ParticleConnectorConfig.PARTICLE_CONNECTOR_KEY_NONE: {
			return EventDataExtractors.extractNull();
		}
		default:
			throw new ConnectException(new IllegalArgumentException("Unexpected value: " + config.getConnectorKeyMode()));
		}
	}
	
	public static Function<ConverterContext, EventDataExtraction>  getValueExtractor(ParticleConnectorConfig config) {
		return EventDataExtractors.extractEventDataBytes();
	}
}
