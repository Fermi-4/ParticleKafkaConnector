package com.github.fermi4.particle.config;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

public class ConfigTest {
	
	@Test
	public void test() {		
		System.out.println(ParticleConnectorConfig.conf().toHtmlTable(new HashMap<>()));
	}
	
}
