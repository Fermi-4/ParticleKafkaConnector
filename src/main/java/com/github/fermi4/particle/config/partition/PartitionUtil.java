package com.github.fermi4.particle.config.partition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.connect.util.ConnectorUtils;

import com.github.fermi4.particle.config.ParticleConnectorConfig;

/**
 * This utility class includes methods to partition configurations into multiple sub-configurations 
 * and distribute them across tasks, which is useful for scaling and parallel processing.
 * 
 * @author Fermi-4
 * 
 */
public class PartitionUtil {
	
	/**
     * Partitions a configuration based on a specified delimiter.
     * 
     * @param config the original ParticleConnectorConfig
     * @param key the configuration key to split
     * @param delimiter the delimiter used to split the value
     * @return a list of ParticleConnectorConfig objects with split values
     */
	public static List<ParticleConnectorConfig> partitionConfigOnFieldDelimiter(ParticleConnectorConfig config, String key, String delimiter) {
		
		String configValue = config.getOriginalsAsStringWithDefaults().get(key);
        if (configValue == null) {
            throw new IllegalArgumentException("Key " + key + " not found in configuration");
        }
		List<ParticleConnectorConfig> listOfConfigs = List
				.of(config.getOriginalsAsStringWithDefaults().get(key).split(delimiter))
				.stream()
				.map(value -> createNewMapFromSplit(config, key, value))
				.map(ParticleConnectorConfig::new)
				.collect(Collectors.toList());
		return listOfConfigs;
	}
	
	/**
     * Distributes configurations across a specified number of tasks.
     * 
     * @param config the original ParticleConnectorConfig
     * @param key the configuration key to split and distribute
     * @param delimiter the delimiter used to split the value
     * @param maxTask the maximum number of tasks
     * @return a list of ParticleConnectorConfig objects distributed across tasks
     */
	public static List<ParticleConnectorConfig> distributePartitionsAsConfig(ParticleConnectorConfig config, String key, String delimiter, int maxTask) {
		return distributePartitionsAsMaps(config, key, delimiter, maxTask)
				.stream()
				.map(ParticleConnectorConfig::new).collect(Collectors.toList());
	}
	
	/**
     * Distributes configurations as maps across a specified number of tasks.
     * 
     * @param config the original ParticleConnectorConfig
     * @param key the configuration key to split and distribute
     * @param delimiter the delimiter used to split the value
     * @param maxTask the maximum number of tasks
     * @return a list of configuration maps distributed across tasks
     *
     */
	public static List<Map<String, String>> distributePartitionsAsMaps(ParticleConnectorConfig config, String key, String delimiter, int maxTask) {
		// 
		// Example
		//
		// split the target "key" using delimiter ie. for key device_id and comma delimiter and 3 tasks:
		//
		// - "device_id" => "1,2,3";     initial mapping
		// - ["1", "2", "3"]       ;     split into list
		// - [["1"], ["2"], ["3"]] ;     distribute evenly across 3 tasks
		// - [<map1>,<map2>,<map3>];     return task map for each with "device_id" targeting the assigned devices
		//
		// Each task map will become a task in the connect cluster which effectively parallelizes the job
		//
		List<String> entries = List.of(config.getOriginalsAsStringWithDefaults().get(key).split(delimiter));
		List<List<String>> partitionedEntries = ConnectorUtils.groupPartitions(entries, maxTask);
		return partitionedEntries.stream()
				.filter(p -> !p.isEmpty())
				.map(p->String.join(delimiter, p))
				.map(p->{
					Map<String, String> map = createCopyOfMap(config.getOriginalsAsStringWithDefaults());
					map.put(key, p);
					return map;
				}).collect(Collectors.toList());
	
	}
	
	/**
     * Creates a new map with the split value for a specific key.
     * 
     * @param config the original ParticleConnectorConfig
     * @param key the configuration key to split
     * @param value the split value
     * @return a new configuration map with the updated key-value pair
     */
	private static Map<String, String> createNewMapFromSplit(ParticleConnectorConfig config, String key, String value) {
        Map<String, String> originals = createCopyOfMap(config.getOriginalsAsStringWithDefaults());
        originals.put(key, value);
        return originals;
    }

	/**
     * Creates a copy of a configuration map.
     * 
     * @param originalMap the original configuration map
     * @return a copy of the configuration map
     */
    private static Map<String, String> createCopyOfMap(Map<String, String> originalMap) {
        return originalMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
