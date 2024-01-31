package com.github.fermi4.particle.config.partition;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.fermi4.particle.config.ParticleConnectorConfig;

public class ProductIdPartitioner implements ParticleTaskConfigPartitioner {

	private static Logger log = LoggerFactory.getLogger(ProductIdPartitioner.class);
	
	@Override
	public List<Map<String, String>> partition(ParticleConnectorConfig config, int maxTask) {
		System.out.println(String.format("Using ProductId Partitioner to distribute task on field %s", ParticleConnectorConfig.PRODUCT_ID_CONFIG));
		return PartitionUtil.distributePartitionsAsMaps(config, ParticleConnectorConfig.PRODUCT_ID_CONFIG, ParticleConnectorConfig.DELIMITER, maxTask);
	}

}
