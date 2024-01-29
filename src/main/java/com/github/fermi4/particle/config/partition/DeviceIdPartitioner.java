package com.github.fermi4.particle.config.partition;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.fermi4.particle.config.ParticleConnectorConfig;

public class DeviceIdPartitioner implements ParticleTaskConfigPartitioner {
	
	private static Logger log = LoggerFactory.getLogger(DeviceIdPartitioner.class);
	
	@Override
	public List<Map<String, String>> partition(ParticleConnectorConfig config, int maxTask) {
		log.info("Using Device ID Partitioner to distribute task on field "+ ParticleConnectorConfig.DEVICE_ID_CONFIG);
		return PartitionUtil.distributePartitionsAsMaps(config, ParticleConnectorConfig.DEVICE_ID_CONFIG, config.getDelimiter(), maxTask);
	}
	
}
