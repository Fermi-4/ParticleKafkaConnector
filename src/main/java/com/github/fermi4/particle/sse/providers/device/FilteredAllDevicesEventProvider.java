package com.github.fermi4.particle.sse.providers.device;

import com.github.fermi4.particle.sse.providers.AbstractQueueingEventProvider;

public class FilteredAllDevicesEventProvider extends AbstractQueueingEventProvider {
	
	private final String eventPrefix;
	
	public FilteredAllDevicesEventProvider(String accessToken, String eventPrefix) {
		this.eventPrefix = eventPrefix;
	}

	@Override
	public void start() {
		this.source = this.particleClient.sseAllDeviceEventsFiltered(eventPrefix, this);
	}
	
	

}
