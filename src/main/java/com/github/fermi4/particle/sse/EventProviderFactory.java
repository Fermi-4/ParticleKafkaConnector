package com.github.fermi4.particle.sse;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.config.partition.PartitionUtil;
import com.github.fermi4.particle.sse.provider.CompositeEventProvider;
import com.github.fermi4.particle.sse.provider.CompositeEventProvider.CompositeEventProviderBuilder;
import com.github.fermi4.particle.sse.provider.ParticleEventProvider;

public class EventProviderFactory {

	public static EventProvider get(ParticleConnectorConfig config) {
		switch (config.getEventMode()) {
		case ParticleConnectorConfig.EVENT_MODE_ALL:
			return new ParticleEventProvider(config);
		case ParticleConnectorConfig.EVENT_MODE_DEVICE:
			return handleDevice(config);
		case ParticleConnectorConfig.EVENT_MODE_PRODUCT:
			return handleProduct(config);
		default:
			return new ParticleEventProvider(config);
		}
	}

	private static EventProvider handleProduct(ParticleConnectorConfig config) {
		return createCompositeEventProvider(config, ParticleConnectorConfig.PRODUCT_ID_CONFIG);
	}

	private static EventProvider handleDevice(ParticleConnectorConfig config) {
		return createCompositeEventProvider(config, ParticleConnectorConfig.DEVICE_ID_CONFIG);
	}

	private static EventProvider createCompositeEventProvider(ParticleConnectorConfig config, String field) {
		CompositeEventProviderBuilder builder = CompositeEventProvider.builder();
		PartitionUtil.partitionConfigOnFieldDelimiter(config, field, ParticleConnectorConfig.DELIMITER)
				.stream()
				.map(ParticleEventProvider::new)
				.forEach(builder::addProvider);
		
		return builder.build();
	}

	private static EventProvider bootstrapFromConfig(ParticleConnectorConfig config) {
		EventProvider fakeSSEProvider = ParticleEventProvider.builder()
                                .client(new OkHttpClient.Builder().build())
                                .config(config)
                                .eventConverter(EventConverterFactory.get(config))
                                .eventListener(latched(new QueueingEventSourceListener(queue), onEventLatch))
                                .sseQueue(queue)
                                .request(new Request.Builder().url(mockWebServer.url("/sse")).build())
                                .build();
		this.config = config;
                this.client = new OkHttpClient.Builder()
                                // TODO: provide connect timeout option in config - default to never timeout
                                .connectTimeout(0, TimeUnit.MILLISECONDS)
                                .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                                .build();
                this.sseQueue = new LinkedBlockingQueue<>();

		this.eventListener = onFailureNotify(new QueueingEventSourceListener(sseQueue), Arrays.asList(deactivateOnFailure(this)));
                this.request = new Request.Builder()
                                .addHeader("Content-Type", "text/event-stream")
                                .addHeader("Connection", "keep-alive")
                                .addHeader("Cache-Control", "no-cache")
                                .url(ParticleEndpointSupplier.get(this.config))
                                .build();
                this.isActive = false;
                this.eventConverter = EventConverterFactory.get(config);

	}

}
