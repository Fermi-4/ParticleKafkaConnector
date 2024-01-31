package com.github.fermi4.particle.sse.listener;

import com.github.fermi4.particle.sse.EventProvider;

import okhttp3.Response;
import okhttp3.sse.EventSource;

/**
 * This adapter binds our EventProvider and the Listener interface
 */
public class EventProviderListenerAdapter implements EventListenerObserver {
	
	EventProvider provider;

	public EventProviderListenerAdapter(EventProvider provider) {
		super();
		this.provider = provider;
	}

	public static EventListenerObserver deactivateOnFailure(EventProvider provider) {
		return new EventProviderListenerAdapter(provider);
	}

	@Override
	public void notifyFailure(EventSource eventSource, Throwable t, Response response) {
		provider.setActive(false);
	}
	
	@Override
	public void notifyClosed(EventSource eventSource) {
		provider.setActive(false);
	}

	@Override
	public void notifyOnOpen(EventSource eventSource, Response response) {
		// Do nothing
	}

	@Override
	public void notifyOnEvent(EventSource eventSource, String id, String type, String data) {
		// Do nothing
		
	}
	
}
