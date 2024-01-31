package com.github.fermi4.particle.convert;

import org.apache.kafka.connect.source.SourceRecord;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.convert.strategy.DefaultConverterStrategy;
import com.github.fermi4.particle.convert.strategy.DeviceSourceRecordStrategy;
import com.github.fermi4.particle.convert.strategy.ProductConverterStrategy;
import com.github.fermi4.particle.sse.SSEEvent;

public class SSEEventConverterFactory {
	
	/**
	 * 
	 * For a given configuration, return the correct converter which is responsible for 
	 * converting {@link SSEEvent} into kafka {@link SourceRecord}
	 * 
	 * @param config
	 * @return {@link SourceRecordConverter}
	 */
	public static SourceRecordConverter<SSEEvent> get(ParticleConnectorConfig config) {
		if(config.getAccessMode() == ParticleConnectorConfig.EVENT_MODE_PRODUCT) {
			if(config.getProductId() != null) {
				// product id provided so key with product id
				return product(config);				
			}
			// device id not defined, key by event type
			return eventType(config);
		} else if(config.getAccessMode() == ParticleConnectorConfig.EVENT_MODE_DEVICE) {
			if(config.getDeviceId() != null) {
				// filtering on device id, so key with device id
				return device(config);				
			}
			// device id not defined, key by event type
			return eventType(config);
		} else {
			return eventType(config);
		}
	}
	
	private static SourceRecordConverter<SSEEvent> eventType(ParticleConnectorConfig config) {
		return new SSEEventSourceRecordConverter(config, new DefaultConverterStrategy(config));
	}
	
	private static SourceRecordConverter<SSEEvent> product(ParticleConnectorConfig config) {
		return new SSEEventSourceRecordConverter(config, new ProductConverterStrategy(config));
	}

	private static SourceRecordConverter<SSEEvent> device(ParticleConnectorConfig config) {
		return new SSEEventSourceRecordConverter(config, new DeviceSourceRecordStrategy(config));
	}
}
