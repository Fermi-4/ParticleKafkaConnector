package com.github.fermi4.particle.config.partition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.connect.util.ConnectorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.fermi4.particle.config.ParticleConnectorConfig;

public class DeviceIdPartitioner implements ParticleTaskConfigPartitioner {

	private static Logger log = LoggerFactory.getLogger(DeviceIdPartitioner.class);
	
	/**
	 * Split the provided device id(s) evenly among the number of task
	 */
	@Override
	public List<Map<String, String>> partition(ParticleConnectorConfig config, int maxTask) {
		
		List<Map<String, String>> configurations = new ArrayList<>();
		
		// Get the comma-separated device IDs from the configuration
        String deviceIdString = config.getDeviceId();
        List<String> deviceIdArray = List.of(deviceIdString.split(ParticleConnectorConfig.DELIMITER));

        // Distribute the device IDs among tasks
        List<List<String>> partitionedDeviceIds = ConnectorUtils.groupPartitions(deviceIdArray, maxTask);
        
        // Create task configurations
        for (List<String> partition : partitionedDeviceIds) {
            Map<String, String> taskConfig = new HashMap<>(config.getOriginalsAsStringWithDefaults());
            taskConfig.put(ParticleConnectorConfig.DEVICE_ID_CONFIG, String.join(ParticleConnectorConfig.DELIMITER, partition));
            
            // log the configuration
            log.info("Created Task Configuration for device partition:");
            taskConfig.forEach((key, value) -> {
            	log.info("\tKey: {}, Value: {}", key, value);
            });
            configurations.add(taskConfig);
        }

        return configurations;
	}
	
}
