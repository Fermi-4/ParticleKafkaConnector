package com.github.fermi4.particle.task;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.fermi4.particle.VersionUtil;
import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.convert.ConverterContext;
import com.github.fermi4.particle.sse.EventProvider;
import com.github.fermi4.particle.sse.EventProviderFactory;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 
 * This same task can be configured for sourcing multiple types of events
 * 	- product/device/all
 *  - multiple devices/products
 *  
 * This pushes the configuration complexity into the dependencies..
 * I could have instead more 'pointed' task which are tailored for 
 * a specific use case.. i.e. device ID source task etc..
 * 
 * so this would become an abstract class from which others would inherit
 * 
 * AbstractSSETask => ParticleSSEDeviceTask ... and so on..
 * but this would lead to larger number of classes to manage so.. trying to do one for all
 *  
 */
@Getter
@Setter
public class ParticleServerSentEventSourceTask extends SourceTask {

	static final Logger log = LoggerFactory.getLogger(ParticleServerSentEventSourceTask.class);

	private ParticleConnectorConfig config;
	private EventProvider eventProvider;
	private ConverterContext converterContext;
	private Function<ConverterContext, SourceRecord> converter;

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
			this.eventProvider = EventProviderFactory.get(config);
			this.converterContext = new ConverterContext();
			this.converterContext.setConfig(config);			
		};
		
		 // Print key-value pairs in the map
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
		this.eventProvider.start();
	}
	
	@Override
	public List<SourceRecord> poll() throws InterruptedException {
		return eventProvider.get().stream().map((event) -> {
			converterContext.setEvent(event);
			return converter.apply(converterContext);
		})
		.collect(Collectors.toList());
	}

	@Override
	public void stop() {
		this.eventProvider.stop();
	}
	
}