package com.github.fermi4.particle.convert.strategy;

import org.apache.kafka.connect.source.SourceRecord;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.sse.SSEEvent;

public class DeviceSourceRecordStrategy implements SSEEventSourceRecordConverterStrategy {

	private ParticleConnectorConfig config;

	public DeviceSourceRecordStrategy(ParticleConnectorConfig config) {
		this.config = config;
		
	}

	/**
	 * All events from the same device should go to the same partition
	 * 
	 * Keys the record using passed in deviceId
	 */
	@Override
	public SourceRecord convert(SSEEvent t) {
			return new SourceRecord(
					null, 
					null, 
					this.config.getTopic(), 
					SSEEvent.PARTICLE_KEY_SCHEMA, 
					this.config.getDeviceId().getBytes(), /* key */
					SSEEvent.PARTICLE_EVENT_SCHEMA, 
					t.getData().getBytes());			 /* value */
	}

}
