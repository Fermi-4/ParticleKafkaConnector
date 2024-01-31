package com.github.fermi4.particle.config.partition;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_ALL;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_DEVICE;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_PRODUCT;

import org.apache.kafka.connect.errors.ConnectException;

import com.github.fermi4.particle.config.ParticleConnectorConfig;

public class PartitionerFactory {
	
	public static ParticleTaskConfigPartitioner get(ParticleConnectorConfig config) {
		if(config.getEventMode() == null) {
			throw new ConnectException(String.format("Access mode cannot be null! Check configuration setting [%s]", EVENT_MODE_CONFIG));
		}
		if (config.getEventMode().equals(EVENT_MODE_ALL)) {
			System.out.println("Using default partitioner");
			return new DefaultPartitioner();
		} 
		else if (config.getEventMode().equals(EVENT_MODE_DEVICE)) {
			System.out.println("Using device ID partitioner");
			return new DeviceIdPartitioner();
		} else if (config.getEventMode().equals(EVENT_MODE_PRODUCT)) {
			System.out.println("Using product ID partitioner");
			return new ProductIdPartitioner();
		}
		throw new ConnectException(String.format("Event mode not recognized [%s]! Check configuration setting [%s]", config.getEventMode(), EVENT_MODE_CONFIG));
	}
	
}
