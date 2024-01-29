package com.github.fermi4.particle.config.partition;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.connect.errors.ConnectException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fermi4.particle.config.ParticleConnectorConfig;

import io.github.fermi4.particle.v1.ParticleClient;
import io.github.fermi4.particle.v1.domain.DeviceInformation;
import io.github.fermi4.particle.v1.domain.serde.ParticleDeserializer;
import okhttp3.Response;

public class AllDevicesPartitioner implements ParticleTaskConfigPartitioner {

    private ParticleClient client;

    public AllDevicesPartitioner(ParticleClient client) {
        this.client = client;
    }

    @Override
    public List<Map<String, String>> partition(ParticleConnectorConfig config, int maxTask) {

        /* check if the access token is defined */
        if (config.getAccessToken() == null || config.getAccessToken().isEmpty()) {
            throw new ConnectException(
                    "Access token is null or empty -- check config " + ParticleConnectorConfig.ACCESS_TOKEN_CONFIG);
        }

        try {
            Response devicesResponse = client.listDevices();
            if (devicesResponse.code() != 200) {
                throw new ConnectException("Got bad response from ParticleClient::listDevices -- error code: ["
                        + devicesResponse.code() + "] response: " + devicesResponse.toString());
            }

            /* NOTE: consumes the string property in Response */
            String payload = devicesResponse.body().string();

            /* Deserialize payload */
            ObjectMapper deserializer = ParticleDeserializer.getParticleDeserializer();
            List<DeviceInformation> deviceInfoList = Arrays
                    .asList(deserializer.readValue(payload, DeviceInformation[].class));

            // comma separated list of device id
            String targetDevices = deviceInfoList.stream()
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
