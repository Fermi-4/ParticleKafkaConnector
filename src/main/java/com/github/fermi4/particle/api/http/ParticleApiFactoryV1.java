package com.github.fermi4.particle.api.http;

import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
/**
 * 
 * This provides utility methods for returning the appropriate 
 * {@link HttpUrl} targeting Particle Cloud API resource 
 * 
 * 
 * @author Fermi-4
 *	
 * TODO: versioning of the API
 */
public class ParticleApiFactoryV1 implements ParticleApiFactory {
	
	protected final String PARTICLE_HOST_URL = "api.particle.io";
	protected final String PARTICLE_SCHEME = "https";
	protected final String PARTICLE_ACCESS_TOKEN_KEY = "access_token";
	
	private static final String PARTICLE_API_VERSION = "v1";	

	
	public HttpUrl listDevicesInProductGroups(String accessToken, String productIdOrSlug, List<String> groups, Map<String, String> queryParameters) {
		/* create groups query parameter from list */
		String groupsString = String.join(",", groups);
		queryParameters.put("groups", groupsString);
		return this.listDevicesInProduct(accessToken, productIdOrSlug, queryParameters);
	}

	/**
	 * 
	 * List devices the currently authenticated user has access to. By default, devices will be sorted by last_handshake_at in descending order.
	 * 
	 * @param accessToken - Particle Cloud API access token
	 * @param deviceId - (optional) Filter results to devices with this ID (partial matching)
	 * @param groups - (optional) Comma separated list of full group names to filter results to devices belonging to these groups only
	 * @param deviceName - (optional) Filter results to devices with this name (partial matching)
	 * @param serialNumber - (optional) Filter results to devices with this serial number (partial matching)
	 * @param sortAttr - (optional) The attribute by which to sort results. Options for sorting are deviceId, firmwareVersion, or lastConnection. By default, if no sortAttr parameter is set, devices will be sorted by last connection, in descending order
	 * @param sortDir - (optional) The direction of sorting. Pass asc for ascending sorting or desc for descending sorting
	 * @param quarantined - (optional) Include / exclude quarantined devices
	 * @param perPage - (optional) Records per page
	 *
	 * @return {@link HttpUrl}
	 * 
	 * @see <a href=
	 *      "https://docs.particle.io/reference/cloud-apis/api/#list-devices">Particle
	 *      Cloud API Docs</a>
	 */
	public HttpUrl listDevicesInProduct(String accessToken, String productIdOrSlug, Map<String, String> queryParameters) {
		Builder builder = new HttpUrl.Builder();
		
		builder.scheme(PARTICLE_SCHEME)
			.host(PARTICLE_HOST_URL)
			.addPathSegment(PARTICLE_API_VERSION)
			.addPathSegment("products")
			.addPathSegment(productIdOrSlug)
			.addPathSegment("devices")
			.addQueryParameter(PARTICLE_ACCESS_TOKEN_KEY, accessToken);
		
		/* add any additional query parameters to the request */
		queryParameters.entrySet().stream()
			.forEach(e -> builder.addQueryParameter(e.getKey(), e.getValue()));

		return builder.build();
	}
	
	/**
	 * 
	 * List devices the currently authenticated user has access to. By default, devices will be sorted by last_handshake_at in descending order.
	 * 
	 * @param accessToken - Particle Cloud API access token
	 * 
	 * @return {@link HttpUrl}
	 * 
	 * @see <a href=
	 *      "https://docs.particle.io/reference/cloud-apis/api/#list-devices">Particle
	 *      Cloud API Docs</a>
	 */
	public HttpUrl listAllDevices(String accessToken) {
		Builder builder = new HttpUrl.Builder();
		
		builder.scheme(PARTICLE_SCHEME)
			.host(PARTICLE_HOST_URL)
			.addPathSegment(PARTICLE_API_VERSION)
			.addPathSegment("devices")
			.addQueryParameter(PARTICLE_ACCESS_TOKEN_KEY, accessToken);
		
		return builder.build();
	}

	/**
	 * 
	 * Open a stream of Server Sent Events for all public events and private events for your devices matching the filter.
	 * 
	 * @apiNote As of April 2018, the event prefix filter is required. It was optional before.
	 * <br><br>
	 * 
	 * @param accessToken - Particle Cloud API access token
	 * @param eventPrefix - Prefix to filter events on (required)
	 * 
	 * @return {@link HttpUrl}
	 * 
	 * @see <a href=
	 *      "https://docs.particle.io/reference/cloud-apis/api/#get-a-stream-of-events">Particle
	 *      Cloud API Docs</a>
	 */
	public HttpUrl getAllEventsFiltered(String accessToken, String eventPrefix) {
		Builder builder = new HttpUrl.Builder();
		
		builder.scheme(PARTICLE_SCHEME)
			.host(PARTICLE_HOST_URL)
			.addPathSegment(PARTICLE_API_VERSION)
			.addPathSegment("events")
			.addPathSegment(eventPrefix)
			.addQueryParameter(PARTICLE_ACCESS_TOKEN_KEY, accessToken);
		
		return builder.build();
	}

