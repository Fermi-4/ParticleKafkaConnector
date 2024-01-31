package com.github.fermi4.particle.config.partition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.connect.util.ConnectorUtils;

import com.github.fermi4.particle.config.ParticleConnectorConfig;

/**
 * TODO: remove the skipEmpty flag in distributePartitionsAsMaps - is there ever a case where we want to keep the empty configs?
 */
public class PartitionUtil {
	
	public static List<ParticleConnectorConfig> partitionConfigOnFieldDelimiter(ParticleConnectorConfig config, String key,
			String delimiter) {
		List<ParticleConnectorConfig> listOfConfigs = List
				.of(config.getOriginalsAsStringWithDefaults().get(key).split(delimiter))
				.stream()
				.map(value -> createNewMapFromSplit(config, key, value))
				.map(ParticleConnectorConfig::new)
				.collect(Collectors.toList());
		return listOfConfigs;
	}

	public static List<ParticleConnectorConfig> distributePartitionsAsConfig(ParticleConnectorConfig config, String key, String delimiter, int maxTask) {
		return distributePartitionsAsConfig(config, key, delimiter, maxTask, true);
	}

	public static List<ParticleConnectorConfig> distributePartitionsAsConfig(ParticleConnectorConfig config, String key, String delimiter, int maxTask, boolean skipEmpty) {
		return distributePartitionsAsMaps(config, key, delimiter, maxTask, skipEmpty).stream().map(ParticleConnectorConfig::new).collect(Collectors.toList());
	}
	
	public static List<Map<String, String>> distributePartitionsAsMaps(ParticleConnectorConfig config, String key, String delimiter, int maxTask) {
		return distributePartitionsAsMaps(config, key, delimiter, maxTask, true);
	}
	
	public static List<Map<String, String>> distributePartitionsAsMaps(ParticleConnectorConfig config, String key, String delimiter, int maxTask, boolean skipEmpty) {
		List<String> entries = List.of(config.getOriginalsAsStringWithDefaults().get(key).split(delimiter));
		List<List<String>> partitionedEntries = ConnectorUtils.groupPartitions(entries, maxTask);
		return partitionedEntries.stream()
				.filter(p -> !(skipEmpty && p.isEmpty()))
				.map(p->{
					Map<String, String> map = config.getOriginalsAsStringWithDefaults();
					map.put(key, String.join(delimiter, p));
					return map;
				}).collect(Collectors.toList());
	
	}

	private static Map<String, String> createNewMapFromSplit(ParticleConnectorConfig config, String key, String value) {
		Map<String, String> originals = config.getOriginalsAsStringWithDefaults();
		originals.put(key, value);
		return originals;
	}
}
