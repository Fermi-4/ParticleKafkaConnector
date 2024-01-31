package com.fermi4.particle.sse.listener;

import java.util.List;

import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

/**
 * Right now I am only interested in the failure event so I can implement retry logic
 * 
 * This pattern of course could be used for every event type
 */
public class OnFailureNotifyDecorator extends AbstractEventSourceListenerDecorator {
	
	private List<OnFailureObserver> observers;

    public OnFailureNotifyDecorator(EventSourceListener delegate, List<OnFailureObserver> observers) {
        super(delegate);
        this.observers = observers;
    }

    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        this.observers.stream().forEach(o->o.notifyFailure(eventSource, t, response));
        super.onFailure(eventSource, t, response);
    }

    /**
     * This brings in the failure notification logic using composition i.e. without needing 
     * to update the base queue class
     * 
     * @param delegate
     * @param provider
     * @return {@link EventSourceListener}
     */
    public static EventSourceListener onFailureNotify(EventSourceListener delegate, List<OnFailureObserver> observers) {
        return new OnFailureNotifyDecorator(delegate, observers);
    }
    
}
