package com.github.fermi4.particle.task;

import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.*;

import com.github.fermi4.particle.config.ParticleConnectorConfig;

public final class ParticleConnectorTask {
	
	/**
	 * 
	 * Returns the appropriate task for given user configuration
	 * 
	 * @param config
	 * @return Class<? extends Task>
	 * @throws ConnectException 
	 */
	public static Class<? extends Task> get(ParticleConnectorConfig config) {
		if(config.getConnectorMode() == PARTICLE_CONNECTOR_MODE_SSE) {
			return sseTask();			
		}
		throw new ConnectException(String.format("Connector mode not recognized: [%s]... check configuration for [%s]", config.getEventMode(), PARTICLE_CONNECTOR_MODE_CONFIG));
	}
	
	private static Class<? extends Task> sseTask() {
		return ParticleServerSentEventSourceTask.class;
	}
	
}
