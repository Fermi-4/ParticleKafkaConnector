package com.github.fermi4.particle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.api.http.ParticleApiFactory;


public class ParticleHttpUrlFactoryTest {
	
	public static String FAKE_ACCESS_TOKEN = "fake_access_token";
	public static String FAKE_PREFIX = "fake_prefix";
	public static String FAKE_DEVICE_ID = "fake_device_id";
	public static String FAKE_PRODUCT_ID = "fake_product_id";

	@Test
	public void testGetAllEvents_Filtered() {
		ParticleApiFactory factory = ParticleApiFactory.v1();
		String expected = String.format("https://api.particle.io/v1/events/%s?access_token=%s", FAKE_PREFIX, FAKE_ACCESS_TOKEN);
		Assertions.assertEquals(expected, factory.getAllEventsFiltered(FAKE_ACCESS_TOKEN, FAKE_PREFIX).toString());
	}
	
	@Test
	public void testGetAllDeviceEvents_Filtered() {
		ParticleApiFactory factory = ParticleApiFactory.v1();
		String expected = String.format("https://api.particle.io/v1/devices/events/%s?access_token=%s", FAKE_PREFIX, FAKE_ACCESS_TOKEN);
		Assertions.assertEquals(expected, factory.getAllDeviceEventsFiltered(FAKE_ACCESS_TOKEN, FAKE_PREFIX).toString());
	}
	
	@Test
	public void testDeviceEvents_Filtered() {
		ParticleApiFactory factory = ParticleApiFactory.v1();
		String expected = String.format("https://api.particle.io/v1/devices/%s/events/%s?access_token=%s", FAKE_DEVICE_ID, FAKE_PREFIX, FAKE_ACCESS_TOKEN);
		Assertions.assertEquals(expected, factory.getDeviceEventsFiltered(FAKE_ACCESS_TOKEN, FAKE_DEVICE_ID, FAKE_PREFIX).toString());
	}
	
	@Test
	public void testGetProductEvents() {
		ParticleApiFactory factory = ParticleApiFactory.v1();
		String expected = String.format("https://api.particle.io/v1/products/%s/events?access_token=%s", FAKE_PRODUCT_ID, FAKE_ACCESS_TOKEN);
		Assertions.assertEquals(expected, factory.getProductEvents(FAKE_ACCESS_TOKEN, FAKE_PRODUCT_ID).toString());
	}
	
	@Test
	public void testGetProductEvents_Filtered() {
		ParticleApiFactory factory = ParticleApiFactory.v1();
		String expected = String.format("https://api.particle.io/v1/products/%s/events/%s?access_token=%s", FAKE_PRODUCT_ID, FAKE_PREFIX, FAKE_ACCESS_TOKEN);
		Assertions.assertEquals(expected, factory.getProductEventsFiltered(FAKE_ACCESS_TOKEN, FAKE_PRODUCT_ID, FAKE_PREFIX).toString());
	}

	@Test
	public void testListAllDevices() {
		ParticleApiFactory factory = ParticleApiFactory.v1();
		String expected = String.format("https://api.particle.io/v1/devices?access_token=%s", FAKE_ACCESS_TOKEN);
		Assertions.assertEquals(expected, factory.listAllDevices(FAKE_ACCESS_TOKEN).toString());
	}

	// TODO: test if this encoding of comma is an issue in url
	@Test
	public void testListDevicesInProductGroups() {
		ParticleApiFactory factory = ParticleApiFactory.v1();
		List<String> fakeGroups = List.of("group1", "group2");
		String page = "1";
		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("page", page);
		String expected = String.format("https://api.particle.io/v1/products/%s/devices?access_token=%s&groups=%s&page=%s", FAKE_PRODUCT_ID, FAKE_ACCESS_TOKEN, "group1%2Cgroup2", page);
		Assertions.assertEquals(expected, factory.listDevicesInProductGroups(FAKE_ACCESS_TOKEN, FAKE_PRODUCT_ID, fakeGroups, queryParams).toString());
	}
	
	@Test
	public void testListDevicesInProduct() {
		ParticleApiFactory factory = ParticleApiFactory.v1();
		Map<String, String> queryParams = new HashMap<>();
		String expected = String.format("https://api.particle.io/v1/products/%s/devices?access_token=%s", FAKE_PRODUCT_ID, FAKE_ACCESS_TOKEN);
		Assertions.assertEquals(expected, factory.listDevicesInProduct(FAKE_ACCESS_TOKEN, FAKE_PRODUCT_ID, queryParams).toString());
	}
}
