package com.fermi4.particle.convert;

import org.apache.kafka.connect.data.Schema;

import com.fermi4.particle.ParticleConnectorConfig;
import com.fermi4.particle.SourceRecordConverter;
import com.fermi4.particle.convert.strategy.DefaultConverterStrategy;
import com.fermi4.particle.convert.strategy.DeviceSourceRecordStrategy;
import com.fermi4.particle.convert.strategy.ProductConverterStrategy;
import com.fermi4.particle.sse.SSEEvent;

public class SSEEventConverterFactory {
	
	public static SourceRecordConverter<SSEEvent> get(ParticleConnectorConfig config) {
		if(config.getAccessMode() == ParticleConnectorConfig.ACCESS_MODE_PRODUCT) {
			if(config.getProductId() != null) {
				return product(config);				
			}
			// device id not defined, key by event type
			return eventType(config);
		} else if(config.getAccessMode() == ParticleConnectorConfig.ACCESS_MODE_DEVICE) {
			if(config.getDeviceId() != null) {
				return device(config);				
			}
			// device id not defined, key by event type
			return eventType(config);
		} else {
			return eventType(config);
		}
	}
	
	private static SourceRecordConverter<SSEEvent> eventType(ParticleConnectorConfig config) {
		System.out.println("Getting eventType");
		return new SSEEventSourceRecordConverter(config, new DefaultConverterStrategy(config, Schema.STRING_SCHEMA, SSEEvent.PARTICLE_EVENT_SCHEMA));
	}
	
	private static SourceRecordConverter<SSEEvent> product(ParticleConnectorConfig config) {
		System.out.println("Getting product");
		return new SSEEventSourceRecordConverter(config, new ProductConverterStrategy(config.getProductId(), Schema.STRING_SCHEMA, SSEEvent.PARTICLE_EVENT_SCHEMA, config.getTopic()));
	}

	private static SourceRecordConverter<SSEEvent> device(ParticleConnectorConfig config) {
		System.out.println("Getting device");
		return new SSEEventSourceRecordConverter(config, new DeviceSourceRecordStrategy(config.getDeviceId(), Schema.STRING_SCHEMA, SSEEvent.PARTICLE_EVENT_SCHEMA, config.getTopic()));
	}
}