	/**
	 * 
	 * Open a stream of Server Sent Events for all public and private events for
	 * your devices. Filters event stream on events matching the event prefix.
	 * 
	 * @param accessToken - Particle Cloud API access token
	 * @param eventPrefix - Prefix to filter events on
	 * 
	 * @return {@link HttpUrl}
	 * 
	 * @see <a href=
	 *      "https://docs.particle.io/reference/cloud-apis/api/#get-a-stream-of-your-events">Particle
	 *      Cloud API Docs</a>
	 */
	public HttpUrl getAllDeviceEventsFiltered(String accessToken, String eventPrefix) {
		Builder builder = new HttpUrl.Builder();
		
		builder.scheme(PARTICLE_SCHEME)
			.host(PARTICLE_HOST_URL)
			.addPathSegment(PARTICLE_API_VERSION)
			.addPathSegment("devices")
			.addPathSegment("events")
			.addPathSegment(eventPrefix)
			.addQueryParameter(PARTICLE_ACCESS_TOKEN_KEY, accessToken);
		
		return builder.build();
	}
	
	/**
	 * 
	 * Open a stream of Server Sent Events for all public and private events for
	 * your devices.
	 * 
	 * @param accessToken - Particle Cloud API access token
	 * 
	 * @return {@link HttpUrl}
	 * 
	 * @see <a href=
	 *      "https://docs.particle.io/reference/cloud-apis/api/#get-a-stream-of-your-events">Particle
	 *      Cloud API Docs</a>
	 */
	public HttpUrl getAllDeviceEvents(String accessToken) {
		Builder builder = new HttpUrl.Builder();
		
		builder.scheme(PARTICLE_SCHEME)
			.host(PARTICLE_HOST_URL)
			.addPathSegment(PARTICLE_API_VERSION)
			.addPathSegment("devices")
			.addPathSegment("events")
			.addQueryParameter(PARTICLE_ACCESS_TOKEN_KEY, accessToken);
		
		return builder.build();
	}

	/**
	 * 
	 * Open a stream of Server Sent Events for all public and private events for
	 * your specific devices. 
	 * <br><br>
	 * Returns only events matching deviceId.
	 * 
	 * @param accessToken - Particle Cloud API access token
	 * @param deviceId    - Device id to filter events from
	 * 
	 * @see <a href=
	 *      "https://docs.particle.io/reference/cloud-apis/api/#get-a-stream-of-events-for-a-device">Particle
	 *      Cloud API Docs</a>
	 * 
	 * @return {@link HttpUrl}
	 */
	public HttpUrl getDeviceEvents(String accessToken, String deviceId) {
		Builder builder = new HttpUrl.Builder();
		
		builder.scheme(PARTICLE_SCHEME)
			.host(PARTICLE_HOST_URL)
			.addPathSegment(PARTICLE_API_VERSION)
			.addPathSegment("devices")
			.addPathSegment(deviceId)
			.addPathSegment("events")
			.addQueryParameter(PARTICLE_ACCESS_TOKEN_KEY, accessToken);
		
		return builder.build();
	}

	/**
	 * 
	 * Open a stream of Server Sent Events for all public and private events for
	 * your specific devices. 
	 * <br><br>
	 * Returns only events matching deviceId and eventPrefix.
	 * 
	 * @param accessToken - Particle Cloud API access token
	 * @param eventPrefix - Prefix to filter events on
	 * @param deviceId    - Device id to filter events from
	 * 
	 * @see <a href=
	 *      "https://docs.particle.io/reference/cloud-apis/api/#get-a-stream-of-events-for-a-device">Particle
	 *      Cloud API Docs</a>
	 * 
	 * @return {@link HttpUrl}
	 */
	public HttpUrl getDeviceEventsFiltered(String accessToken, String deviceId, String eventPrefix) {
		Builder builder = new HttpUrl.Builder();
		
		builder.scheme(PARTICLE_SCHEME)
			.host(PARTICLE_HOST_URL)
			.addPathSegment(PARTICLE_API_VERSION)
			.addPathSegment("devices")
			.addPathSegment(deviceId)
			.addPathSegment("events")
			.addPathSegment(eventPrefix)
			.addQueryParameter(PARTICLE_ACCESS_TOKEN_KEY, accessToken);
		
		return builder.build();
	}

