package com.github.fermi4.particle.sse.listener.deco;

import java.util.List;

import com.github.fermi4.particle.sse.listener.EventListenerObserver;

import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

public class OnFailureNotifyDecorator extends AbstractEventSourceListenerDecorator {
	
	private List<EventListenerObserver> observers;

    public OnFailureNotifyDecorator(EventSourceListener delegate, List<EventListenerObserver> observers) {
        super(delegate);
        this.observers = observers;
    }

    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        this.observers.stream().forEach(o->o.notifyFailure(eventSource, t, response));
        this.delegate.onFailure(eventSource, t, response);
    }

	/**
     * This brings in the failure notification logic using composition i.e. without needing 
     * to update the base queue class
     * 
     * @param delegate
     * @param provider
     * @return {@link EventSourceListener}
     */
    public static EventSourceListener onFailureNotify(EventSourceListener delegate, List<EventListenerObserver> observers) {
        return new OnFailureNotifyDecorator(delegate, observers);
    }
    
}
