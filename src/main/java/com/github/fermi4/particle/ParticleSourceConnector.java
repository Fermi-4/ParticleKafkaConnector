package com.github.fermi4.particle;

import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
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
		return PartitionerFactory.get(config).partition(config, i);
	}

	@Override
	public void stop() {
		// TODO: Do things that are necessary to stop your connector.
	}

	@Override
	public ConfigDef config() {
		return ParticleConnectorConfig.conf();
	}
}