	/**
	 * 
	 * Open a stream of Server Sent Events for all public and private events for a
	 * product.
	 * <br><br>
	 * Returns events for products matching productIdOrSlug and eventPrefix.
	 * 
	 * @param accessToken 	  - Particle Cloud API access token
	 * @param productIdOrSlug - Product ID or slug
	 * @param eventPrefix     - Prefix to filter events on
	 * 
	 * @see <a href=
	 *      "https://docs.particle.io/reference/cloud-apis/api/#product-event-stream">Particle
	 *      Cloud API Docs</a>
	 * 
	 * @return {@link HttpUrl}
	 */
	public HttpUrl getProductEventsFiltered(String accessToken, String productIdOrSlug, String eventPrefix) {
		Builder builder = new HttpUrl.Builder();
		
		builder.scheme(PARTICLE_SCHEME)
			.host(PARTICLE_HOST_URL)
			.addPathSegment(PARTICLE_API_VERSION)
			.addPathSegment("products")
			.addPathSegment(productIdOrSlug)
			.addPathSegment("events")
			.addPathSegment(eventPrefix)
			.addQueryParameter(PARTICLE_ACCESS_TOKEN_KEY, accessToken);
		
		return builder.build();
	}

	/**
	 * 
	 * Open a stream of Server Sent Events for all public and private events for a
	 * product.
	 * <br><br>
	 * Returns events for products matching productIdOrSlug.
	 * 
	 * @param accessToken     - Particle Cloud API access token
	 * @param productIdOrSlug - Product ID or slug
	 * 
	 * @see <a href=
	 *      "https://docs.particle.io/reference/cloud-apis/api/#product-event-stream">Particle
	 *      Cloud API Docs</a>
	 * 
	 * @return {@link HttpUrl}
	 */
	public HttpUrl getProductEvents(String accessToken, String productIdOrSlug) {
		Builder builder = new HttpUrl.Builder();
		
		builder.scheme(PARTICLE_SCHEME)
			.host(PARTICLE_HOST_URL)
			.addPathSegment(PARTICLE_API_VERSION)
			.addPathSegment("products")
			.addPathSegment(productIdOrSlug)
			.addPathSegment("events")
			.addQueryParameter(PARTICLE_ACCESS_TOKEN_KEY, accessToken);
		
		return builder.build();
	}
	

	/**
	 * Refresh diagnostic vitals for a single device.
     * <br><br>
     * This will instruct the device to publish a new event to the Device Cloud containing a
     * device vitals payload. This is an asynchronous request: the HTTP request returns immediately 
     * after the request to the device is sent. In order for the device to respond with a vitals payload,
     * it must be online and connected to the Device Cloud. The device will respond by publishing an event  
     * named <i>spark/device/diagnostics/update</i>.  
     * <br><br>
	 * 
	 * @param accessToken - Particle Cloud API access token
	 * @param deviceId    - Device id target to refresh vitals
	 * 
	 * @see <a href=
	 *      "https://docs.particle.io/reference/cloud-apis/api/#refresh-device-vitals">Particle
	 *      Cloud API Docs</a>
	 * 
	 * @return {@link HttpUrl}
	 */
	public HttpUrl refreshDeviceVitals(String accessToken, String deviceId) {
		Builder builder = new HttpUrl.Builder();
		
		builder.scheme(PARTICLE_SCHEME)
			.host(PARTICLE_HOST_URL)
			.addPathSegment(PARTICLE_API_VERSION)
			.addPathSegment("diagnostics")
			.addPathSegment(deviceId)
			.addPathSegment("update")
			.addQueryParameter(PARTICLE_ACCESS_TOKEN_KEY, accessToken);
		
		return builder.build();
	}

	// TODO: docs
	public HttpUrl getDeviceInformation(String accessToken, String deviceId) {
		Builder builder = new HttpUrl.Builder();
		
		builder.scheme(PARTICLE_SCHEME)
			.host(PARTICLE_HOST_URL)
			.addPathSegment(PARTICLE_API_VERSION)
			.addPathSegment("devices")
			.addPathSegment(deviceId)
			.addQueryParameter(PARTICLE_ACCESS_TOKEN_KEY, accessToken);
		
		return builder.build();
	}

    public HttpUrl listAllProducts(String accessToken) {
		Builder builder = new HttpUrl.Builder();
		
		builder.scheme(PARTICLE_SCHEME)
			.host(PARTICLE_HOST_URL)
			.addPathSegment(PARTICLE_API_VERSION)
			.addPathSegment("user")
			.addPathSegment("products")
			.addQueryParameter(PARTICLE_ACCESS_TOKEN_KEY, accessToken);
		
		return builder.build();
    }
	
}
