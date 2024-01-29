package com.fermi4.particle;

import static com.fermi4.particle.sse.LatchedEventSourceListener.latched;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

public class ParticleIOHttpEventListenerTest {
	
	
	
	@Test
	public void test() throws InterruptedException, IOException {
		MockWebServer server = new MockWebServer();
		try {
			server.start();

			// Enqueue a response with SSE events
			String sseResponseBody = "event: temperature\ndata: {\"data\":\"23.34\",\"ttl\":\"60\",\"published_at\":\"2015-07-18T00:12:18.174Z\",\"coreid\":\"0123456789abcdef01234567\"}\n\n"
					+ "event: temperature\ndata: {\"data\":\"24.34\",\"ttl\":\"60\",\"published_at\":\"2015-07-18T00:12:18.174Z\",\"coreid\":\"0123456789abcdef01234567\"}\n\n"
					+ "event: temperature\ndata: {\"data\":\"25.34\",\"ttl\":\"60\",\"published_at\":\"2015-07-18T00:12:18.174Z\",\"coreid\":\"0123456789abcdef01234567\"}\n\n";
			
			MockResponse mockResponse = new MockResponse()
					.setHeader("Content-Type", "text/event-stream")
					.setBody(sseResponseBody);
			
			server.enqueue(mockResponse);
			
			EventSourceListener particle_listener = Mockito.mock(QueueingEventSourceListener.class);
			
			/**
			 *  This is used to latch when onEvent is called 
			 *  instead of having a hardcoded wait time
			 */
			CountDownLatch onEventLatch = new CountDownLatch(3);
			EventSourceListener listener = latched(particle_listener, onEventLatch);
			
			OkHttpClient client = new OkHttpClient.Builder().build();
			
			new HttpUrl.Builder()
				.addQueryParameter("access_token", "1233123123")
				.build();
			
			
			Request request = new Request.Builder()
					.url(server.url("/sse")) // endpoint doesn't matter
					.build();
			/**
			 * This creates the event source immediately and starts sending events
			 */   
			EventSource.Factory factory = EventSources.createFactory(client);
			EventSource eventSource = factory.newEventSource(request, listener);
			System.out.println(eventSource.request());
			
			if(!onEventLatch.await(10, TimeUnit.SECONDS)) {
				throw new AssertionError("ParticleIOHttpEventListener.onEvent not called within 5 second window!");
			}
			
		}catch (Exception e) {
			//
		}finally {
			server.close();
		}

		
		
		
		
		
	}
	
}
