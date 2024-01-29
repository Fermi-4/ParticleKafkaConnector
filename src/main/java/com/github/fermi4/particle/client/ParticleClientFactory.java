package com.github.fermi4.particle.client;

import java.util.concurrent.TimeUnit;

import org.apache.kafka.connect.errors.ConnectException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.fermi4.particle.v1.domain.DeviceInformation;
import io.github.fermi4.particle.v1.domain.resource.DeviceApiResponse;
import io.github.fermi4.particle.v1.domain.resource.ProductApiResponse;
import io.github.fermi4.particle.v1.domain.serde.DeviceApiResponseDeserializer;
import io.github.fermi4.particle.v1.domain.serde.DeviceInformationDeserializer;
import io.github.fermi4.particle.v1.domain.serde.ProductApiResponseDeserializer;
import io.github.fermi4.particle.v1.ParticleClient;
//import com.github.fermi4.particle.api.http.ParticleApiFactory;
//import com.github.fermi4.particle.api.serde.DeviceApiResponseDeserializer;
//import com.github.fermi4.particle.api.serde.DeviceInformationDeserializer;
//import com.github.fermi4.particle.api.serde.ProductApiResponseDeserializer;
import com.github.fermi4.particle.config.HttpClientConfig;
import com.github.fermi4.particle.config.ParticleConnectorConfig;

import okhttp3.OkHttpClient;

public class ParticleClientFactory {
	public static ParticleClient get(ParticleConnectorConfig config) {
		String version = config.getApiVersion();
		if (version == null) {
			throw new ConnectException("API version cannot be null when using ParticleClient -- check "
					+ ParticleConnectorConfig.API_VERSION_CONFIG);
		}
		switch (version) {
			case ParticleConnectorConfig.PARTICLE_CONNECTOR_API_VERSION_1:
				return ParticleClientFactory.getV1ParticleClient(config);
			default:
				throw new ConnectException(
						"API version not recognized for constructing ParticleClient -- check "
								+ ParticleConnectorConfig.API_VERSION_CONFIG);
		}
	}

	public static ParticleClient getV1ParticleClient(ParticleConnectorConfig config) {

		/* Extract the http client config options */
		HttpClientConfig clientConfig = config.getHttpClientConfig();

		/* Create instance of http client */
		OkHttpClient httpClient = ParticleClientFactory.createOkHttpClientFromConfig(clientConfig);

		/* Create instance of ParticleClient */
		ParticleClient particleClient = ParticleClient.builder()
				.withToken(config.getAccessToken())
				.withMapper(ParticleClientFactory.createObjectMapper())
				.withClient(httpClient)
				.build();

		return particleClient;
	}

	protected static OkHttpClient createOkHttpClientFromConfig(HttpClientConfig httpClientConfig) {
		OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

		// TODO: Configure Proxy if provided
		// if (httpClientConfig.getProxyHost() != null &&
		// !httpClientConfig.getProxyHost().isEmpty()) {
		// if (httpClientConfig.getProxyHost().toLowerCase() ==
		// ParticleConnectorConfig.HTTP_PROXY_HOST_CONFIG_NO_PROXY) {
		// httpClientBuilder.proxy(Proxy.NO_PROXY);
		// } else {
		// Proxy proxy = new Proxy(Proxy.Type.HTTP,new
		// InetSocketAddress(httpClientConfig.getProxyHost(),
		// httpClientConfig.getProxyPort()));
		// httpClientBuilder.proxy(proxy);
		// }
		// }
		if (httpClientConfig != null) {
			httpClientBuilder.readTimeout(httpClientConfig.getReadTimeout(), TimeUnit.MILLISECONDS);
			httpClientBuilder.connectTimeout(httpClientConfig.getConnectTimeout(), TimeUnit.MILLISECONDS);
		}

		return httpClientBuilder.build();
	}

	public static ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new SimpleModule()
				.addDeserializer(DeviceInformation.class,
						new DeviceInformationDeserializer(DeviceInformation.class))
				.addDeserializer(DeviceApiResponse.class,
						new DeviceApiResponseDeserializer(DeviceApiResponse.class))
				.addDeserializer(ProductApiResponse.class,
						new ProductApiResponseDeserializer(ProductApiResponse.class)));
		return mapper;
	}
}
