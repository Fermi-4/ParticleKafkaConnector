package com.github.fermi4.particle;
import static com.github.fermi4.particle.api.ParticleHttpUrlFactory.getAllDeviceEventsFiltered;
import static com.github.fermi4.particle.api.ParticleHttpUrlFactory.getAllEventsFiltered;
import static com.github.fermi4.particle.api.ParticleHttpUrlFactory.getDeviceEventsFiltered;
import static com.github.fermi4.particle.api.ParticleHttpUrlFactory.getProductEvents;
import static com.github.fermi4.particle.api.ParticleHttpUrlFactory.getProductEventsFiltered;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ParticleHttpUrlFactoryTest {
	
	public static String FAKE_ACCESS_TOKEN = "fake_access_token";
	public static String FAKE_PREFIX = "fake_prefix";
	public static String FAKE_DEVICE_ID = "fake_device_id";
	public static String FAKE_PRODUCT_ID = "fake_product_id";

	@Test
	public void testGetAllEvents_Filtered() {
		String expected = String.format("https://api.particle.io/v1/events/%s?access_token=%s", FAKE_PREFIX, FAKE_ACCESS_TOKEN);
		Assertions.assertEquals(expected, getAllEventsFiltered(FAKE_ACCESS_TOKEN, FAKE_PREFIX).toString());
	}
	
	@Test
	public void testGetAllDeviceEvents_Filtered() {
		String expected = String.format("https://api.particle.io/v1/devices/events/%s?access_token=%s", FAKE_PREFIX, FAKE_ACCESS_TOKEN);
		Assertions.assertEquals(expected, getAllDeviceEventsFiltered(FAKE_ACCESS_TOKEN, FAKE_PREFIX).toString());
	}
	
	@Test
	public void testDeviceEvents_Filtered() {
		String expected = String.format("https://api.particle.io/v1/devices/%s/events/%s?access_token=%s", FAKE_DEVICE_ID, FAKE_PREFIX, FAKE_ACCESS_TOKEN);
		Assertions.assertEquals(expected, getDeviceEventsFiltered(FAKE_ACCESS_TOKEN, FAKE_DEVICE_ID, FAKE_PREFIX).toString());
	}
	
	@Test
	public void testGetProductEvents() {
		String expected = String.format("https://api.particle.io/v1/products/%s/events?access_token=%s", FAKE_PRODUCT_ID, FAKE_ACCESS_TOKEN);
		Assertions.assertEquals(expected, getProductEvents(FAKE_ACCESS_TOKEN, FAKE_PRODUCT_ID).toString());
	}
	
	@Test
	public void testGetProductEvents_Filtered() {
		String expected = String.format("https://api.particle.io/v1/products/%s/events/%s?access_token=%s", FAKE_PRODUCT_ID, FAKE_PREFIX, FAKE_ACCESS_TOKEN);
		Assertions.assertEquals(expected, getProductEventsFiltered(FAKE_ACCESS_TOKEN, FAKE_PRODUCT_ID, FAKE_PREFIX).toString());
	}
	
}
