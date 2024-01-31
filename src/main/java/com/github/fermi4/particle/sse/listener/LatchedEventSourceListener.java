package com.github.fermi4.particle.sse.listener;

import java.util.concurrent.CountDownLatch;

import com.github.fermi4.particle.sse.listener.deco.AbstractEventSourceListenerDecorator;

import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

/**
 * This is just used in testing to assert that the onEvent method was triggered
 */
public class LatchedEventSourceListener extends AbstractEventSourceListenerDecorator {

	private CountDownLatch onEventLatch;
	
	public LatchedEventSourceListener(EventSourceListener delegate, CountDownLatch onEventLatch) {
		super(delegate);
		this.onEventLatch = onEventLatch;
	}

	@Override
	public void onEvent(EventSource eventSource, String id, String type, String data) {
		this.delegate.onEvent(eventSource, id, type, data);
		this.onEventLatch.countDown();
	}

	public static EventSourceListener latched(EventSourceListener delegate, CountDownLatch latch) {
		return new LatchedEventSourceListener(delegate, latch);
	}
	
}
