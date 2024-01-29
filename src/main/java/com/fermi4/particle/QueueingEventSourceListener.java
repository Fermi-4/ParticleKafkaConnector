package com.fermi4.particle;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fermi4.particle.sse.SSEEvent;

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
	private BlockingQueue<SSEEvent> sharedQueue;
	
	public QueueingEventSourceListener(BlockingQueue<SSEEvent> sharedQueue) {
		super();
		this.sharedQueue = sharedQueue;
	}

	@Override
	public void onClosed(EventSource eventSource) {
		log.info("Closing EventSourceListener");
		super.onClosed(eventSource);
	}

	@Override
	public void onEvent(EventSource eventSource, String id, String type, String data) {
		log.info("Received event from request source: [{}], [{}, {}, {}]", eventSource.request(), id, type, data);
		/**
		 * TODO: Look to offer
		 */
		this.sharedQueue.add(new SSEEvent(id, type, data));
	}

	@Override
	public void onFailure(EventSource eventSource, Throwable t, Response response) {
		log.error("Error in EventSourceListener [{}]", t);
		super.onFailure(eventSource, t, response);
	}

	@Override
	public void onOpen(EventSource eventSource, Response response) {
		log.info("EventSource listener onOpen called with request: [{}] response: [{}]", eventSource.request(), response);
		super.onOpen(eventSource, response);
	}

}
