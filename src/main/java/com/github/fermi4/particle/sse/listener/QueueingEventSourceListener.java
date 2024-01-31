package com.github.fermi4.particle.sse.listener;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.sse.Event;

import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
/**
 * 
 * The purpose of this class is to receive SSE and insert them into the shared queue
 * 
 * @author Fermi-4
 *
 */
public class QueueingEventSourceListener extends EventSourceListener {

	static final Logger log = LoggerFactory.getLogger(QueueingEventSourceListener.class);
	private BlockingQueue<Event> sharedQueue;
	
	public QueueingEventSourceListener(BlockingQueue<Event> sharedQueue) {
		super();
		this.sharedQueue = sharedQueue;
	}

	@Override
	public void onClosed(EventSource eventSource) {
		System.out.println("Closing EventSourceListener");
		super.onClosed(eventSource);
	}

	@Override
	public void onEvent(EventSource eventSource, String id, String type, String data) {
		System.out.println(String.format("Received event from request source: [%s], [%s, %s, %s]", eventSource.request(), id, type, data));
		/**
		 * TODO: Look to offer
		 */
		this.sharedQueue.add(new Event(id, type, data));
	}

	@Override
	public void onFailure(EventSource eventSource, Throwable t, Response response) {
		if(response != null && response.code() != 200) {
			log.error("Error in EventSourceListener [{}]", response.toString());
			if(response.code() == 401) {
				log.error("Got 401 error.. Is your access token correct? Check configuration parameter [{}]", ParticleConnectorConfig.ACCESS_TOKEN_CONFIG);
			}
		}
		if(t!=null) {
			log.error("Error caught with message [{}]", t.getMessage());			
		}
		super.onFailure(eventSource, t, response);
	}

	@Override
	public void onOpen(EventSource eventSource, Response response) {
		System.out.println(String.format("EventSource listener onOpen called with request: [%s] response: [%s]", eventSource.request().toString(), response.toString()));
		super.onOpen(eventSource, response);
	}

}
