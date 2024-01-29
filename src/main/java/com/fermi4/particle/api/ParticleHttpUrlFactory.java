package com.fermi4.particle.api;

import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
/**
 * 
 * This provides utility methods for the {@link HttpUrl}
 * 
 * 
 * @author Fermi-4
 *
 */
public class ParticleHttpUrlFactory {
	private static final String PARTICLE_HOST_URL = "api.particle.io";
	private static final String PARTICLE_SCHEME = "https";
	private static final String PARTICLE_API_VERSION = "v1";
	private static final String PARTICLE_ACCESS_TOKEN_KEY = "access_token";
	

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
	public static HttpUrl getAllEventsFiltered(String accessToken, String eventPrefix) {
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
	public static HttpUrl getAllDeviceEventsFiltered(String accessToken, String eventPrefix) {
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
	public static HttpUrl getAllDeviceEvents(String accessToken) {
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
	public static HttpUrl getDeviceEvents(String accessToken, String deviceId) {
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
	public static HttpUrl getDeviceEventsFiltered(String accessToken, String deviceId, String eventPrefix) {
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
	public static HttpUrl getProductEventsFiltered(String accessToken, String productIdOrSlug, String eventPrefix) {
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
	public static HttpUrl getProductEvents(String accessToken, String productIdOrSlug) {
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
	
}
