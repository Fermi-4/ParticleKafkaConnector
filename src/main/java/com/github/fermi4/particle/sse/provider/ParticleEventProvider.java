package com.github.fermi4.particle.sse.provider;

import static com.github.fermi4.particle.sse.listener.EventProviderListenerAdapter.deactivateOnFailure;
import static com.github.fermi4.particle.sse.listener.deco.OnFailureNotifyDecorator.onFailureNotify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.fermi4.particle.ParticleEndpointSupplier;
import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.convert.ConverterContext;
import com.github.fermi4.particle.convert.EventConverterFactory;
import com.github.fermi4.particle.sse.Event;
import com.github.fermi4.particle.sse.EventProvider;
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
public class ParticleEventProvider implements EventProvider {

	static final Logger log = LoggerFactory.getLogger(ParticleEventProvider.class);
	
	private OkHttpClient client;
	private EventSource eventSource;
	private EventSourceListener eventListener;
	private Request request;
	private ParticleConnectorConfig config;
	private BlockingQueue<Event> sseQueue;
	private Function<ConverterContext, SourceRecord> eventConverter;
	
	@Builder.Default
	private boolean isActive = false;
	
	public ParticleEventProvider(ParticleConnectorConfig config) {
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
		this.eventConverter = EventConverterFactory.get(config);
	}

	@Override
	public void start() {
		System.out.println("Start called on ParticleEventProvider");
		this.isActive = true;
		this.eventSource = EventSources.createFactory(this.client).newEventSource(this.request, this.eventListener);
	}

	@Override
	public void stop() {
		this.eventSource.cancel();
		this.isActive = false;
	}

	@Override
	public List<Event> get() {
		this.retryIfNeeded();
		List<Event> events = new ArrayList<>();
		this.sseQueue.drainTo(events);
		return events.stream().collect(Collectors.toList());
	}

	@Override
	public boolean isActive() {
		return this.isActive;
	}

	@Override
	public void setActive(boolean active) {
		this.isActive = active;
	}

	@Override
	public EventProvider retryIfNeeded() {
		if(!this.isActive()) {
			System.out.println("SourceRecordProvider is not active - trying to connect to particle service...");
			int reconnectAttempt=1;
			while(!this.isActive() && reconnectAttempt < this.config.getMaxReconnectAttempts()+1) {
				System.out.println(String.format("Attempting restart of event provider, attempt: %i", reconnectAttempt));
				reconnectAttempt++;
				try {
					System.out.println(String.format("Waiting for %i milliseconds before the next retry...", this.config.getRetryDelay()));
					Thread.sleep(this.config.getRetryDelay());
				} catch (InterruptedException e) {
					throw new ConnectException(e);
				}
				this.start();
				if(!this.isActive()) {
					this.stop();
				}
			}
			if(!this.isActive()) {
				throw new ConnectException("Could not reestablish connection... putting task in failed state!");
			} else {
				System.out.println("Connection successfull");
			}
		}
		return this;
	}

}
