package com.github.fermi4.particle.sse;

import java.util.List;

public interface EventProvider {
	public void start();
	public void stop();
	public List<Event> get();
}
