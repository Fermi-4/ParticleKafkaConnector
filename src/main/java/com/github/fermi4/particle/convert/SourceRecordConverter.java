package com.github.fermi4.particle.convert;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.Header;
import org.apache.kafka.connect.source.SourceRecord;

import com.github.fermi4.particle.sse.Event;

import lombok.Builder;

@Builder
public class SourceRecordConverter implements Function<Event, SourceRecord> {
	
	@Builder.Default Function<Event, Map<String, ?>> partitionMapExtractor = c -> null;
	@Builder.Default Function<Event, Map<String, ?>> offsetsMapExtractor = c -> null;
	@Builder.Default Function<Event, EventDataExtraction> keyExtractor = c -> new EventDataExtraction(null, null);
	@Builder.Default Function<Event, EventDataExtraction> valueExtractor = c -> new EventDataExtraction(null, null);
	@Builder.Default Function<Event, Integer> partitionExtractor = c -> null;
	@Builder.Default Function<Event, String> topicExtractor = c -> null;
	@Builder.Default Function<Event, Long> timestampExtractor = c -> null;
	@Builder.Default Function<Event, Iterable<Header>> headersExtractor = c -> null;
	
	@Override
	public SourceRecord apply(Event event) {
		
		// null check
		Objects.requireNonNull(partitionMapExtractor);
		Objects.requireNonNull(offsetsMapExtractor);
		Objects.requireNonNull(keyExtractor);
		Objects.requireNonNull(valueExtractor);
		Objects.requireNonNull(partitionExtractor);
		Objects.requireNonNull(topicExtractor);
		Objects.requireNonNull(timestampExtractor);
		Objects.requireNonNull(headersExtractor);
		
		// get the data
		Map<String, ?> partitionMap = partitionMapExtractor.apply(event);
		Map<String, ?> offsetMap = offsetsMapExtractor.apply(event);
		EventDataExtraction keyExtraction = keyExtractor.apply(event);
		EventDataExtraction valueExtraction = valueExtractor.apply(event);
		Integer partition = partitionExtractor.apply(event);
		String topic = topicExtractor.apply(event);
		Long timestamp = timestampExtractor.apply(event);
		Iterable<Header> headers = headersExtractor.apply(event);
		
		// create the record
		return new SourceRecord(
				partitionMap,
				offsetMap, 
				topic, 
				partition, 
				keyExtraction.getSchema().orElse(Schema.OPTIONAL_BYTES_SCHEMA),
				keyExtraction.getData().orElse(null), 
				valueExtraction.getSchema().orElse(Schema.OPTIONAL_BYTES_SCHEMA), 
				valueExtraction.getData().orElse(null), 
				timestamp,
				headers);
	}
	
}
