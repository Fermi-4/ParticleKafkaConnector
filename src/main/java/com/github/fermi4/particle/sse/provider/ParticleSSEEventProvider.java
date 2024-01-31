package com.github.fermi4.particle.sse.provider;

import static com.github.fermi4.particle.sse.listener.SSEProviderListenerAdapter.deactivateOnFailure;
import static com.github.fermi4.particle.sse.listener.deco.OnFailureNotifyDecorator.onFailureNotify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.github.fermi4.particle.ParticleEndpointSupplier;
import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.sse.SSEEvent;
import com.github.fermi4.particle.sse.SSEEventProvider;
import com.github.fermi4.particle.sse.listener.QueueingEventSourceListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ParticleSSEEventProvider implements SSEEventProvider {

	private OkHttpClient client;
	private EventSource eventSource;
	private EventSourceListener eventListener;
	private Request request;
	private ParticleConnectorConfig config;
	private BlockingQueue<SSEEvent> sseQueue;
	
	@Builder.Default
	private boolean isActive = false;
	
	public ParticleSSEEventProvider(ParticleConnectorConfig config) {
		this.config = config;
		this.client = new OkHttpClient.Builder()
				// TODO: provide connect timeout option in config - default to never timeout
				.connectTimeout(0, TimeUnit.MILLISECONDS)
				.readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
				.build();
		this.sseQueue = new LinkedBlockingQueue<>();
		
		/* 
		 * Base class is queueing event source listener which 
		 * makes events available through the use of a shared queue 
		 * 
		 * then applying two decorators here... 
		 * 		- onFailureNotify (decorates listener): notifies subscriber of failure mode
		 * 		- deactivateOnFailure (decorates this): when failure notification happens it will deactivate this class (sets active as false)
		 * 
		 * this should then trigger the task to reconnect
		 *
		 */
		this.eventListener = onFailureNotify(new QueueingEventSourceListener(sseQueue), Arrays.asList(deactivateOnFailure(this)));
		this.request = new Request.Builder()
				.addHeader("Content-Type", "text/event-stream")
				.addHeader("Connection", "keep-alive")
				.addHeader("Cache-Control", "no-cache")
				.url(ParticleEndpointSupplier.get(this.config))
				.build();
		this.isActive = false;
	}

	@Override
	public void start() {
		// TODO: need to see how this fails when connection cannot be established 
		this.eventSource = EventSources.createFactory(this.client).newEventSource(this.request, this.eventListener);
		this.isActive = true;
	}

	@Override
	public void stop() {
		this.eventSource.cancel();
		this.isActive = false;
	}

	@Override
	public List<SSEEvent> get() {
		List<SSEEvent> events = new ArrayList<>();
		this.sseQueue.drainTo(events);
		return events;
	}

	@Override
	public boolean isActive() {
		return this.isActive;
	}

	@Override
	public void setActive(boolean active) {
		this.isActive = active;
	}

}
