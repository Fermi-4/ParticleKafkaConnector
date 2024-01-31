package com.github.fermi4.particle.convert;

import org.apache.kafka.connect.source.SourceRecord;
/**
 * 
 * Convert type <T> to a SourceRecord
 * 
 * <br><br>
 * @author Fermi-4
 *
 * @param <T>
 */
public interface SourceRecordConverter<T> {
	public SourceRecord convert(T t);
}
