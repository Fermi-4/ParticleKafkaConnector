package com.fermi4.particle.sse;

import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

public abstract class AbstractEventSourceListenerDecorator extends EventSourceListener {

	private EventSourceListener delegate;
	
	public AbstractEventSourceListenerDecorator(EventSourceListener delegate) {
		this.delegate = delegate;
	}

	@Override
	public void onClosed(EventSource eventSource) {
		delegate.onClosed(eventSource);
	}

	@Override
	public void onEvent(EventSource eventSource, String id, String type, String data) {
		delegate.onEvent(eventSource, id, type, data);
	}

	@Override
	public void onFailure(EventSource eventSource, Throwable t, Response response) {
		delegate.onFailure(eventSource, t, response);
	}

	@Override
	public void onOpen(EventSource eventSource, Response response) {
		delegate.onOpen(eventSource, response);
	}
	
}
