package com.github.fermi4.particle;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_DEVICE;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.ACCESS_TOKEN_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.DEVICE_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.TOPIC_CONFIG;
import static com.github.fermi4.particle.sse.listener.LatchedEventSourceListener.latched;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.convert.ConverterContext;
import com.github.fermi4.particle.convert.EventConverterFactory;
import com.github.fermi4.particle.convert.SourceRecordConverter;
import com.github.fermi4.particle.sse.Event;
import com.github.fermi4.particle.sse.EventProvider;
import com.github.fermi4.particle.sse.listener.QueueingEventSourceListener;
import com.github.fermi4.particle.sse.provider.ParticleEventProvider;
import com.github.fermi4.particle.task.ParticleServerSentEventSourceTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class ParticleEventSourceTaskTest {

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
		ParticleConnectorConfig config = new ParticleConnectorConfig(map);
		
		CountDownLatch onEventLatch = new CountDownLatch(EXPECTED_DATA_COUNT);
		BlockingQueue<Event> queue = new LinkedBlockingQueue<Event>();
		
		EventProvider fakeSSEProvider = ParticleEventProvider.builder()
				.client(new OkHttpClient.Builder().build())
				.config(config)
				.eventConverter(EventConverterFactory.get(config))
				.eventListener(latched(new QueueingEventSourceListener(queue), onEventLatch))
				.sseQueue(queue)
				.request(new Request.Builder().url(mockWebServer.url("/sse")).build())
				.build();
		
		fakeSSEProvider.start();
		
		if(!onEventLatch.await(10, TimeUnit.SECONDS)) {
			throw new AssertionError("ParticleIOHttpEventListener.onEvent not called within 5 second window!");
		}
		List<?> records = fakeSSEProvider.get();
		Assertions.assertEquals(EXPECTED_DATA_COUNT, records.size());
	}
	
	@Test
	public void testParticleEventSourceTaskAndProvider() throws InterruptedException {
		/**
		 * This is testing the task
		 */
		int EXPECTED_DATA_COUNT = 3;
		Map<String, String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);
		map.put(DEVICE_ID_CONFIG, FAKE_DEVICE_ID);

		ParticleServerSentEventSourceTask task = new ParticleServerSentEventSourceTask();
		CountDownLatch onEventLatch = new CountDownLatch(EXPECTED_DATA_COUNT);
		BlockingQueue<Event> queue = new LinkedBlockingQueue<Event>();
		ParticleConnectorConfig config = new ParticleConnectorConfig(map);
		ConverterContext context = new ConverterContext();
		
		context.setConfig(config);
		task.setConverterContext(context);
		
		// this doesn't extract any data, most fields will be null
		task.setConverter(SourceRecordConverter.builder().build());
		
		EventProvider fakeSSEProvider = ParticleEventProvider.builder()
				.client(new OkHttpClient.Builder().build())
				.config(config)
				.eventConverter(EventConverterFactory.get(config))
				.eventListener(latched(new QueueingEventSourceListener(queue), onEventLatch))
				.sseQueue(queue)
				.request(new Request.Builder().url(mockWebServer.url("/sse")).build())
				.build();
		
		task.setConfig(config);
		task.setEventProvider(fakeSSEProvider);
		task.start(map);
		
		if(!onEventLatch.await(10, TimeUnit.SECONDS)) {
			throw new AssertionError("ParticleIOHttpEventListener.onEvent not called within 10 second window!");
		}
		
		List<SourceRecord> records = task.poll();
		records.stream().forEach(s -> System.out.println(s.toString()));
		Assertions.assertEquals(EXPECTED_DATA_COUNT, records.size());
	}
}
