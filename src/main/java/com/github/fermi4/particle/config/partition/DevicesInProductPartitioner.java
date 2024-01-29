package com.github.fermi4.particle.config.partition;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.connect.errors.ConnectException;

import com.github.fermi4.particle.config.ParticleConnectorConfig;

import io.github.fermi4.particle.v1.ParticleClient;
import io.github.fermi4.particle.v1.domain.DeviceInformation;
import io.github.fermi4.particle.v1.domain.resource.DeviceApiResponse;
import io.github.fermi4.particle.v1.domain.serde.ParticleDeserializer;
import okhttp3.Response;

public class DevicesInProductPartitioner implements ParticleTaskConfigPartitioner {

    private ParticleClient client;
    private final String productIdOrSlug;

    public DevicesInProductPartitioner(ParticleClient client, String productIdOrSlug) {
        this.client = client;
        this.productIdOrSlug = productIdOrSlug;
    }

    @Override
    public List<Map<String, String>> partition(ParticleConnectorConfig config, int maxTask) {
        /* check if the access token is defined */
        if (config.getAccessToken() == null || config.getAccessToken().isEmpty()) {
            throw new ConnectException(
                    "Access token is null or empty -- check config " + ParticleConnectorConfig.ACCESS_TOKEN_CONFIG);
        }
        try {

            Response response = client.listDevicesInProduct(this.productIdOrSlug); // TODO: handle pagination
            if (!response.isSuccessful()) {
                throw new ConnectException("Got bad response from ParticleClient::listDevices -- error code: ["
                        + response.code() + "] response: " + response.toString());
            }

            /* NOTE: consumes the string property in Response */
            String payload = response.body().string();
            DeviceApiResponse deviceApiResponse = ParticleDeserializer.getParticleDeserializer().readValue(payload,
                    DeviceApiResponse.class);

            String targetDevices = deviceApiResponse.getDevices()
                    .stream()
                    .map(DeviceInformation::getId)
                    .collect(Collectors.joining(config.getDelimiter()));

            Map<String, String> newConfigMap = config.getOriginalsAsStringWithDefaults();

            newConfigMap.put(ParticleConnectorConfig.DEVICE_ID_CONFIG, targetDevices);

            ParticleConnectorConfig newConfig = new ParticleConnectorConfig(newConfigMap);
            return PartitionUtil.distributePartitionsAsMaps(newConfig, ParticleConnectorConfig.DEVICE_ID_CONFIG,
                    config.getDelimiter(), maxTask);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConnectException(e);
        }
    }

}
