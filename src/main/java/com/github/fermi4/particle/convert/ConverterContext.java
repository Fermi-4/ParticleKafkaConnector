package com.github.fermi4.particle.convert;

import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.sse.Event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConverterContext {
	private ParticleConnectorConfig config;
	private Event event;
}
