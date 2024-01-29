package com.fermi4.particle.sse;

import java.util.concurrent.CountDownLatch;

import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

public class LatchedEventSourceListener extends AbstractEventSourceListenerDecorator {

	private CountDownLatch onEventLatch;
	
	public LatchedEventSourceListener(EventSourceListener delegate, CountDownLatch onEventLatch) {
		super(delegate);
		this.onEventLatch = onEventLatch;
	}

	
	// TODO: add latches for other methods
	@Override
	public void onEvent(EventSource eventSource, String id, String type, String data) {
		super.onEvent(eventSource, id, type, data);
		this.onEventLatch.countDown();
	}

	public static EventSourceListener latched(EventSourceListener delegate, CountDownLatch latch) {
		return new LatchedEventSourceListener(delegate, latch);
	}
	
}
