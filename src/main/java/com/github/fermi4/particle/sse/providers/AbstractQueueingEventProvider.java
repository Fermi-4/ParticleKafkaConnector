package com.github.fermi4.particle.sse.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.kafka.connect.errors.ConnectException;

import io.github.fermi4.particle.v1.ParticleClient;
import com.github.fermi4.particle.sse.Event;
import com.github.fermi4.particle.sse.EventProvider;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

@AllArgsConstructor
@RequiredArgsConstructor
public abstract class AbstractQueueingEventProvider extends EventSourceListener implements EventProvider {
	
	protected EventSource source;
	protected ParticleClient particleClient;
	protected BlockingQueue<Event> queue;
	
	public AbstractQueueingEventProvider(ParticleClient particleClient, BlockingQueue<Event> queue) {
		this.particleClient = particleClient;
		this.queue = queue;
	}
	
	@Override
	public void stop() {
		this.source.cancel();
	}

	@Override
	public List<Event> get() {
		List<Event> events = new ArrayList<>();
		this.queue.drainTo(events);
		return events;
	}
	
	@Override
	public void onEvent(EventSource eventSource, String id, String type, String data) {
		try {
			System.out.println("AbstractQueueingEventProvider::onEvent got event id: " + id + " data: " + data + " type: " + type);
			this.queue.put(new Event(id, type, data));
		} catch (InterruptedException e) {
			throw new ConnectException(e);
		}
	}
	
	@Override
	public void onFailure(EventSource eventSource, Throwable t, Response response) {
		this.source.cancel();
		this.start();
	}

	
}
