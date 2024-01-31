package com.fermi4.particle;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fermi4.particle.convert.SSEEventConverterFactory;
import com.fermi4.particle.convert.SourceRecordConverter;
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
		/**
		 * When Connect framework creates an instance of this class, this class is 
		 * responsible for bootstrapping itself - but I also want the flexibility to inject 
		 * other implementations/dependencies for testing hence I check first if context 
		 * is not null (Running in Connect runtime).
		 * 
		 * This works so long as my business logic doesn't interact with the context class 
		 * - at that point this approach will need to become a little more sophisticated
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
		/**
		 * Monitor if our source is still active or not
		 * If it isn't, try to restart until active
		 */
		if(!this.eventProvider.isActive()) {
			System.out.println("[REPLACE WITH LOG][ERROR] event provider is not active - trying to reactivate...");
			int reconnectAttempt=0;
			while(!this.eventProvider.isActive() && reconnectAttempt < this.config.getRetryAttempts()) {
				this.eventProvider.start();
				reconnectAttempt++;
				Thread.sleep(this.config.getRetryDelay());
			}
			
		}
		return this.eventProvider.get().stream().map(this.converter::convert).collect(Collectors.toList());
	}

	@Override
	public void stop() {
		this.eventProvider.stop();
	}
}