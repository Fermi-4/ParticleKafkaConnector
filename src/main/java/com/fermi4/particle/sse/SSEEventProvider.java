package com.fermi4.particle.sse;

import java.util.List;
import java.util.function.Supplier;

public interface SSEEventProvider extends Supplier<List<SSEEvent>>{
	public void start();
	public void stop();
}
