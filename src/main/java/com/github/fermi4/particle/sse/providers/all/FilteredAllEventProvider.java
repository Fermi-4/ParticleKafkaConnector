package com.github.fermi4.particle.sse.providers.all;

import java.util.concurrent.LinkedBlockingDeque;

import com.github.fermi4.particle.api.ParticleClient;
import com.github.fermi4.particle.sse.Event;
import com.github.fermi4.particle.sse.providers.AbstractQueueingEventProvider;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * Target all events
 * 
 * @author Fermi-4
 *
 */
@Getter
@Setter
public class FilteredAllEventProvider extends AbstractQueueingEventProvider {
	
	private final String eventPrefix;
	
	public FilteredAllEventProvider(String eventPrefix, ParticleClient particleClient) {
		super(particleClient, new LinkedBlockingDeque<Event>());
		this.eventPrefix = eventPrefix;
	}
	
	@Override
	public void start() {
		this.source = this.particleClient.sseAllEventsFiltered(eventPrefix, this);
	}

}