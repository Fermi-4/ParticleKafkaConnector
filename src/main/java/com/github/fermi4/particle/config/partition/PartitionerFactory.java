package com.github.fermi4.particle.config.partition;

import java.util.List;

import org.apache.kafka.connect.errors.ConnectException;

import com.github.fermi4.particle.api.ParticleClient;
import com.github.fermi4.particle.api.ParticleClientFactory;
import com.github.fermi4.particle.config.ParticleConnectorConfig;

public class PartitionerFactory {
	
	public static ParticleTaskConfigPartitioner get(ParticleConnectorConfig config) {
		if(config.getDiscover() != null) {
			ParticleClient client = ParticleClientFactory.get(config);
			switch(config.getDiscover()) {
			case ParticleConnectorConfig.DISCOVER_MODE_DISCOVER_ALL_DEVICES:
				return PartitionerFactory.getAllDevicesPartitioner(client);
			case ParticleConnectorConfig.DISCOVER_MODE_DISCOVER_ALL_PRODUCTS:
				return PartitionerFactory.getAllProductsPartitioner(client);
			case ParticleConnectorConfig.DISCOVER_MODE_DISCOVER_ALL_DEVICES_IN_PRODUCT:
				return PartitionerFactory.getDevicesInProductPartitioner(client, config.getProductId());
			case ParticleConnectorConfig.DISCOVER_MODE_DISCOVER_ALL_DEVICES_IN_PRODUCT_AND_GROUPS:
				List<String> productGroups = List.of(config.getProductGroups().split(config.getDelimiter()));
				return PartitionerFactory.getDevicesInProductAndGroupsPartitioner(client, config.getProductId(), productGroups);
			default: 
				throw new ConnectException("Discover mode not recognized [" + config.getDiscover() + "] check " + ParticleConnectorConfig.DISCOVER_MODE_CONFIG);
			}
		}
		if(config.getEventMode() != null) {
			switch(config.getEventMode()) {
			case ParticleConnectorConfig.EVENT_MODE_ALL:
				return PartitionerFactory.getDefaultPartitioner();
			case ParticleConnectorConfig.EVENT_MODE_DEVICE:
				return PartitionerFactory.getDeviceIdPartitioner();
			case ParticleConnectorConfig.EVENT_MODE_PRODUCT:
				return PartitionerFactory.getProductIdPartitioner();
			default:
				throw new ConnectException("Event mode not recognized [" + config.getEventMode() + "] check " + ParticleConnectorConfig.EVENT_MODE_CONFIG);
			}
		}
		throw new ConnectException("Error getting partitioner -- need to set either " + ParticleConnectorConfig.EVENT_MODE_CONFIG + " or " + ParticleConnectorConfig.DISCOVER_MODE_CONFIG);
	}
	
	public static ParticleTaskConfigPartitioner getDefaultPartitioner() {
		return new DefaultPartitioner();
	}

	public static ParticleTaskConfigPartitioner getDeviceIdPartitioner() {
		return new DeviceIdPartitioner();
	}

	public static ParticleTaskConfigPartitioner getProductIdPartitioner() {
		return new ProductIdPartitioner();
	}
	
	/**
	 * TODO: The below partitioners are based on discovering devices/product event streams to target by utilizing the particle API
	 * 		 These should be moved out into a separate "discovery" interface instead of `ParticleTaskConfigPartitioner` 
	 * 		 but this works for now by just combining the concepts of discovery and partitioning
	 */
	
	public static ParticleTaskConfigPartitioner getDevicesInProductAndGroupsPartitioner(ParticleClient particleClient, String productIdOrSlug, List<String> productGroups) {
		return new DevicesInGroupsPartitioner(particleClient,productIdOrSlug,productGroups);
	}

	public static ParticleTaskConfigPartitioner getDevicesInProductPartitioner(ParticleClient particleClient, String productIdOrSlug) {
		return new DevicesInProductPartitioner(particleClient, productIdOrSlug);
	}
	
	public static ParticleTaskConfigPartitioner getAllDevicesPartitioner(ParticleClient particleClient) {
		return new AllDevicesPartitioner(particleClient); 
	}
	
	public static ParticleTaskConfigPartitioner getAllProductsPartitioner(ParticleClient particleClient) {
		return new AllProductsPartitioner(particleClient);
	}
}
