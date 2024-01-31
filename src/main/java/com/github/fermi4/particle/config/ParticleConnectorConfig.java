package com.github.fermi4.particle.config;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import com.github.fermi4.particle.config.validators.OneOfValidator;

public class ParticleConnectorConfig extends AbstractConfig {

	/* top level mode */
	public static final String PARTICLE_CONNECTOR_MODE_CONFIG = "particle.connector";
	private static final String PARTICLE_CONNECTOR_MODE_CONFIG_DOC = "Connector will capture events from Particle SSE stream.";
	public static final String PARTICLE_CONNECTOR_MODE_SSE = "event";
	public static final String PARTICLE_CONNECTOR_MODE_DEFAULT = PARTICLE_CONNECTOR_MODE_SSE;

	/* select how to key events received */
	public static final String PARTICLE_CONNECTOR_KEY_CONFIG = "particle.connector.key_mode";
	public static final String PARTICLE_CONNECTOR_KEY_NONE = "none";
	public static final String PARTICLE_CONNECTOR_KEY_COREID = "coreid";
	public static final String PARTICLE_CONNECTOR_KEY_EVENT = "event";
	public static final String PARTICLE_CONNECTOR_KEY_DEVICE = "device";
	public static final String PARTICLE_CONNECTOR_KEY_PRODUCT = "product";
	public static final String PARTICLE_CONNECTOR_KEY_DEFAULT = PARTICLE_CONNECTOR_KEY_NONE;
	private static final String PARTICLE_CONNECTOR_KEY_CONFIG_DOC = "Specify what the connector should key source record data by (none, event - by event type, device - by device id, product - by product id).";	
	
	/* filter events matching this prefix */
	public static final String EVENT_PREFIX_CONFIG = "particle.connector.event_prefix";
	private static final String EVENT_PREFIX_CONFIG_DOC = "Filters the stream to only events starting with the specified prefix.";

	/* topic to publish events to */
	public static final String TOPIC_CONFIG = "particle.connector.topic";
	public static final String TOPIC_CONFIG_DOC = "Output topic for received events.";

	/* particle cloud api access token */
	public static final String ACCESS_TOKEN_CONFIG = "particle.connector.token";
	private static final String ACCESS_TOKEN_CONFIG_DOC = "Access token for interacting with Particle API.";

	/* collect events from all/device/product */
	public static final String EVENT_MODE_CONFIG = "particle.connector.target";
	private static final String EVENT_MODE_CONFIG_DOC = "Setup for device event stream or product event stream or all (none, device, product).";

	/* access mode options */
	public static final String EVENT_MODE_ALL = "all";
	public static final String EVENT_MODE_DEVICE = "device";
	public static final String EVENT_MODE_PRODUCT = "product";

	/* set default access mode */
	private static final String EVENT_MODE_DEFAULT = EVENT_MODE_ALL;

	/* timeout on data rx */
	public static final String READ_TIMEOUT_CONFIG = "particle.connector.read_timeout_ms";
	private static final String READ_TIMEOUT_CONFIG_DOC = "Timeout (in milliseconds) for a read to take place. Connection will be considered stale and connector will attempt to reestablish connection.";
	private static final int READ_TIMEOUT_DEFAULT = 30000;

	/* device id option if access mode == device */
	public static final String DEVICE_ID_CONFIG = "particle.connector.device_id";
	private static final String DEVICE_ID_CONFIG_DOC = "Device ID to filter stream of events on.";

	/* product id option if access mode == product */
	public static final String PRODUCT_ID_CONFIG = "particle.connector.product_id";
	private static final String PRODUCT_ID_CONFIG_DOC = "Product ID to filter stream of events on.";
	
	/* idle time in between retry connection attempts */
	public static final String RETRY_DELAY_CONFIG = "particle.connector.retry_delay_ms";
	private static final String RETRY_DELAY_CONFIG_DOC = "Delay (in milliseconds) to wait between connection attempts.";
	private static final int RETRY_DELAY_CONFIG_DEFAULT = 5000;
	
	public static final String MAX_RETRY_CONNECT_ATTEMPTS_CONFIG = "particle.connector.retry_attempts";
    private static final int MAX_RETRY_CONNECT_ATTEMPTS_DEFAULT = 10;
    private static final String MAX_RETRY_CONNECT_ATTEMPTS_DOC = "Maximum number of retry attempts for connecting to a resource.";

	
	public static final String DELIMITER = ",";

