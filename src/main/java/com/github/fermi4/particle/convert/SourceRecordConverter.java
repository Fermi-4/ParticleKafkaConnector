package com.github.fermi4.particle.convert;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.Header;
import org.apache.kafka.connect.source.SourceRecord;

import com.github.fermi4.particle.sse.Event;

public class SourceRecordConverter implements Function<Event, SourceRecord> {

	Function<Event, Map<String, ?>> partitionMapExtractor = c -> null;
	Function<Event, Map<String, ?>> offsetsMapExtractor = c -> null;
	Function<Event, EventDataExtraction> keyExtractor = c -> new EventDataExtraction(null, null);
	Function<Event, EventDataExtraction> valueExtractor = c -> new EventDataExtraction(null, null);
	Function<Event, Integer> partitionExtractor = c -> null;
	Function<Event, String> topicExtractor = c -> null;
	Function<Event, Long> timestampExtractor = c -> null;
	Function<Event, Iterable<Header>> headersExtractor = c -> null;

	private SourceRecordConverter(SourceRecordConverterBuilder builder) {
		this.keyExtractor = builder.keyExtractor;
		this.valueExtractor = builder.valueExtractor;
		this.partitionExtractor = builder.partitionExtractor;
		this.partitionMapExtractor = builder.partitionMapExtractor;
		this.topicExtractor = builder.topicExtractor;
		this.timestampExtractor = builder.timestampExtractor;
		this.headersExtractor = builder.headersExtractor;
		this.offsetsMapExtractor = builder.offsetsMapExtractor;
	}

	public static class SourceRecordConverterBuilder {
		Function<Event, Map<String, ?>> partitionMapExtractor = c -> null;
		Function<Event, Map<String, ?>> offsetsMapExtractor = c -> null;
		Function<Event, EventDataExtraction> keyExtractor = c -> new EventDataExtraction(null, null);
		Function<Event, EventDataExtraction> valueExtractor = c -> new EventDataExtraction(null, null);
		Function<Event, Integer> partitionExtractor = c -> null;
		Function<Event, String> topicExtractor = c -> null;
		Function<Event, Long> timestampExtractor = c -> null;
		Function<Event, Iterable<Header>> headersExtractor = c -> null;

		public SourceRecordConverterBuilder() {

		}

		public SourceRecordConverterBuilder keyExtractor(Function<Event, EventDataExtraction> keyExtractor) {
			this.keyExtractor = keyExtractor;
			return this;
		}

		public SourceRecordConverterBuilder valueExtractor(
				Function<Event, EventDataExtraction> valueExtractor) {
			this.valueExtractor = valueExtractor;
			return this;
		}

		public SourceRecordConverterBuilder partitionMapExtractor(
				Function<Event, Map<String, ?>> partitionMapExtractor) {
			this.partitionMapExtractor = partitionMapExtractor;
			return this;
		}

		public SourceRecordConverterBuilder offsetsMapExtractor(
				Function<Event, Map<String, ?>> offsetsMapExtractor) {
			this.offsetsMapExtractor = offsetsMapExtractor;
			return this;
		}

		public SourceRecordConverterBuilder partitionExtractor(Function<Event, Integer> partitionFunction) {
			this.partitionExtractor = partitionFunction;
			return this;
		}

		public SourceRecordConverterBuilder topicExtractor(Function<Event, String> topicExtractor) {
			this.topicExtractor = topicExtractor;
			return this;
		}

		public SourceRecordConverterBuilder timestampExtractor(Function<Event, Long> timestampExtractor) {
			this.timestampExtractor = timestampExtractor;
			return this;
		}

		public SourceRecordConverterBuilder headersExtractor(
				Function<Event, Iterable<Header>> headersExtractor) {
			this.headersExtractor = headersExtractor;
			return this;
		}

		public SourceRecordConverter build() {
			return new SourceRecordConverter(this);
		}

	}

	public static SourceRecordConverterBuilder builder() {
		return new SourceRecordConverterBuilder();
	}

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
