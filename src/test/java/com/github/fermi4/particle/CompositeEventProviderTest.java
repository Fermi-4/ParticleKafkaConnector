package com.github.fermi4.particle;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.ACCESS_TOKEN_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.DEVICE_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_DEVICE;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.TOPIC_CONFIG;
import static com.github.fermi4.particle.sse.listener.LatchedEventSourceListener.latched;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.sse.SSEEvent;
import com.github.fermi4.particle.sse.SSEEventProvider;
import com.github.fermi4.particle.sse.listener.QueueingEventSourceListener;
import com.github.fermi4.particle.sse.provider.CompositeEventProvider;
import com.github.fermi4.particle.sse.provider.ParticleSSEEventProvider;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class CompositeEventProviderTest {
	
	MockWebServer mockWebServer1;
	MockWebServer mockWebServer2;
	private final String TOPIC = "test_topic";
	private final String ACCESS_KEY = "test_topic";
	private final String FAKE_DEVICE_ID = "123456789";
	
	@BeforeEach
	public void setUp() throws Exception {
		String sseResponseBody = "event: temperature\ndata: {\"data\":\"23.34\",\"ttl\":\"60\",\"published_at\":\"2015-07-18T00:12:18.174Z\",\"coreid\":\"0123456789abcdef01234567\"}\n\n"
				+ "event: temperature\ndata: {\"data\":\"24.34\",\"ttl\":\"60\",\"published_at\":\"2015-07-18T00:12:18.174Z\",\"coreid\":\"0123456789abcdef01234567\"}\n\n"
				+ "event: temperature\ndata: {\"data\":\"25.34\",\"ttl\":\"60\",\"published_at\":\"2015-07-18T00:12:18.174Z\",\"coreid\":\"0123456789abcdef01234567\"}\n\n";
		
		MockResponse mockResponse = new MockResponse().setHeader("Content-Type", "text/event-stream")
				.setBody(sseResponseBody);

		// Start MockWebServer
		mockWebServer1 = new MockWebServer();
		mockWebServer1.enqueue(mockResponse);
		mockWebServer1.start();
		
		mockWebServer2 = new MockWebServer();
		mockWebServer2.enqueue(mockResponse);
		mockWebServer2.start();

	}
	
	@AfterEach
	public void tearDown() throws Exception {
		// Shutdown MockWebServer
		if (mockWebServer1 != null) {
			mockWebServer1.shutdown();
		}
		if (mockWebServer2 != null) {
			mockWebServer2.shutdown();
		}
	}
	
	/**
	 * 
	 * Tests the CompositeEventProvider with two fake SSE providers pointing to separate servers.
     * Joins them into a single composite provider. The total data received should be the sum
     * of both providers if the composite worked correctly.
	 * 
	 * @throws InterruptedException
	 * @throws AssertionError
	 */
	@Test
	public void testTwoProvidersInComposite() throws InterruptedException, AssertionError {
		
		int EXPECTED_DATA_COUNT = 6;
		Map<String, String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);
		map.put(DEVICE_ID_CONFIG, FAKE_DEVICE_ID);
		
		CountDownLatch onEventLatch = new CountDownLatch(EXPECTED_DATA_COUNT);
		BlockingQueue<SSEEvent> queue = new LinkedBlockingQueue<SSEEvent>();
		
		SSEEventProvider fakeSSEProvider1 = ParticleSSEEventProvider.builder()
				.client(new OkHttpClient.Builder().build())
				.config(new ParticleConnectorConfig(map))
				.eventListener(latched(new QueueingEventSourceListener(queue), onEventLatch))
				.sseQueue(queue)
				.request(new Request.Builder().url(mockWebServer1.url("/sse")).build())
				.build();
		
		SSEEventProvider fakeSSEProvider2 = ParticleSSEEventProvider.builder()
				.client(new OkHttpClient.Builder().build())
				.config(new ParticleConnectorConfig(map))
				.eventListener(latched(new QueueingEventSourceListener(queue), onEventLatch))
				.sseQueue(queue)
				.request(new Request.Builder().url(mockWebServer2.url("/sse")).build())
				.build();
		
		SSEEventProvider provider = CompositeEventProvider.builder()
				.addProvider(fakeSSEProvider1)
				.addProvider(fakeSSEProvider2)
				.build();
		
		provider.start();
		
		if(!onEventLatch.await(10, TimeUnit.SECONDS)) {
			throw new AssertionError("ParticleIOHttpEventListener.onEvent not called within 10 second window!");
		}
		List<SSEEvent> records = provider.get();
		Assertions.assertEquals(EXPECTED_DATA_COUNT, records.size());
	}
}
