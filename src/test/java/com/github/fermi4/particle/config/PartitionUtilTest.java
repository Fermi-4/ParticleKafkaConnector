package com.github.fermi4.particle.config;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.ACCESS_TOKEN_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.DEVICE_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_DEVICE;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.TOPIC_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.DELIMITER_DEFAULT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.config.partition.PartitionUtil;

public class PartitionUtilTest {

	@Test
	public void testPartitionConfigOnFieldDelimiter() {

		int EXPECTED_NUM_CONFIGS = 2;
		String DEVICE1 = "device1";
		String DEVICE2 = "device2";

		/* Device configuration */
		Map<String, String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
		map.put(TOPIC_CONFIG, "test");
		map.put(ACCESS_TOKEN_CONFIG, "key");
		map.put(DEVICE_ID_CONFIG, DEVICE1 + DELIMITER_DEFAULT + DEVICE2);
		ParticleConnectorConfig originalConfig = new ParticleConnectorConfig(map);

		/* partition the config by device id */
		List<ParticleConnectorConfig> partitionedConfigs = PartitionUtil.partitionConfigOnFieldDelimiter(originalConfig,
				DEVICE_ID_CONFIG, DELIMITER_DEFAULT);

		/* assertions */
		Assertions.assertEquals(partitionedConfigs.size(), EXPECTED_NUM_CONFIGS);
		Assertions.assertTrue(partitionedConfigs.stream()
				.map(c -> c.getDeviceId()).collect(Collectors.toList())
				.containsAll(List.of(DEVICE1, DEVICE2)));

	}
	
	@Test
	public void testDistributePartitions() {

		int EXPECTED_NUM_CONFIGS = 2;
		String DEVICE1 = "device1";
		String DEVICE2 = "device2";

		/* Device configuration */
		Map<String, String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
		map.put(TOPIC_CONFIG, "test");
		map.put(ACCESS_TOKEN_CONFIG, "key");
		map.put(DEVICE_ID_CONFIG, DEVICE1 + DELIMITER_DEFAULT + DEVICE2);
		ParticleConnectorConfig originalConfig = new ParticleConnectorConfig(map);

		/* partition the config by device id */
		List<ParticleConnectorConfig> partitionedConfigs = PartitionUtil.distributePartitionsAsConfig(originalConfig, DEVICE_ID_CONFIG, DELIMITER_DEFAULT, 3);

		/* assertions */
		Assertions.assertEquals(partitionedConfigs.size(), EXPECTED_NUM_CONFIGS);
		Assertions.assertTrue(partitionedConfigs.stream()
				.map(c -> c.getDeviceId()).collect(Collectors.toList())
				.containsAll(List.of(DEVICE1, DEVICE2)));

	}
}
