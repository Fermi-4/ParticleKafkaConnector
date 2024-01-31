package com.github.fermi4.particle.config.partition;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_ALL;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_DEVICE;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_PRODUCT;

import org.apache.kafka.connect.errors.ConnectException;

import com.github.fermi4.particle.config.ParticleConnectorConfig;

public class PartitionerFactory {
	
	public static ParticleTaskConfigPartitioner get(ParticleConnectorConfig config) {
		if(config.getAccessMode() == null) {
			throw new ConnectException(String.format("Access mode cannot be null! Check configuration setting [%s]", EVENT_MODE_CONFIG));
		}
		if (config.getAccessMode().equals(EVENT_MODE_ALL)) {
			return new DefaultPartitioner();
		} 
		else if (config.getAccessMode().equals(EVENT_MODE_DEVICE)) {
			return new DeviceIdPartitioner();
		} else if (config.getAccessMode().equals(EVENT_MODE_PRODUCT)) {
			return new ProductIdPartitioner();
		}
		return null;
	}
	
}
