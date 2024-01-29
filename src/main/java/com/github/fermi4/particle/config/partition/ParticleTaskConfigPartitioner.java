package com.github.fermi4.particle.config.partition;

import java.util.List;
import java.util.Map;

import com.github.fermi4.particle.config.ParticleConnectorConfig;

/**
 * This lets us encapsulate the logic for splitting the configuration across different number of task
 */
public interface ParticleTaskConfigPartitioner {
	List<Map<String, String>> partition(ParticleConnectorConfig config, int maxTask);
}
