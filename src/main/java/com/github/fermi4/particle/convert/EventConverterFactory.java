package com.github.fermi4.particle.convert;

import java.util.function.Function;

import org.apache.kafka.connect.source.SourceRecord;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.sse.Event;

public class EventConverterFactory {
	
	/**
	 * 
	 * For a given configuration, return the correct converter which is responsible for 
	 * converting {@link Event} into Kafka {@link SourceRecord}
	 * 
	 */
    public static Function<ConverterContext, SourceRecord> get(ParticleConnectorConfig config) {
        if (config.getEventMode() == ParticleConnectorConfig.EVENT_MODE_PRODUCT && config.getProductId() != null) {
            return product(config);
        } else if (config.getEventMode() == ParticleConnectorConfig.EVENT_MODE_DEVICE && config.getDeviceId() != null) {
            return device(config);
        } else {
            return eventType(config);
        }
    }
    
    /**
     * 
     * These static methods are all returning same impl - but will 
     * leave for the moment in case we need to implement specific event mode
     * handling again 
     */    
    private static Function<ConverterContext, SourceRecord> eventType(ParticleConnectorConfig config) {
        return createConverter(config);
    }

    private static Function<ConverterContext, SourceRecord> product(ParticleConnectorConfig config) {
        return createConverter(config);
    }

    private static Function<ConverterContext, SourceRecord> device(ParticleConnectorConfig config) {
        return createConverter(config);
    }

    private static Function<ConverterContext, SourceRecord> createConverter(ParticleConnectorConfig config) {
    	// TODO: create factor for this extractor based on the configuration
    	return SourceRecordConverter.builder().build();
    }
}
