package com.fermi4.particle.sse.listener;

import com.fermi4.particle.sse.SSEEventProvider;

import okhttp3.Response;
import okhttp3.sse.EventSource;

/**
 * This adapter binds our SSEProvider and the obs interface
 */
public class SSEProviderListenerAdapter implements OnFailureObserver {
	
	SSEEventProvider provider;

	public SSEProviderListenerAdapter(SSEEventProvider provider) {
		super();
		this.provider = provider;
	}

	@Override
	public void notifyFailure(EventSource eventSource, Throwable t, Response response) {
		provider.setActive(false);
	}
	
	public static OnFailureObserver deactivateOnFailure(SSEEventProvider provider) {
		return new SSEProviderListenerAdapter(provider);
	}
	
}
