package com.github.fermi4.particle.sse;

import java.util.List;

import org.apache.kafka.connect.errors.ConnectException;

import io.github.fermi4.particle.v1.ParticleClient;
import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.sse.providers.CompositeEventProvider;
import com.github.fermi4.particle.sse.providers.CompositeEventProvider.CompositeEventProviderBuilder;
import com.github.fermi4.particle.sse.providers.device.AllDevicesEventProvider;
import com.github.fermi4.particle.sse.providers.device.DeviceEventProvider;
import com.github.fermi4.particle.sse.providers.device.FilteredDeviceEventProvider;
import com.github.fermi4.particle.sse.providers.product.FilteredProductEventProvider;
import com.github.fermi4.particle.sse.providers.product.ProductEventProvider;

public class EventProviderFactory {

	public static EventProvider get(ParticleConnectorConfig config, ParticleClient client) {
		switch (config.getEventMode()) {
			case ParticleConnectorConfig.EVENT_MODE_ALL:
				return new AllDevicesEventProvider(client);
			case ParticleConnectorConfig.EVENT_MODE_DEVICE:
				CompositeEventProviderBuilder deviceEventProviderBuilder = CompositeEventProvider
						.builder();

				List.of(config.getDeviceId().split(config.getDelimiter()))
						.stream()
						.map(deviceId -> getDeviceEventProvider(deviceId, config, client))
						.forEach(deviceEventProviderBuilder::addProvider);

				return deviceEventProviderBuilder.build();
			case ParticleConnectorConfig.EVENT_MODE_PRODUCT:
				CompositeEventProviderBuilder productEventProviderBuilder = CompositeEventProvider
						.builder();

				List.of(config.getProductId().split(config.getDelimiter()))
						.stream()
						.map(productIdOrSlug -> getProductEventProvider(productIdOrSlug, config,
								client))
						.forEach(productEventProviderBuilder::addProvider);

				return productEventProviderBuilder.build();
			default:
				throw new ConnectException("Error getting event provider event mode ["
						+ config.getEventMode() + "] not recognized");
		}
	}

	private static EventProvider getProductEventProvider(String productIdOrSlug, ParticleConnectorConfig config,
			ParticleClient client) {
		if (config.getEventPrefix() != null && !config.getEventPrefix().isEmpty()) {
			System.out.println(
					"getProductEventProvider::EVENT_MODE_PRODUCT creating FilteredProductEventProvider "
							+ config.getEventPrefix());
			return new FilteredProductEventProvider(productIdOrSlug, config.getEventPrefix(), client);
		}
		System.out.println("EventProviderFactory::EVENT_MODE_PRODUCT creating ProductEventProvider: "
				+ productIdOrSlug);
		return new ProductEventProvider(productIdOrSlug, client);
	}

	private static EventProvider getDeviceEventProvider(String deviceId, ParticleConnectorConfig config,
			ParticleClient client) {
		if (config.getEventPrefix() != null && !config.getEventPrefix().isEmpty()) {
			System.out.println(
					"EventProviderFactory::EVENT_MODE_DEVICE creating FilteredDeviceEventProvider "
							+ config.getEventPrefix());
			return new FilteredDeviceEventProvider(deviceId, config.getEventPrefix(), client);
		}
		System.out.println("EventProviderFactory::EVENT_MODE_DEVICE creating DeviceEventProvider: " + deviceId);
		return new DeviceEventProvider(deviceId, client);
	}

}
