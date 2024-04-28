package com.github.fermi4.particle.api.http;

import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;

public interface ParticleApiFactory {
	HttpUrl listDevicesInProductGroups(String accessToken, String productIdOrSlug, List<String> groups, Map<String, String> queryParameters);
	HttpUrl listDevicesInProduct(String accessToken, String productIdOrSlug, Map<String, String> queryParameters);
	HttpUrl listAllDevices(String accessToken);
	HttpUrl listAllProducts(String accessToken);
	HttpUrl refreshDeviceVitals(String accessToken, String deviceId);
	HttpUrl getDeviceInformation(String accessToken, String deviceId);
	HttpUrl getAllEventsFiltered(String accessToken, String eventPrefix);
	HttpUrl getAllDeviceEventsFiltered(String accessToken, String eventPrefix);
	HttpUrl getAllDeviceEvents(String accessToken);
	HttpUrl getDeviceEvents(String accessToken, String deviceId);
	HttpUrl getDeviceEventsFiltered(String accessToken, String deviceId, String eventPrefix);
	HttpUrl getProductEventsFiltered(String accessToken, String productIdOrSlug, String eventPrefix);
	HttpUrl getProductEvents(String accessToken, String productIdOrSlug);
	
	public static ParticleApiFactory v1() {
		return new ParticleApiFactoryV1();
	}
}
