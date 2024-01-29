package com.github.fermi4.particle;

import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.config.partition.ParticleTaskConfigPartitioner;
import com.github.fermi4.particle.config.partition.PartitionerFactory;
import com.github.fermi4.particle.task.ParticleServerSentEventSourceTask;

public class ParticleSourceConnector extends SourceConnector {

	private static Logger log = LoggerFactory.getLogger(ParticleSourceConnector.class);
	private ParticleConnectorConfig config;

	@Override
	public String version() {
		return VersionUtil.getVersion();
	}

	@Override
	public void start(Map<String, String> map) {
		this.config = new ParticleConnectorConfig(map);
	}

	@Override
	public Class<? extends Task> taskClass() {
		return ParticleServerSentEventSourceTask.class;
	}

	@Override
	public List<Map<String, String>> taskConfigs(int i) {
		System.out.println("Max task is " + i);
		List<Map<String, String>> configs = getPartitioner(config).partition(config, i);
		System.out.println("Creating " + configs.size() + " task configs");
		for (int j = 0; j < configs.size(); j++) {
		    Map<String, String> currentConfig = configs.get(j);
		    System.out.println("Created task config " + (j + 1) + ": " + currentConfig);
		}

		configs.stream().forEach(m -> {
			System.out.println("=========================================");
			m.entrySet().stream().forEach(e -> {
				System.out.println("KEY: " + e.getKey() + " " + e.getValue());
			});
		});

		return configs;
	}

	@Override
	public void stop() {
	}

	@Override
	public ConfigDef config() {
		return ParticleConnectorConfig.conf();
	}

	public ParticleTaskConfigPartitioner getPartitioner(ParticleConnectorConfig config) {
		return PartitionerFactory.get(config);
	}
}
