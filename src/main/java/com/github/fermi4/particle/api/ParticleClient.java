package com.github.fermi4.particle.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fermi4.particle.api.domain.DeviceInformation;
import com.github.fermi4.particle.api.domain.resource.DeviceApiResponse;
import com.github.fermi4.particle.api.domain.resource.ProductApiResponse;
import com.github.fermi4.particle.api.http.ParticleApiFactory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ParticleClient {

	private OkHttpClient client;
	private String accessToken;
	private ObjectMapper mapper;
	private ParticleApiFactory endpointFactory;

	public DeviceApiResponse listDevicesInProduct(String productIdOrSlug) throws IOException {
		Map<String, String> queryParameters = new HashMap<>();
        return listDevicesInProduct(productIdOrSlug, queryParameters);
    }

	public DeviceApiResponse listDevicesInProductInGroups(String productIdOrSlug, List<String> productGroups) throws IOException {
		Map<String, String> queryParameters = new HashMap<>();
        return listDevicesInProductInGroups(productIdOrSlug, productGroups, queryParameters);
    }

	public DeviceApiResponse listDevicesInProductInGroups(String productIdOrSlug, List<String> productGroups, Map<String, String> queryParameters) throws IOException {
        return listDevicesWithPagination(
                (page) -> endpointFactory.listDevicesInProductGroups(accessToken, productIdOrSlug, productGroups, addPageToQueryParams(queryParameters, page)));
    }

    public DeviceApiResponse listDevicesInProduct(String productIdOrSlug, Map<String, String> queryParameters) throws IOException {
        return listDevicesWithPagination(
                (page) -> endpointFactory.listDevicesInProduct(accessToken, productIdOrSlug, addPageToQueryParams(queryParameters, page)));
    }
	
	public DeviceInformation getDeviceInformation(String deviceId) throws IOException {
        HttpUrl url = endpointFactory.getDeviceInformation(accessToken, deviceId);
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if(response == null) {
                throw new IOException("Response from request " + request.toString() + " was null"  + response);
            }
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return mapper.readValue(response.body().string(), DeviceInformation.class);
        }
    }

    public List<DeviceInformation> listAllDevices() throws IOException {
        HttpUrl url = endpointFactory.listAllDevices(accessToken);
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if(response == null) {
                throw new IOException("Response from request " + request.toString() + " was null"  + response);
            }
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return List.of(mapper.readValue(response.body().string(), DeviceInformation[].class));
        }
    }

    public ProductApiResponse listAllProducts() throws IOException {
        HttpUrl url = endpointFactory.listAllProducts(accessToken);
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if(response == null) {
                throw new IOException("Response from request " + request.toString() + " was null"  + response);
            }
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return mapper.readValue(response.body().string(), ProductApiResponse.class);
        }
    }
	
	public EventSource createEventSource(Request request, EventSourceListener listener) {
		return EventSources.createFactory(this.client).newEventSource(request, listener);
	}
	
	public EventSource sseAllEventsFiltered(String eventPrefix, EventSourceListener listener) {
		Request request = getSseRequest(endpointFactory.getAllEventsFiltered(accessToken, eventPrefix));
		return createEventSource(request, listener);
	}
	
	public EventSource sseAllDeviceEvents(EventSourceListener listener) {
		Request request = getSseRequest(endpointFactory.getAllDeviceEvents(accessToken));
		return createEventSource(request, listener);
	}
	
	public EventSource sseAllDeviceEventsFiltered(String eventPrefix, EventSourceListener listener) {
		Request request = getSseRequest(endpointFactory.getAllDeviceEventsFiltered(accessToken, eventPrefix));
		return createEventSource(request, listener);
	}
	
	public EventSource sseDeviceEvents(String deviceId, EventSourceListener listener) {
		Request request = getSseRequest(endpointFactory.getDeviceEvents(accessToken, deviceId));
		return createEventSource(request, listener);
	}
	
	public EventSource sseDeviceEventsFiltered(String deviceId, String eventPrefix, EventSourceListener listener) {
		Request request = getSseRequest(endpointFactory.getDeviceEventsFiltered(accessToken, deviceId, eventPrefix));
		return createEventSource(request, listener);
	}
	
	public EventSource sseProductEvents(String productIdOrSlug, EventSourceListener listener) {
		Request request = getSseRequest(endpointFactory.getProductEvents(accessToken, productIdOrSlug));
		return createEventSource(request, listener);
	}
	
	public EventSource sseProductEventsFiltered(String productIdOrSlug, String eventPrefix, EventSourceListener listener) {
		Request request = getSseRequest(endpointFactory.getProductEventsFiltered(accessToken, productIdOrSlug, eventPrefix));
		return createEventSource(request, listener);
	}
	
	private Request getSseRequest(HttpUrl url) {
		return new Request.Builder()
				.addHeader("Content-Type", "text/event-stream")
				.addHeader("Connection", "keep-alive")
				.addHeader("Cache-Control", "no-cache")
				.url(url)
				.build();
	}

	private Map<String, String> addPageToQueryParams(Map<String, String> queryParams, int page) {
        queryParams.put("page", String.valueOf(page));
        return queryParams;
    }

    private DeviceApiResponse listDevicesWithPagination(Function<Integer, HttpUrl> urlProvider) throws IOException {
        int currentPage = 1;
        DeviceApiResponse aggregatedResponse = new DeviceApiResponse();

        do {
            HttpUrl url = urlProvider.apply(currentPage);
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                if(response == null) {
                    throw new IOException("Response from request " + request.toString() + " was null"  + response);
                }

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String jsonResponseString = response.body().string();
                DeviceApiResponse apiResponse = mapper.readValue(jsonResponseString, DeviceApiResponse.class);

                aggregatedResponse.getDevices().addAll(apiResponse.getDevices());
                aggregatedResponse.getCustomers().addAll(apiResponse.getCustomers());

                currentPage++;
            }
        } while (currentPage <= aggregatedResponse.getTotalPages());

        return aggregatedResponse;
    }
}
