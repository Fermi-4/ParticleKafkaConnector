package com.fermi4.particle.sse;

import java.util.List;
import java.util.function.Supplier;
// TODO: remove the supplier extension... hmmm
public interface SSEEventProvider extends Supplier<List<SSEEvent>>{
	public void start();
	public void stop();
	public void setActive(boolean active);
	public boolean isActive();
}
