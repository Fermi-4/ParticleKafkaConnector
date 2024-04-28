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
	public static final String PARTICLE_CONNECTOR_MODE_CONFIG = "particle.connector.source";
	private static final String PARTICLE_CONNECTOR_MODE_CONFIG_DOC = "Connector will capture events from Particle SSE stream.";
	public static final String PARTICLE_CONNECTOR_MODE_SSE = "event";
	public static final String PARTICLE_CONNECTOR_MODE_DEFAULT = PARTICLE_CONNECTOR_MODE_SSE;

	/* api version */
	public static final String API_VERSION_CONFIG = "particle.connector.api_version";
	private static final String PARTICLE_CONNECTOR_API_VERSION_CONFIG_DOC = "Version of the Particle cloud API";
	public static final String PARTICLE_CONNECTOR_API_VERSION_1 = "1.0";
	public static final String PARTICLE_CONNECTOR_API_VERSION_DEFAULT = PARTICLE_CONNECTOR_API_VERSION_1;
	
	/* select how to key events received */
	public static final String PARTICLE_CONNECTOR_KEY_CONFIG = "particle.connector.key_mode"; 
	public static final String PARTICLE_CONNECTOR_KEY_NONE = "none";
	public static final String PARTICLE_CONNECTOR_KEY_PAYLOAD_COREID = "coreid";
	public static final String PARTICLE_CONNECTOR_KEY_EVENT_TYPE = "event_type";
	public static final String PARTICLE_CONNECTOR_KEY_DEVICE_ID_CONFIG = "config_device_id";
	public static final String PARTICLE_CONNECTOR_KEY_PRODUCT_ID_CONFIG = "config_product_id";
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
	public static final String EVENT_MODE_CONFIG = "particle.connector.target"; // TODO: rename this... or refactor somehow
	private static final String EVENT_MODE_CONFIG_DOC = "Setup for device event stream or product event stream or all (none, device, product).";

	/* event mode options */
	public static final String EVENT_MODE_ALL = "all";
	public static final String EVENT_MODE_DEVICE = "device";
	public static final String EVENT_MODE_PRODUCT = "product";

	/* set default access mode */
	private static final String EVENT_MODE_DEFAULT = EVENT_MODE_ALL;
	
	/*  */
	public static final String DISCOVER_MODE_CONFIG = "particle.connector.discover";
	private static final String DISCOVER_MODE_CONFIG_DOC = "Have connector use Particle.io Cloud API to discover products/devices.";
	public static final String DISCOVER_MODE_DISCOVER_ALL_PRODUCTS = "discover_products";
	public static final String DISCOVER_MODE_DISCOVER_ALL_DEVICES = "discover_devices";
	public static final String DISCOVER_MODE_DISCOVER_ALL_DEVICES_IN_PRODUCT = "discover_devices_in_product";
	public static final String DISCOVER_MODE_DISCOVER_ALL_DEVICES_IN_PRODUCT_AND_GROUPS = "discover_devices_in_product_and_groups";

	
	/* device id option if access mode == device */
	public static final String DEVICE_ID_CONFIG = "particle.connector.device_id";
	private static final String DEVICE_ID_CONFIG_DOC = "Device ID to filter stream of events on.";

	/* product id option if access mode == product */
	public static final String PRODUCT_ID_CONFIG = "particle.connector.product_id";
	private static final String PRODUCT_ID_CONFIG_DOC = "Product ID to filter stream of events on.";

	public static final String PRODUCT_GROUPS_CONFIG = "particle.connector.product_groups";
	private static final String PRODUCT_GROUPS_CONFIG_DOC = "Comma separated list of product groups to filter devices on when using device discovery.";

	/* idle time in between retry connection attempts */
	public static final String RETRY_DELAY_CONFIG = "particle.connector.retry_delay_ms";
	private static final String RETRY_DELAY_CONFIG_DOC = "Delay (in milliseconds) to wait between connection attempts.";
	private static final int RETRY_DELAY_CONFIG_DEFAULT = 5000;

	public static final String MAX_RETRY_CONNECT_ATTEMPTS_CONFIG = "particle.connector.retry_attempts";
	private static final int MAX_RETRY_CONNECT_ATTEMPTS_DEFAULT = 10;
	private static final String MAX_RETRY_CONNECT_ATTEMPTS_DOC = "Maximum number of retry attempts for connecting to a resource.";

	/* http client config options */
	public static final String HTTP_PROXY_HOST_CONFIG = "particle.http.proxy.host";
	public static final String HTTP_PROXY_HOST_CONFIG_DOC = "Configure the http client to use a proxy host (or no proxy with configuration 'no_proxy')";
	public static final String HTTP_PROXY_PORT_CONFIG = "particle.http.proxy.port";
	public static final String HTTP_PROXY_PORT_CONFIG_DOC = "Configure the port for the http client proxy";
	
	
	public static final String HTTP_CONNECT_TIMEOUT_CONFIG = "particle.http.connect.timeout"; /* timeout on connect*/
	public static final int HTTP_CONNECT_TIMEOUT_CONFIG_DEFAULT = 30000;
	
	public static final String HTTP_READ_TIMEOUT_CONFIG = "particle.http.read.timeout"; /* timeout on data rx */
	public static final String HTTP_READ_TIMEOUT_CONFIG_DOC = "Timeout (in milliseconds) for a read to take place. Connection will be considered stale and connector will attempt to reestablish connection.";
	public static final int HTTP_READ_TIMEOUT_CONFIG_DEFAULT = 30000;
	
	
	public static final String HTTP_PROXY_HOST_CONFIG_NO_PROXY = "no_proxy";

	public static final String DELIMITER_DEFAULT = ",";
	public static final String DELIMITER_CONFIG = "particle.connector.delimiter";
	public static final String DELIMITER_CONFIG_DOC = "Delimiter to use when processing configuration fields for parallelization (default is " + DELIMITER_DEFAULT + "). Example targeting " + DEVICE_ID_CONFIG + ": device1,device2,device3 -- would split into 3 independent configurations if max task equals 3";

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
				.define(DELIMITER_CONFIG, Type.STRING, DELIMITER_DEFAULT, Importance.LOW, DELIMITER_CONFIG_DOC)
				.define(TOPIC_CONFIG, Type.STRING, ConfigDef.NO_DEFAULT_VALUE, Importance.HIGH, TOPIC_CONFIG_DOC)
				.define(DISCOVER_MODE_CONFIG, Type.STRING, null, Importance.HIGH, DISCOVER_MODE_CONFIG_DOC)
				.define(API_VERSION_CONFIG, Type.STRING, PARTICLE_CONNECTOR_API_VERSION_DEFAULT, Importance.HIGH, PARTICLE_CONNECTOR_API_VERSION_CONFIG_DOC)
				.define(PARTICLE_CONNECTOR_KEY_CONFIG, Type.STRING, PARTICLE_CONNECTOR_KEY_DEFAULT, OneOfValidator.isOneOfAndNotNull(String.class, PARTICLE_CONNECTOR_KEY_NONE, PARTICLE_CONNECTOR_KEY_EVENT_TYPE, PARTICLE_CONNECTOR_KEY_DEVICE_ID_CONFIG, PARTICLE_CONNECTOR_KEY_PAYLOAD_COREID, PARTICLE_CONNECTOR_KEY_PRODUCT_ID_CONFIG), Importance.HIGH, PARTICLE_CONNECTOR_KEY_CONFIG_DOC)
				.define(RETRY_DELAY_CONFIG, Type.INT, RETRY_DELAY_CONFIG_DEFAULT, Importance.LOW, RETRY_DELAY_CONFIG_DOC)
				.define(EVENT_PREFIX_CONFIG, Type.STRING, "", Importance.HIGH, EVENT_PREFIX_CONFIG_DOC)
				.define(EVENT_MODE_CONFIG, Type.STRING, EVENT_MODE_DEFAULT, OneOfValidator.isOneOfAndNotNull(String.class, EVENT_MODE_ALL, EVENT_MODE_PRODUCT, EVENT_MODE_DEVICE), Importance.HIGH, EVENT_MODE_CONFIG_DOC)
				.define(DEVICE_ID_CONFIG, Type.STRING, null, Importance.HIGH, DEVICE_ID_CONFIG_DOC)
				.define(PARTICLE_CONNECTOR_MODE_CONFIG, Type.STRING, PARTICLE_CONNECTOR_MODE_DEFAULT, OneOfValidator.isOneOfAndNotNull(String.class, PARTICLE_CONNECTOR_MODE_SSE), Importance.HIGH, PARTICLE_CONNECTOR_MODE_CONFIG_DOC)
				.define(PRODUCT_ID_CONFIG, Type.STRING, null, Importance.HIGH, PRODUCT_ID_CONFIG_DOC)
				.define(PRODUCT_GROUPS_CONFIG, Type.STRING, null, Importance.HIGH, PRODUCT_GROUPS_CONFIG_DOC)
				.define(ACCESS_TOKEN_CONFIG, Type.STRING, null, new ConfigDef.NonEmptyString(), Importance.HIGH, ACCESS_TOKEN_CONFIG_DOC)
				.define(MAX_RETRY_CONNECT_ATTEMPTS_CONFIG, Type.INT, MAX_RETRY_CONNECT_ATTEMPTS_DEFAULT, ConfigDef.Range.atLeast(0), Importance.LOW, MAX_RETRY_CONNECT_ATTEMPTS_DOC)
				.define(HTTP_PROXY_HOST_CONFIG, Type.STRING, null, Importance.HIGH, HTTP_PROXY_HOST_CONFIG_DOC)
				.define(HTTP_PROXY_PORT_CONFIG, Type.INT, -1, Importance.HIGH, HTTP_PROXY_PORT_CONFIG_DOC)
				.define(HTTP_CONNECT_TIMEOUT_CONFIG, Type.LONG, HTTP_CONNECT_TIMEOUT_CONFIG_DEFAULT, Importance.HIGH, "Connect timeout in ms")
				.define(HTTP_READ_TIMEOUT_CONFIG, Type.LONG, HTTP_READ_TIMEOUT_CONFIG_DEFAULT, Importance.HIGH, HTTP_READ_TIMEOUT_CONFIG_DOC);
	}
	
	public String getDiscover() {
		return this.getString(DISCOVER_MODE_CONFIG);
	}
	
	public String getApiVersion() {
		return this.getString(API_VERSION_CONFIG);
	}
	
	public String getConnectorKeyMode() {
		return this.getString(PARTICLE_CONNECTOR_KEY_CONFIG);
	}

	public int getMaxReconnectAttempts() {
		return this.getInt(MAX_RETRY_CONNECT_ATTEMPTS_CONFIG);
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

	public String getProductGroups() {
		return this.getString(PRODUCT_GROUPS_CONFIG);
	}

	public String getDelimiter() {
		return this.getString(DELIMITER_CONFIG);
	}

	public HttpClientConfig getHttpClientConfig() {
		HttpClientConfig httpClientConfig = new HttpClientConfig();
		if(get(HTTP_PROXY_HOST_CONFIG) != null) {
			httpClientConfig.setProxyHost(getString(HTTP_PROXY_HOST_CONFIG));
		}
		if(get(HTTP_PROXY_PORT_CONFIG) != null) {
			httpClientConfig.setProxyPort(getInt(HTTP_PROXY_PORT_CONFIG));			
		}
		if(get(HTTP_CONNECT_TIMEOUT_CONFIG) != null) {
			httpClientConfig.setConnectTimeout(getLong(HTTP_CONNECT_TIMEOUT_CONFIG));
		}
		if(get(HTTP_READ_TIMEOUT_CONFIG) != null) {
			httpClientConfig.setReadTimeout(getLong(HTTP_READ_TIMEOUT_CONFIG));
		}
		return httpClientConfig;
	}

	public Map<String, String> getOriginalsAsStringWithDefaults() {

		// Create a stream of entries from the originalsStrings map
		Stream<Entry<String, String>> userInputOriginalsStream = this.originalsStrings().entrySet().stream();

		// Create a stream of entries from the defaultValues map, converting values to
		// type String
		Stream<Entry<String, String>> defaultValuesStream = conf().defaultValues().entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> String.valueOf(e.getValue()))).entrySet().stream();

		// Concatenate the two streams of entries
		// The collect operation creates a new map using the entries, with keys from
		// userInputOriginals overriding defaultValues
		Map<String, String> combinedConfigs = Stream.concat(defaultValuesStream, userInputOriginalsStream)
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (a, b) -> b));

		return combinedConfigs;
	}
}