	public ParticleConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
		super(config, parsedConfig);
	}

	public ParticleConnectorConfig(Map<String, String> parsedConfig) {
		this(conf(), parsedConfig);
	}

	public static ParticleConnectorConfig fromMapWithDefaults(Map<String, String> map) {
		Objects.requireNonNull(map);
		return new ParticleConnectorConfig(new ParticleConnectorConfig(map).getOriginalsAsStringWithDefaults());
	}
	
	public static ConfigDef conf() {
		return new ConfigDef()
				.define(
						TOPIC_CONFIG, 
						Type.STRING, 
						ConfigDef.NO_DEFAULT_VALUE, 
						Importance.HIGH, 
						TOPIC_CONFIG_DOC
						)
				.define(
						PARTICLE_CONNECTOR_KEY_CONFIG, 
						Type.STRING, 
						PARTICLE_CONNECTOR_KEY_DEFAULT,
						OneOfValidator.isOneOfAndNotNull(
								String.class,
								PARTICLE_CONNECTOR_KEY_NONE,
								PARTICLE_CONNECTOR_KEY_DEVICE, 
								PARTICLE_CONNECTOR_KEY_PRODUCT,
								PARTICLE_CONNECTOR_KEY_EVENT),
						Importance.HIGH,
						PARTICLE_CONNECTOR_KEY_CONFIG_DOC
						)
				.define(
						READ_TIMEOUT_CONFIG, 
						Type.INT, 
						READ_TIMEOUT_DEFAULT, 
						Importance.HIGH, 
						READ_TIMEOUT_CONFIG_DOC
						)
				.define(
						RETRY_DELAY_CONFIG, 
						Type.INT, 
						RETRY_DELAY_CONFIG_DEFAULT, 
						Importance.LOW,
						RETRY_DELAY_CONFIG_DOC
						)
				.define(
						EVENT_PREFIX_CONFIG, 
						Type.STRING, 
						null, 
						Importance.HIGH, 
						EVENT_PREFIX_CONFIG_DOC
						)
				.define(
						EVENT_MODE_CONFIG, 
						Type.STRING, 
						EVENT_MODE_DEFAULT,
						OneOfValidator.isOneOfAndNotNull(
								String.class, 
								EVENT_MODE_ALL, 
								EVENT_MODE_PRODUCT,
								EVENT_MODE_DEVICE
								),
						Importance.HIGH, 
						EVENT_MODE_CONFIG_DOC
						)
				.define(
						DEVICE_ID_CONFIG, 
						Type.STRING, 
						null, 
						Importance.HIGH, 
						DEVICE_ID_CONFIG_DOC
						)
				.define(
						PARTICLE_CONNECTOR_MODE_CONFIG, 
						Type.STRING, 
						PARTICLE_CONNECTOR_MODE_DEFAULT,
						OneOfValidator.isOneOfAndNotNull(
								String.class, 
								PARTICLE_CONNECTOR_MODE_SSE
								),
						Importance.HIGH, 
						PARTICLE_CONNECTOR_MODE_CONFIG_DOC
						)
				.define(
						PRODUCT_ID_CONFIG, 
						Type.STRING, 
						null, 
						Importance.HIGH, 
						PRODUCT_ID_CONFIG_DOC
						)
				.define(
						ACCESS_TOKEN_CONFIG, 
						Type.STRING,
						null,
						new ConfigDef.NonEmptyString(),
						Importance.HIGH, 
						ACCESS_TOKEN_CONFIG_DOC
						)
				.define(
		                MAX_RETRY_CONNECT_ATTEMPTS_CONFIG,
		                Type.INT,
		                MAX_RETRY_CONNECT_ATTEMPTS_DEFAULT,
		                ConfigDef.Range.atLeast(0),
		                Importance.LOW,
		                MAX_RETRY_CONNECT_ATTEMPTS_DOC
		            );
	}

	public String getConnectorKeyMode() {
		return this.getString(PARTICLE_CONNECTOR_KEY_CONFIG);
	}
	
	public int getMaxReconnectAttempts() {
		return this.getInt(MAX_RETRY_CONNECT_ATTEMPTS_CONFIG);
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

	public String getEventMode() {
		return this.getString(EVENT_MODE_CONFIG);
	}

	public String getDeviceId() {
		return this.getString(DEVICE_ID_CONFIG);
	}

	public String getProductId() {
		return this.getString(PRODUCT_ID_CONFIG);
	}

	public Map<String, String> getOriginalsAsStringWithDefaults() {
		
        // Create a stream of entries from the originalsStrings map
		Stream<Entry<String, String>> userInputOriginalsStream = this.originalsStrings().entrySet().stream();
		
        // Create a stream of entries from the defaultValues map, converting values to type String
		Stream<Entry<String, String>> defaultValuesStream = conf().defaultValues().entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> String.valueOf(e.getValue()))).entrySet().stream();
		
		// Concatenate the two streams of entries
		// The collect operation creates a new map using the entries, with keys from userInputOriginals overriding defaultValues
		Map<String, String> combinedConfigs = Stream.concat(defaultValuesStream, userInputOriginalsStream)
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (a, b) -> b));
		
		return combinedConfigs;
	}

}
