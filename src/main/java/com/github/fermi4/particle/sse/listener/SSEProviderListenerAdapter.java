package com.github.fermi4.particle.sse.listener;

import com.github.fermi4.particle.sse.SSEEventProvider;

import okhttp3.Response;
import okhttp3.sse.EventSource;

/**
 * This adapter binds our SSEProvider and the obs interface
 */
public class SSEProviderListenerAdapter implements EventListenerObserver {
	
	SSEEventProvider provider;

	public SSEProviderListenerAdapter(SSEEventProvider provider) {
		super();
		this.provider = provider;
	}

	public static EventListenerObserver deactivateOnFailure(SSEEventProvider provider) {
		return new SSEProviderListenerAdapter(provider);
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
