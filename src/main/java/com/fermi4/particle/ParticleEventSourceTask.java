package com.fermi4.particle;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fermi4.particle.convert.SSEEventConverterFactory;
import com.fermi4.particle.sse.SSEEvent;
import com.fermi4.particle.sse.SSEEventProvider;
import com.fermi4.particle.sse.provider.ParticleSSEEventProvider;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticleEventSourceTask extends SourceTask {

	static final Logger log = LoggerFactory.getLogger(ParticleEventSourceTask.class);

	private ParticleConnectorConfig config;
	private SourceRecordConverter<SSEEvent> converter;
	private SSEEventProvider eventProvider;

	@Override
	public String version() {
		return VersionUtil.getVersion();
	}

	@Override
	public void start(Map<String, String> map) {
		if (this.config == null) {
			this.config = new ParticleConnectorConfig(map);
		}
		if (this.converter == null) {
			this.converter = SSEEventConverterFactory.get(config);
		}
		if (this.eventProvider == null) {
			this.eventProvider = new ParticleSSEEventProvider(config);
		}
		this.eventProvider.start();
	}

	@Override
	public List<SourceRecord> poll() throws InterruptedException {
		return this.eventProvider.get().stream().map(this.converter::convert).collect(Collectors.toList());
	}

	@Override
	public void stop() {
		this.eventProvider.stop();
	}
}