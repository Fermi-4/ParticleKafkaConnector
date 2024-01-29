package com.fermi4.particle;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Type;

import com.fermi4.particle.validate.OneOfValidator;

import org.apache.kafka.common.config.ConfigDef.Importance;

import java.util.Map;

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
	 */

	public static final String EVENT_PREFIX_CONFIG = "particle.event.prefix";
	private static final String EVENT_PREFIX_CONFIG_DOC = "Filters the stream to only events starting with the specified prefix.";

	public static final String TOPIC_CONFIG = "particle.event.topic";
	public static final String TOPIC_CONFIG_DOC = "Output topic for received events.";

	public static final String ACCESS_TOKEN_CONFIG = "particle.event.access.token";
	private static final String ACCESS_TOKEN_CONFIG_DOC = "Access token for interacting with Particle API.";

	public static final String ACCESS_MODE_CONFIG = "particle.event.mode";
	private static final String ACCESS_MODE_CONFIG_DOC = "Setup for device event stream or product event stream.";

	/* access mode options */
	public static final String ACCESS_MODE_ALL = "all";
	public static final String ACCESS_MODE_DEVICE = "device";
	public static final String ACCESS_MODE_PRODUCT = "product";

	/* set default access mode */
	private static final String ACCESS_MODE_DEFAULT = ACCESS_MODE_ALL;

	public static final String DEVICE_ID_CONFIG = "particle.event.device.id";
	private static final String DEVICE_ID_CONFIG_DOC = "Device ID to filter stream of events on.";

	public static final String PRODUCT_ID_CONFIG = "particle.event.product.id";
	private static final String PRODUCT_ID_CONFIG_DOC = "Product ID to filter stream of events on.";

	public ParticleConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
		super(config, parsedConfig);
	}

	public ParticleConnectorConfig(Map<String, String> parsedConfig) {
		this(conf(), parsedConfig);
	}

	public static ConfigDef conf() {
		return new ConfigDef()
				.define(TOPIC_CONFIG, Type.STRING, ConfigDef.NO_DEFAULT_VALUE, Importance.HIGH, TOPIC_CONFIG_DOC)
				.define(EVENT_PREFIX_CONFIG, Type.STRING, null, Importance.HIGH, EVENT_PREFIX_CONFIG_DOC)
				.define(ACCESS_MODE_CONFIG, Type.STRING, ACCESS_MODE_DEFAULT,
						OneOfValidator.isOneOfAndNotNull(String.class, ACCESS_MODE_ALL, ACCESS_MODE_PRODUCT,
								ACCESS_MODE_DEVICE),
						Importance.HIGH, ACCESS_MODE_CONFIG_DOC)
				.define(DEVICE_ID_CONFIG, Type.STRING, null, Importance.HIGH, DEVICE_ID_CONFIG_DOC)
				.define(PRODUCT_ID_CONFIG, Type.STRING, null, Importance.HIGH, PRODUCT_ID_CONFIG_DOC)
				.define(ACCESS_TOKEN_CONFIG, Type.STRING, Importance.HIGH, ACCESS_TOKEN_CONFIG_DOC);
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
		return this.getString(ACCESS_MODE_CONFIG);
	}

	public String getDeviceId() {
		return this.getString(DEVICE_ID_CONFIG);
	}

	public String getProductId() {
		return this.getString(PRODUCT_ID_CONFIG);
	}

}
