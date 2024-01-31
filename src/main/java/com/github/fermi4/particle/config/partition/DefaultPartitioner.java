package com.github.fermi4.particle.config.partition;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.fermi4.particle.config.ParticleConnectorConfig;

public class DefaultPartitioner implements ParticleTaskConfigPartitioner {
	
	private static Logger log = LoggerFactory.getLogger(DefaultPartitioner.class);
	
	@Override
	public List<Map<String, String>> partition(ParticleConnectorConfig config, int maxTask) {
		if (maxTask > 1) {
			log.info("Using DefaultPartitioner... There is only one task which will be created [requested: %d]", maxTask);
		}
		Map<String, String> configurationWithDefaults = config.getOriginalsAsStringWithDefaults();

		// log the configuration
		log.info("Printing configurationWithDefaults:");
		configurationWithDefaults.forEach((key, value) -> {
		    log.info("\tKey: {}, Value: {}", key, value);
		});
		
		return Collections.singletonList(config.originalsStrings());
	}

}
