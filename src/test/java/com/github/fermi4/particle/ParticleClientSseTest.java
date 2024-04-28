package com.github.fermi4.particle;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.ACCESS_TOKEN_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.DEVICE_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_DEVICE;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.TOPIC_CONFIG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.api.ParticleClient;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

public class ParticleClientSseTest {

	MockWebServer mockWebServer;

	private final String TOPIC = "test_topic";
	private final String ACCESS_KEY = "test_topic";
	private final String FAKE_DEVICE_ID = "123456789";

	@BeforeEach
	public void setUp() throws Exception {
		// Start MockWebServer
		mockWebServer = new MockWebServer();

		String sseResponseBody = "event: temperature\ndata: {\"data\":\"23.34\",\"ttl\":\"60\",\"published_at\":\"2015-07-18T00:12:18.174Z\",\"coreid\":\"0123456789abcdef01234567\"}\n\n"
				+ "event: temperature\ndata: {\"data\":\"24.34\",\"ttl\":\"60\",\"published_at\":\"2015-07-18T00:12:18.174Z\",\"coreid\":\"0123456789abcdef01234567\"}\n\n"
				+ "event: temperature\ndata: {\"data\":\"25.34\",\"ttl\":\"60\",\"published_at\":\"2015-07-18T00:12:18.174Z\",\"coreid\":\"0123456789abcdef01234567\"}\n\n";

		MockResponse mockResponse = new MockResponse().setHeader("Content-Type", "text/event-stream")
				.setBody(sseResponseBody);

		mockWebServer.enqueue(mockResponse);
		mockWebServer.start();

	}

	@AfterEach
	public void tearDown() throws Exception {
		// Shutdown MockWebServer
		if (mockWebServer != null) {
			mockWebServer.shutdown();
		}
	}

	@Test
	public void testParticleEventProvider() throws InterruptedException {

		int EXPECTED_DATA_COUNT = 3;
		Map<String, String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);
		map.put(DEVICE_ID_CONFIG, FAKE_DEVICE_ID);

		ParticleClient client = ParticleClient.builder().client(new OkHttpClient.Builder().build()).build();

		Request targetRequest = new Request.Builder().url(mockWebServer.url("/sse")).build();
		CountDownLatch onEventLatch = new CountDownLatch(EXPECTED_DATA_COUNT);
		AtomicInteger count = new AtomicInteger(0);
		client.createEventSource(targetRequest, new EventSourceListener() {
			@Override
			public void onEvent(EventSource eventSource, String id, String type, String data) {
				count.incrementAndGet();
				System.out.println("Received event: " + count);
				System.out.println("Event data: " + data);
				onEventLatch.countDown();
			}
		});

		if (!onEventLatch.await(30, TimeUnit.SECONDS)) {
			throw new AssertionError("ParticleIOHttpEventListener.onEvent not called expected number of times within 10 second window!");
		}
		System.out.println("Total received events: " + count.get());
		Assertions.assertEquals(EXPECTED_DATA_COUNT, count.get());
	}
}
