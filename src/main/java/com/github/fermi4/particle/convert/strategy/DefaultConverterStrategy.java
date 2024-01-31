package com.github.fermi4.particle.convert.strategy;

import org.apache.kafka.connect.source.SourceRecord;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.sse.SSEEvent;

// TODO docs
// TODO might need to explicitly convert to json here
public class DefaultConverterStrategy implements SSEEventSourceRecordConverterStrategy {

	private ParticleConnectorConfig config;

	public DefaultConverterStrategy(ParticleConnectorConfig config) {
		super();
		this.config = config;
	}

	/**
	 * Sets the key to the event type so events of same type will go to same
	 * partition for unfiltered case
	 */
	@Override
	public SourceRecord convert(SSEEvent t) {
		return new SourceRecord(
				null, 					      /* no source partition */
				null, 					      /* no source offset */
				this.config.getTopic(),       /* topic to publish */
				SSEEvent.PARTICLE_KEY_SCHEMA, /* key schema - just a string */
				t.getType().getBytes(),			      /* key by event type */
				SSEEvent.PARTICLE_EVENT_SCHEMA, /* event schema */
				t.getData().getBytes());			      /* event converted to struct */
	}

}
