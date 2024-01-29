package com.fermi4.particle;

import static com.fermi4.particle.ParticleConnectorConfig.ACCESS_MODE_ALL;
import static com.fermi4.particle.ParticleConnectorConfig.ACCESS_MODE_CONFIG;
import static com.fermi4.particle.ParticleConnectorConfig.ACCESS_MODE_DEVICE;
import static com.fermi4.particle.ParticleConnectorConfig.ACCESS_MODE_PRODUCT;

import org.apache.kafka.connect.errors.ConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fermi4.particle.api.ParticleHttpUrlFactory;

import okhttp3.HttpUrl;

public class ParticleEndpointSupplier {
	
	static final Logger log = LoggerFactory.getLogger(ParticleEndpointSupplier.class);
	
	// TODO: add docs, logging
	public static HttpUrl get(ParticleConnectorConfig config) throws ConnectException {
		if (config.getAccessMode() == ACCESS_MODE_ALL) {
			return ParticleHttpUrlFactory.getAllEventsFiltered(config.getAccessToken(), config.getEventPrefix());
		} else if (config.getAccessMode() == ACCESS_MODE_DEVICE) {
			/* Device ID not specified, returns events for all devices */
			if(config.getDeviceId() == null) {
				/* Apply event prefix filter or not */
				if(config.getEventPrefix() == null) {
					/* Get unfiltered device events for all devices */
					return ParticleHttpUrlFactory.getAllDeviceEvents(config.getAccessToken());
				} else {
					/* Get device events filtered by event prefix */
					return ParticleHttpUrlFactory.getAllDeviceEventsFiltered(config.getAccessToken(), config.getEventPrefix());
				}
			/* Device ID is specified, returns events for specific device */
			} else {
				/* Apply event prefix filter or not */
				if(config.getEventPrefix() == null) {
					/* Get device events matching device ID */
					return ParticleHttpUrlFactory.getDeviceEvents(config.getAccessToken(), config.getDeviceId());
				} else {
					/* Get filtered device events matching device ID and event prefix */
					return ParticleHttpUrlFactory.getDeviceEventsFiltered(config.getAccessToken(), config.getDeviceId(),
							config.getEventPrefix());
				}
			}
		} else if (config.getAccessMode() == ACCESS_MODE_PRODUCT) {
			/* Product ID must be specified */
			if(config.getProductId() == null) {
				throw new ConnectException(String.format("Product Slug/ID cannot be null with access_mode [%s] check configuration setting [%s]", ACCESS_MODE_PRODUCT, ACCESS_MODE_CONFIG));
			}
			if(config.getEventPrefix() == null) {
				return ParticleHttpUrlFactory.getProductEvents(config.getAccessToken(), config.getProductId());
			} else {
				return ParticleHttpUrlFactory.getProductEventsFiltered(config.getAccessToken(), config.getProductId(), config.getEventPrefix());
			}
		}
		
		throw new ConnectException(String.format("Endpoint supplier could not be determined! Access mode [%s] not recognized! Check configuration parameter [%s]", config.getAccessMode(), ParticleConnectorConfig.ACCESS_MODE_CONFIG));
	}

}
