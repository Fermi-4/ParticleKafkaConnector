package com.fermi4.particle;
import static com.fermi4.particle.api.ParticleHttpUrlFactory.getAllDeviceEventsFiltered;
import static com.fermi4.particle.api.ParticleHttpUrlFactory.getAllEventsFiltered;
import static com.fermi4.particle.api.ParticleHttpUrlFactory.getDeviceEventsFiltered;
import static com.fermi4.particle.api.ParticleHttpUrlFactory.getProductEvents;
import static com.fermi4.particle.api.ParticleHttpUrlFactory.getProductEventsFiltered;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ParticleHttpUrlFactoryTest {

	@Test
	public void testGetAllEvents_Filtered() {
		String accessToken = "3567939723";
		String prefix = "temp";
		String expected = String.format("https://api.particle.io/v1/events/%s?access_token=%s", prefix, accessToken);
		Assertions.assertEquals(expected, getAllEventsFiltered(accessToken, prefix).toString());
	}
	
	@Test
	public void testGetAllDeviceEvents_Filtered() {
		String accessToken = "3567939723";
		String prefix = "temp";
		String expected = String.format("https://api.particle.io/v1/devices/events/%s?access_token=%s", prefix, accessToken);
		Assertions.assertEquals(expected, getAllDeviceEventsFiltered(accessToken, prefix).toString());
	}
	
	@Test
	public void testDeviceEvents_Filtered() {
		String accessToken = "3567939723";
		String deviceId = "1995350697";
		String prefix = "temp";
		String expected = String.format("https://api.particle.io/v1/devices/%s/events/%s?access_token=%s", deviceId, prefix, accessToken);
		Assertions.assertEquals(expected, getDeviceEventsFiltered(accessToken, deviceId, prefix).toString());
	}
	
	@Test
	public void testGetProductEvents() {
		String accessToken = "3567939723";
		String productId = "1995350697";
		String expected = String.format("https://api.particle.io/v1/products/%s/events?access_token=%s", productId, accessToken);
		Assertions.assertEquals(expected, getProductEvents(accessToken, productId).toString());
	}
	
	@Test
	public void testGetProductEvents_Filtered() {
		String accessToken = "3567939723";
		String productId = "1995350697";
		String prefix = "temp";
		String expected = String.format("https://api.particle.io/v1/products/%s/events/%s?access_token=%s", productId, prefix, accessToken);
		Assertions.assertEquals(expected, getProductEventsFiltered(accessToken, productId, prefix).toString());
	}
	
}
