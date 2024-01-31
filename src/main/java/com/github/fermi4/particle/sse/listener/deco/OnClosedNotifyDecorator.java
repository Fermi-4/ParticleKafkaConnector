package com.github.fermi4.particle.sse.listener.deco;

import java.util.List;

import com.github.fermi4.particle.sse.listener.EventListenerObserver;

import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

public class OnClosedNotifyDecorator extends AbstractEventSourceListenerDecorator {
	
	private List<EventListenerObserver> observers;

	public OnClosedNotifyDecorator(EventSourceListener delegate, List<EventListenerObserver> observers) {
		super(delegate);
		this.observers = observers;
	}
	
	@Override
	public void onClosed(EventSource eventSource) {
    	this.observers.stream().forEach(o->o.notifyClosed(eventSource));
        this.delegate.onClosed(eventSource);
	}
	
	public static EventSourceListener onClosedNotify(EventSourceListener delegate, List<EventListenerObserver> observers) {
		return new OnClosedNotifyDecorator(delegate, observers);
	}
	
	
}
