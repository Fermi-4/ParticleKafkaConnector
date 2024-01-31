package com.github.fermi4.particle.convert.extract;

import java.util.Objects;
import java.util.function.Function;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.github.fermi4.particle.convert.ConverterContext;

public class EventDataExtractors {
	
	public static Function<ConverterContext, EventDataExtraction> extractNull() {
		return (ctx) -> new EventDataExtraction(null, null);
	}
	
	public static Function<ConverterContext, EventDataExtraction> extractEventTypeBytes() {
		return (ctx) -> new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA, ctx.getEvent().getType().getBytes());
	}
	
	public static Function<ConverterContext, EventDataExtraction> extractEventDataBytes() {
		return (ctx) -> new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA, ctx.getEvent().getData().getBytes());
	}
	
	public static Function<ConverterContext, EventDataExtraction> extractConfigDeviceIdBytes() {
		return (ctx) -> new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA, ctx.getConfig().getDeviceId().getBytes());
	}
	
	public static Function<ConverterContext, EventDataExtraction> extractConfigProductIdBytes() {
		return (ctx) -> new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA, ctx.getConfig().getProductId().getBytes());
	}
	
	public static Function<ConverterContext, EventDataExtraction> extractPayloadCoreIdBytes() {
		return (ctx) -> {
			Objects.requireNonNull(ctx.getEvent());
			String eventJson = ctx.getEvent().getData();
			Objects.requireNonNull(eventJson);
			JsonNode node = convertJsonString(eventJson);
			JsonNode coreId = node.get("coreid");
			Objects.requireNonNull(coreId);
			return new EventDataExtraction(Schema.OPTIONAL_BYTES_SCHEMA, coreId.asText().getBytes());
		};
	}
	
	private static JsonNode convertJsonString(String jsonString) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readTree(jsonString); 
		} catch (JsonProcessingException e) {
			throw new ConnectException(e);
		}
	}
}
