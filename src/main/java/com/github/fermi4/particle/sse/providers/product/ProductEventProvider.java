package com.github.fermi4.particle.sse.providers.product;

import java.util.concurrent.LinkedBlockingQueue;

import com.github.fermi4.particle.api.ParticleClient;
import com.github.fermi4.particle.sse.Event;
import com.github.fermi4.particle.sse.providers.AbstractQueueingEventProvider;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductEventProvider extends AbstractQueueingEventProvider {

	private final String productIdOrSlug;

	public ProductEventProvider(String productIdOrSlug, ParticleClient particleClient) {
		super(particleClient, new LinkedBlockingQueue<Event>());
		this.productIdOrSlug = productIdOrSlug;
	}

	@Override
	public void start() {
		this.source = this.particleClient.sseProductEvents(productIdOrSlug, this);
	}


}
