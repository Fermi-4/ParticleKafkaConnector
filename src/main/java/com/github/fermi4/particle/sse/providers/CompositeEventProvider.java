package com.github.fermi4.particle.sse.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.fermi4.particle.sse.Event;
import com.github.fermi4.particle.sse.EventProvider;

/**
 * A single task can handle multiple streams at once
 * <br></br>
 * This allows composing of event providers - one for each target stream
 * 
 * @author Fermi-4
 */
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

}
