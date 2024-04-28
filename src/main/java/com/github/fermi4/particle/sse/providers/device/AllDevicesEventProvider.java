package com.github.fermi4.particle.sse.providers.device;

import java.util.concurrent.LinkedBlockingQueue;

import com.github.fermi4.particle.api.ParticleClient;
import com.github.fermi4.particle.sse.Event;
import com.github.fermi4.particle.sse.providers.AbstractQueueingEventProvider;

public class AllDevicesEventProvider extends AbstractQueueingEventProvider {
	
	public AllDevicesEventProvider(ParticleClient particleClient) {
		super(particleClient, new LinkedBlockingQueue<Event>());
	}

	@Override
	public void start() {
		this.source = this.particleClient.sseAllDeviceEvents(this);
	}
}
