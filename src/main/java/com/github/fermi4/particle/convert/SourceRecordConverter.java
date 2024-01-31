package com.github.fermi4.particle.convert;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.Header;
import org.apache.kafka.connect.source.SourceRecord;

import com.github.fermi4.particle.convert.extract.EventDataExtraction;

import lombok.Builder;

@Builder
public class SourceRecordConverter implements Function<ConverterContext, SourceRecord> {
	
	@Builder.Default
	Function<ConverterContext, Map<String, ?>> partitionMapExtractor = c -> null;
	
	@Builder.Default
	Function<ConverterContext, Map<String, ?>> offsetsMapExtractor = c -> null;
	
	@Builder.Default
	Function<ConverterContext, EventDataExtraction> keyExtractor = c -> new EventDataExtraction(null, null);
	
	@Builder.Default
	Function<ConverterContext, EventDataExtraction> valueExtractor = c -> new EventDataExtraction(null, null);
	
	@Builder.Default
	Function<ConverterContext, Integer> partitionExtractor = c -> null;
	
	@Builder.Default
	Function<ConverterContext, String> topicExtractor = c -> null;
	
	@Builder.Default
	Function<ConverterContext, Long> timestampExtractor = c -> null;
	
	@Builder.Default
	Function<ConverterContext, Iterable<Header>> headersExtractor = c -> null;
	
	@Override
	public SourceRecord apply(ConverterContext context) {
		
		Objects.requireNonNull(partitionMapExtractor);
		Objects.requireNonNull(offsetsMapExtractor);
		Objects.requireNonNull(keyExtractor);
		Objects.requireNonNull(valueExtractor);
		Objects.requireNonNull(partitionExtractor);
		Objects.requireNonNull(topicExtractor);
		Objects.requireNonNull(timestampExtractor);
		Objects.requireNonNull(headersExtractor);

		Map<String, ?> partitionMap = partitionMapExtractor.apply(context);
		Map<String, ?> offsetMap = offsetsMapExtractor.apply(context);
		EventDataExtraction keyExtraction = keyExtractor.apply(context);
		EventDataExtraction valueExtraction = valueExtractor.apply(context);
		Integer partition = partitionExtractor.apply(context);
		String topic = topicExtractor.apply(context);
		Long timestamp = timestampExtractor.apply(context);
		Iterable<Header> headers = headersExtractor.apply(context);
		
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
