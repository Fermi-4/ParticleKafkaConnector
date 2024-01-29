package com.fermi4.particle.sse.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

import com.fermi4.particle.ParticleConnectorConfig;
import com.fermi4.particle.sse.SSEEvent;
import com.fermi4.particle.sse.SSEEventProvider;

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

	public ParticleSSEEventProvider(ParticleConnectorConfig config) {
		this.config = config;
		this.client = new OkHttpClient.Builder().build();
		this.sseQueue = new LinkedBlockingQueue<>();
//		this.request = ParticleEndpointSupplier.get(this.config);
	}

	public ParticleSSEEventProvider(OkHttpClient client, EventSource eventSource, Request request,
			ParticleConnectorConfig config) {
		this.client = client;
		this.eventSource = eventSource;
		this.request = request;
		this.config = config;
		this.sseQueue = new LinkedBlockingQueue<>();
	}

	@Override
	public void start() {
		this.eventSource = EventSources.createFactory(this.client).newEventSource(this.request, this.eventListener);
	}

	@Override
	public void stop() {
		this.eventSource.cancel();
	}

	@Override
	public List<SSEEvent> get() {
		List<SSEEvent> events = new ArrayList<>();
		this.sseQueue.drainTo(events);
		return events;
	}

}
