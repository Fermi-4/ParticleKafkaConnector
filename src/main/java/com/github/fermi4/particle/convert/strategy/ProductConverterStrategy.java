package com.github.fermi4.particle.convert.strategy;

import org.apache.kafka.connect.source.SourceRecord;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.sse.SSEEvent;

public class ProductConverterStrategy implements SSEEventSourceRecordConverterStrategy {

	private ParticleConnectorConfig config;

	public ProductConverterStrategy(ParticleConnectorConfig config) {
		this.config = config;
	}

	@Override
	public SourceRecord convert(SSEEvent t) {
		return new SourceRecord(
				null, 
				null, 
				this.config.getTopic(), 
				SSEEvent.PARTICLE_KEY_SCHEMA, 
				this.config.getProductId().getBytes(), 
				SSEEvent.PARTICLE_EVENT_SCHEMA, 
				t.getData().getBytes());
	}

}
