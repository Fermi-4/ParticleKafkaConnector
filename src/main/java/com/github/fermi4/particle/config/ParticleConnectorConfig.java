package com.github.fermi4.particle.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Type;

import com.github.fermi4.particle.config.validators.OneOfValidator;

import org.apache.kafka.common.config.ConfigDef.Importance;

import java.util.Map;
import java.util.stream.Collectors;

public class ParticleConnectorConfig extends AbstractConfig {
	/**
	 * TODO: have the option of passing in a list of target device id/product id
	 * 
	 * i.e.
	 * 
	 * particle.event.device.id = 123,123,123,123 particle.event.product.id =
	 * 123,123,123,123
	 * 
	 * Have each task be responsible for handling one of each - this way ordering
	 * will be preserved
	 * 
	 * 
	 *  particle.connector.mode
		particle.connector.topic
		particle.connector.token
		
		particle.event.prefix
		particle.event.mode
		particle.event.device.id
		particle.event.product.id
		particle.event.connection.timeoutms
		particle.event.connection.retry.delayms
	 * 
	 */
	
	/* top level mode */
	public static final String PARTICLE_CONNECTOR_MODE_CONFIG = "particle.connector.mode";
	private static final String PARTICLE_CONNECTOR_MODE_CONFIG_DOC = "Determines which high level task to run for this connector (sse, healthcheck, ledger, etc)";
	
	public static final String PARTICLE_CONNECTOR_MODE_SSE = "sse";
	public static final String PARTICLE_CONNECTOR_MODE_HEALTH = "health";
	
	public static final String PARTICLE_CONNECTOR_MODE_DEFAULT = PARTICLE_CONNECTOR_MODE_SSE;
	
	/* filter events matching this prefix */
	public static final String EVENT_PREFIX_CONFIG = "particle.event.prefix";
	private static final String EVENT_PREFIX_CONFIG_DOC = "Filters the stream to only events starting with the specified prefix.";

	/* topic to publish events to */
	public static final String TOPIC_CONFIG = "particle.connector.topic";
	public static final String TOPIC_CONFIG_DOC = "Output topic for received events.";
	
	/* particle cloud api access token */
	public static final String ACCESS_TOKEN_CONFIG = "particle.connector.token";
	private static final String ACCESS_TOKEN_CONFIG_DOC = "Access token for interacting with Particle API.";
	
	/* collect events from all/device/product */
	public static final String EVENT_MODE_CONFIG = "particle.event.mode";
	private static final String EVENT_MODE_CONFIG_DOC = "Setup for device event stream or product event stream.";
	
	/* access mode options */
	public static final String EVENT_MODE_ALL = "all";
	public static final String EVENT_MODE_DEVICE = "device";
	public static final String EVENT_MODE_PRODUCT = "product";
	
	/* set default access mode */
	private static final String EVENT_MODE_DEFAULT = EVENT_MODE_ALL;

	/* timeout on data rx */
	public static final String READ_TIMEOUT_CONFIG = "particle.event.connection.timeoutms";
	private static final String READ_TIMEOUT_CONFIG_DOC = "Timeout (in milliseconds) for a read to take place. Connection will be considered stale and connector will attempt to reestablish connection.";
	private static final int READ_TIMEOUT_DEFAULT = 30000;
	
	/* device id option if access mode == device */
	public static final String DEVICE_ID_CONFIG = "particle.event.device.id";
	private static final String DEVICE_ID_CONFIG_DOC = "Device ID to filter stream of events on.";
	
	/* product id option if access mode == product */
	public static final String PRODUCT_ID_CONFIG = "particle.event.product.id";
	private static final String PRODUCT_ID_CONFIG_DOC = "Product ID to filter stream of events on.";
	/* idle time in between retry connection attempts */
	public static final String RETRY_DELAY_CONFIG = "particle.event.connection.retry.delayms";
	private static final String RETRY_DELAY_CONFIG_DOC = "Delay (in milliseconds) to wait between connection attempts.";
	private static final int RETRY_DELAY_CONFIG_DEFAULT = 100;
	
	public static final String DELIMITER = ","; 

	public ParticleConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
		super(config, parsedConfig);
	}

	public ParticleConnectorConfig(Map<String, String> parsedConfig) {
		this(conf(), parsedConfig);
	}

	public static ConfigDef conf() {
		return new ConfigDef()
				.define(TOPIC_CONFIG, Type.STRING, ConfigDef.NO_DEFAULT_VALUE, Importance.HIGH, TOPIC_CONFIG_DOC)
				.define(READ_TIMEOUT_CONFIG, Type.INT, READ_TIMEOUT_DEFAULT, Importance.HIGH, READ_TIMEOUT_CONFIG_DOC)
				.define(RETRY_DELAY_CONFIG, Type.INT, RETRY_DELAY_CONFIG_DEFAULT, Importance.LOW, RETRY_DELAY_CONFIG_DOC)
				.define(EVENT_PREFIX_CONFIG, Type.STRING, null, Importance.HIGH, EVENT_PREFIX_CONFIG_DOC)
				.define(EVENT_MODE_CONFIG, Type.STRING, EVENT_MODE_DEFAULT, OneOfValidator.isOneOfAndNotNull(String.class, EVENT_MODE_ALL, EVENT_MODE_PRODUCT, EVENT_MODE_DEVICE), Importance.HIGH, EVENT_MODE_CONFIG_DOC)
				.define(DEVICE_ID_CONFIG, Type.STRING, null, Importance.HIGH, DEVICE_ID_CONFIG_DOC)
				.define(PARTICLE_CONNECTOR_MODE_CONFIG, Type.STRING, PARTICLE_CONNECTOR_MODE_DEFAULT, OneOfValidator.isOneOfAndNotNull(String.class, PARTICLE_CONNECTOR_MODE_SSE, PARTICLE_CONNECTOR_MODE_HEALTH), Importance.HIGH, PARTICLE_CONNECTOR_MODE_CONFIG_DOC)
				.define(PRODUCT_ID_CONFIG, Type.STRING, null, Importance.HIGH, PRODUCT_ID_CONFIG_DOC)
				.define(ACCESS_TOKEN_CONFIG, Type.STRING, Importance.HIGH, ACCESS_TOKEN_CONFIG_DOC);
	}
	
	public int getReadTimeout() {
		return this.getInt(READ_TIMEOUT_CONFIG);
	}
	
	public int getRetryDelay() {
		return this.getInt(RETRY_DELAY_CONFIG);
	}
	
	public String getConnectorMode() {
		return this.getString(PARTICLE_CONNECTOR_MODE_CONFIG);
	}
	
	public String getTopic() {
		return this.getString(TOPIC_CONFIG);
	}

	public String getEventPrefix() {
		return this.getString(EVENT_PREFIX_CONFIG);
	}

	public String getAccessToken() {
		return this.getString(ACCESS_TOKEN_CONFIG);
	}

	public String getAccessMode() {
		return this.getString(EVENT_MODE_CONFIG);
	}

	public String getDeviceId() {
		return this.getString(DEVICE_ID_CONFIG);
	}

	public String getProductId() {
		return this.getString(PRODUCT_ID_CONFIG);
	}
	
	public Map<String, String> getOriginalsAsStringWithDefaults() {
		return conf()
				.defaultValues()
				.entrySet()
				.stream()
				.collect(Collectors.toMap(e->e.getKey(), e->String.valueOf(e)));
	}

}
