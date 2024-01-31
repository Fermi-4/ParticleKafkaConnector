package com.github.fermi4.particle.task;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.fermi4.particle.VersionUtil;
import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.convert.SSEEventConverterFactory;
import com.github.fermi4.particle.convert.SourceRecordConverter;
import com.github.fermi4.particle.sse.SSEEvent;
import com.github.fermi4.particle.sse.SSEEventProvider;
import com.github.fermi4.particle.sse.provider.ParticleSSEEventProvider;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticleServerSentEventSourceTask extends SourceTask {

	static final Logger log = LoggerFactory.getLogger(ParticleServerSentEventSourceTask.class);

	private ParticleConnectorConfig config;
	private SourceRecordConverter<SSEEvent> converter;
	private SSEEventProvider eventProvider;

	@Override
	public String version() {
		return VersionUtil.getVersion();
	}

	@Override
	public void start(Map<String, String> map) {
		/**
		 * When Connect framework creates an instance of this class, this class is 
		 * responsible for bootstrapping itself - but I also want the flexibility to inject 
		 * other implementations/dependencies for testing hence I check first if context 
		 * is not null (Running in Connect runtime).
		 * 
		 * This works so long as my business logic doesn't interact with the context class which 
		 * I then need to test - at that point this approach will need to become more sophisticated
		 * 
		 */
		if(this.context != null) {
			this.config = new ParticleConnectorConfig(map);
			this.converter = SSEEventConverterFactory.get(config);
			this.eventProvider = new ParticleSSEEventProvider(config);
		};
		
		this.eventProvider.start();
	}
	
	@Override
	public List<SourceRecord> poll() throws InterruptedException {
		if(!this.eventProvider.isActive()) {
			System.out.println("[REPLACE WITH LOG][ERROR] event provider is not active - trying to reactivate...");
			int reconnectAttempt=0;
			while(!this.eventProvider.isActive()) {
				System.out.println("[REPLACE WITH LOG][INFO] attempting restart of event provider attp: " + reconnectAttempt);
				this.eventProvider.start();
				reconnectAttempt++;
				Thread.sleep(this.config.getRetryDelay());
				if(!this.eventProvider.isActive()) {
					this.eventProvider.stop();
				}
			}
			
		}
		return this.eventProvider.get().stream().map(this.converter::convert).collect(Collectors.toList());
	}

	@Override
	public void stop() {
		this.eventProvider.stop();
	}
}