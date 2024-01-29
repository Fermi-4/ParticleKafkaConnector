package com.github.fermi4.particle.sse.providers.all;

import java.util.concurrent.LinkedBlockingDeque;

import io.github.fermi4.particle.v1.ParticleClient;
import com.github.fermi4.particle.sse.Event;
import com.github.fermi4.particle.sse.providers.AbstractQueueingEventProvider;

/**
 * 
 * Target all events
 * 
 * @author Fermi-4
 *
 */
public class FilteredAllEventProvider extends AbstractQueueingEventProvider {

	private final String eventPrefix;

	public FilteredAllEventProvider(String eventPrefix, ParticleClient particleClient) {
		super(particleClient, new LinkedBlockingDeque<Event>());
		this.eventPrefix = eventPrefix;
	}

	@Override
	public void start() {
		this.source = this.particleClient.openEventStream(eventPrefix, this);
	}

}
