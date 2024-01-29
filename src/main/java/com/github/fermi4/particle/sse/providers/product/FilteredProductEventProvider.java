package com.github.fermi4.particle.sse.providers.product;

import java.util.concurrent.LinkedBlockingQueue;

import io.github.fermi4.particle.v1.ParticleClient;
import com.github.fermi4.particle.sse.Event;
import com.github.fermi4.particle.sse.providers.AbstractQueueingEventProvider;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilteredProductEventProvider extends AbstractQueueingEventProvider {

	private final String productIdOrSlug;
	private final String eventPrefix;

	public FilteredProductEventProvider(String productIdOrSlug, String eventPrefix, ParticleClient particleClient) {
		super(particleClient, new LinkedBlockingQueue<Event>());
		this.productIdOrSlug = productIdOrSlug;
		this.eventPrefix = eventPrefix;
	}

	@Override
	public void start() {
		this.source = this.particleClient.openProductEventStream(productIdOrSlug, eventPrefix, this);
	}

}
