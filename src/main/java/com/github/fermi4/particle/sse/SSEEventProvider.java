package com.github.fermi4.particle.sse;

import java.util.List;

public interface SSEEventProvider {
	public void start();
	public void stop();
	public List<SSEEvent> get();
	public void setActive(boolean active);
	public boolean isActive();
}
