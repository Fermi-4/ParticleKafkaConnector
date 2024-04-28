package com.github.fermi4.particle.sse.providers.device;

import java.util.concurrent.LinkedBlockingQueue;

import com.github.fermi4.particle.api.ParticleClient;
import com.github.fermi4.particle.sse.Event;
import com.github.fermi4.particle.sse.providers.AbstractQueueingEventProvider;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceEventProvider extends AbstractQueueingEventProvider {

	private final String deviceId;

	public DeviceEventProvider(String deviceId, ParticleClient particleClient) {
		super(particleClient, new LinkedBlockingQueue<Event>());
		this.deviceId = deviceId;
	}

	@Override
	public void start() {
		this.source = this.particleClient.sseDeviceEvents(deviceId, this);
	}

}
