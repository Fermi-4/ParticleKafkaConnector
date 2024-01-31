package com.github.fermi4.particle.convert;

import org.apache.kafka.connect.source.SourceRecord;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.convert.strategy.SSEEventSourceRecordConverterStrategy;
import com.github.fermi4.particle.sse.SSEEvent;

public class SSEEventSourceRecordConverter implements SourceRecordConverter<SSEEvent> {

	private ParticleConnectorConfig config;
	private SSEEventSourceRecordConverterStrategy strategy;

	public SSEEventSourceRecordConverter(ParticleConnectorConfig config,
			SSEEventSourceRecordConverterStrategy strategy) {
		super();
		this.config = config;
		this.strategy = strategy;
	}

	@Override
	public SourceRecord convert(SSEEvent t) {
		return this.strategy.convert(t);
	}

}
