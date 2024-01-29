package com.fermi4.particle.convert;

import org.apache.kafka.connect.source.SourceRecord;

import com.fermi4.particle.ParticleConnectorConfig;
import com.fermi4.particle.SourceRecordConverter;
import com.fermi4.particle.convert.strategy.SSEEventSourceRecordConverterStrategy;
import com.fermi4.particle.sse.SSEEvent;

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
