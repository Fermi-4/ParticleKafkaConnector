package com.github.fermi4.particle.sse.providers.device;

import java.util.concurrent.LinkedBlockingQueue;

import com.github.fermi4.particle.sse.Event;
import com.github.fermi4.particle.sse.providers.AbstractQueueingEventProvider;

import io.github.fermi4.particle.v1.ParticleClient;

public class FilteredAllDevicesEventProvider extends AbstractQueueingEventProvider {

	private final String eventPrefix;

	public FilteredAllDevicesEventProvider(ParticleClient client, String eventPrefix) {
		super(client, new LinkedBlockingQueue<Event>());
		this.eventPrefix = eventPrefix;
	}

	@Override
	public void start() {
		this.source = this.particleClient.openAllDeviceEventStream(eventPrefix, this);
	}

}
