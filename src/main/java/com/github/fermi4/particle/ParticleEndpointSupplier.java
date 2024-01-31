package com.github.fermi4.particle;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_ALL;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_DEVICE;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_PRODUCT;

import org.apache.kafka.connect.errors.ConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.fermi4.particle.api.ParticleHttpUrlFactory;
import com.github.fermi4.particle.config.ParticleConnectorConfig;

import okhttp3.HttpUrl;

public class ParticleEndpointSupplier {
	
	static final Logger log = LoggerFactory.getLogger(ParticleEndpointSupplier.class);

	/**
	 * 
	 * This returns the source endpoint based on the passed in ParticleConnectorConfig
	 * object
	 * 
	 * @author Fermi-4
	 * 
	 * @param {@link ParticleConnectorConfig}
	 * @return {@link HttpUrl} 
	 * @throws ConnectException
	 */
	public static HttpUrl get(ParticleConnectorConfig config) throws ConnectException {
		System.out.println(String.format("Checking arg [%s] against key: [%s]", config.getAccessMode(), EVENT_MODE_ALL));
		if(config.getAccessMode() == null) {
			throw new ConnectException(String.format("Event mode cannot be null! Check configuration setting [%s]", EVENT_MODE_CONFIG));
		}
		
	    switch (config.getAccessMode()) {
	        case EVENT_MODE_ALL:
	            return ParticleHttpUrlFactory.getAllEventsFiltered(config.getAccessToken(), config.getEventPrefix());

	        case EVENT_MODE_DEVICE:
	            return handleDeviceMode(config);

	        case EVENT_MODE_PRODUCT:
	            return handleProductMode(config);

	        default:
	            throw new ConnectException(String.format("Endpoint supplier could not be determined! Event mode [%s] not recognized! Check configuration parameter [%s]", config.getAccessMode(), ParticleConnectorConfig.EVENT_MODE_CONFIG));
	    }
	}

	private static HttpUrl handleProductMode(ParticleConnectorConfig config) {
		if (config.getProductId() == null) {
	        throw new ConnectException(String.format("Product Slug/ID cannot be null with event mode [%s] check configuration setting [%s]", EVENT_MODE_PRODUCT, EVENT_MODE_CONFIG));
	    }

	    if (config.getEventPrefix() == null) {
	        return ParticleHttpUrlFactory.getProductEvents(config.getAccessToken(), config.getProductId());
	    } else {
	        return ParticleHttpUrlFactory.getProductEventsFiltered(config.getAccessToken(), config.getProductId(), config.getEventPrefix());
	    }
	}

	private static HttpUrl handleDeviceMode(ParticleConnectorConfig config) {
		if (config.getDeviceId() == null) {
	        if (config.getEventPrefix() == null) {
	            return ParticleHttpUrlFactory.getAllDeviceEvents(config.getAccessToken());
	        } else {
	            return ParticleHttpUrlFactory.getAllDeviceEventsFiltered(config.getAccessToken(), config.getEventPrefix());
	        }
	    } else {
	        if (config.getEventPrefix() == null) {
	            return ParticleHttpUrlFactory.getDeviceEvents(config.getAccessToken(), config.getDeviceId());
	        } else {
	            return ParticleHttpUrlFactory.getDeviceEventsFiltered(config.getAccessToken(), config.getDeviceId(), config.getEventPrefix());
	        }
	    }
	}

}
