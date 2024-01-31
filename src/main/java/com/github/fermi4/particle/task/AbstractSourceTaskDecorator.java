package com.github.fermi4.particle.task;

import java.util.List;
import java.util.Map;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

public abstract class AbstractSourceTaskDecorator extends SourceTask {
	
	private SourceTask delegate;

	public AbstractSourceTaskDecorator(SourceTask delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public String version() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start(Map<String, String> props) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<SourceRecord> poll() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
