package com.fermi4.particle.sse.listener;

import okhttp3.Response;
import okhttp3.sse.EventSource;

public interface OnFailureObserver {
	public void notifyFailure(EventSource eventSource, Throwable t, Response response);
}
