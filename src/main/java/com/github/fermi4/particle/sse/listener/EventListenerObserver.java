package com.github.fermi4.particle.sse.listener;

import okhttp3.Response;
import okhttp3.sse.EventSource;

public interface EventListenerObserver {
	public void notifyOnOpen(EventSource eventSource, Response response);
	public void notifyOnEvent(EventSource eventSource, String id, String type, String data);
	public void notifyFailure(EventSource eventSource, Throwable t, Response response);
	public void notifyClosed(EventSource eventSource);
}
