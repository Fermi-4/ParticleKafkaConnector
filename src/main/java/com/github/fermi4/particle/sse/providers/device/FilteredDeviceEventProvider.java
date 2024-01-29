package com.github.fermi4.particle.sse.providers.device;

import java.util.concurrent.LinkedBlockingQueue;

import io.github.fermi4.particle.v1.ParticleClient;
import com.github.fermi4.particle.sse.Event;
import com.github.fermi4.particle.sse.providers.AbstractQueueingEventProvider;

public class FilteredDeviceEventProvider extends AbstractQueueingEventProvider {

	private final String eventPrefix;
	private final String deviceId;

	public FilteredDeviceEventProvider(String deviceId, String eventPrefix, ParticleClient particleClient) {
		super(particleClient, new LinkedBlockingQueue<Event>());
		this.eventPrefix = eventPrefix;
		this.deviceId = deviceId;
	}

	@Override
	public void start() {
		this.source = this.particleClient.openDeviceEventStream(deviceId, eventPrefix, this);
	}

}
