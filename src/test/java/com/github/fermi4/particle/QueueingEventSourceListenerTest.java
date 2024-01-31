package com.github.fermi4.particle;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.fermi4.particle.sse.Event;
import com.github.fermi4.particle.sse.listener.QueueingEventSourceListener;

import okhttp3.sse.EventSource;



public class QueueingEventSourceListenerTest {
	
	/**
	 * Test the interaction between a queue and our custom Listener
	 * 
	 * Our listener should load the Event object into the queue
	 * when onEvent is called
	 */
	@Test
	public void testThatQueueReceivedEvents() {
		
		/* Create and bind instances */
		BlockingQueue<Event> sharedQueue = new LinkedBlockingQueue<>();
		QueueingEventSourceListener listener = new QueueingEventSourceListener(sharedQueue);
		
		/* Expected data returned + mocked source */
		String EXPECTED_ID = "id";
		String EXPECTED_TYPE = "type";
		String EXPECTED_DATA = "data";
		Integer EXPECTED_SIZE = 1;
		EventSource fakeSource = Mockito.mock(EventSource.class);
		
		/* Trigger fake event */
		listener.onEvent(fakeSource, EXPECTED_ID, EXPECTED_TYPE, EXPECTED_DATA);
		
		/* Assert queue was loaded and with correct data */
		Assertions.assertEquals(EXPECTED_SIZE.intValue(), sharedQueue.size());
		
		/* Assert the data is correct */
		Event event = sharedQueue.poll();
		Assertions.assertEquals(EXPECTED_ID, event.getId());
		Assertions.assertEquals(EXPECTED_TYPE, event.getType());
		Assertions.assertEquals(EXPECTED_DATA, event.getData());
		
	}
}
