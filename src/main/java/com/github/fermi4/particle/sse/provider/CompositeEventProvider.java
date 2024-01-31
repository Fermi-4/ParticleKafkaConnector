package com.github.fermi4.particle.sse.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.fermi4.particle.sse.Event;
import com.github.fermi4.particle.sse.EventProvider;

public class CompositeEventProvider implements EventProvider {
	
	private List<EventProvider> providers;

	private CompositeEventProvider(List<EventProvider> providers) {
		super();
		this.providers = providers;
	}

	@Override
	public void start() {
		providers.forEach(p -> p.start());
	}

	@Override
	public void stop() {
		providers.forEach(p -> p.stop());
	}

	@Override
	public List<Event> get() {
		return providers.stream().flatMap(provider -> provider.get().stream()).collect(Collectors.toList());
	}

	@Override
	public void setActive(boolean active) {
		providers.forEach(p -> p.setActive(active));
	}

	@Override
	public boolean isActive() {
		return providers.stream().allMatch(p->p.isActive());
	}
	
	public static CompositeEventProviderBuilder builder() {
		return new CompositeEventProviderBuilder();
	}
	
	public static class CompositeEventProviderBuilder {
		private List<EventProvider> providers;

		public CompositeEventProviderBuilder() {
			super();
			this.providers = new ArrayList<>();
		}

		public CompositeEventProviderBuilder addProvider(EventProvider provider) {
			this.providers.add(provider);
			return this;
		}
		
		public CompositeEventProviderBuilder addProvider(EventProvider... provider) {
			for (int i = 0; i < provider.length; i++) {
				this.providers.add(provider[i]);				
			}
			return this;
		}
		
		public EventProvider build() {
			return new CompositeEventProvider(this.providers);
		}
	}

	@Override
	public EventProvider retryIfNeeded() {
		this.providers.stream().forEach(p->p.retryIfNeeded());
		return this;
	}
}
