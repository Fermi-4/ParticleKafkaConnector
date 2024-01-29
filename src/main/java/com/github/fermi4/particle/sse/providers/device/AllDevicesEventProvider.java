package com.github.fermi4.particle.sse.providers.device;

import java.util.concurrent.LinkedBlockingQueue;

import io.github.fermi4.particle.v1.ParticleClient;
import com.github.fermi4.particle.sse.Event;
import com.github.fermi4.particle.sse.providers.AbstractQueueingEventProvider;

public class AllDevicesEventProvider extends AbstractQueueingEventProvider {

	public AllDevicesEventProvider(ParticleClient particleClient) {
		super(particleClient, new LinkedBlockingQueue<Event>());
	}

	@Override
	public void start() {
		this.source = this.particleClient.openAllDeviceEventStream(this);
	}
}
