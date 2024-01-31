package com.fermi4.particle;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParticleEventSourceConnector extends SourceConnector {

	private static Logger log = LoggerFactory.getLogger(ParticleEventSourceConnector.class);
	private Map<String, String> props;
	private ParticleConnectorConfig config;
	private String mode;

	@Override
	public String version() {
		return VersionUtil.getVersion();
	}

	@Override
	public void start(Map<String, String> map) {
		this.props = map;
		this.config = new ParticleConnectorConfig(map);
		// TODO: Add things you need to do to setup your connector.
	}

	@Override
	public Class<? extends Task> taskClass() {
		return ParticleEventSourceTask.class;
	}

	@Override
	public List<Map<String, String>> taskConfigs(int i) {
		if (i > 1) {
			log.warn("There is only one task which will be created [requested: %s]", i);
		}
		return Collections.singletonList(this.config.originalsStrings());
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
