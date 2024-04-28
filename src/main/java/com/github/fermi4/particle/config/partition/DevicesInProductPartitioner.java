package com.github.fermi4.particle.config.partition;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.connect.errors.ConnectException;

import com.github.fermi4.particle.api.ParticleClient;
import com.github.fermi4.particle.api.domain.DeviceInformation;
import com.github.fermi4.particle.config.ParticleConnectorConfig;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DevicesInProductPartitioner implements ParticleTaskConfigPartitioner {
	
	private ParticleClient client;
    private final String productIdOrSlug;
    
	@Override
    public List<Map<String, String>> partition(ParticleConnectorConfig config, int maxTask) {
        /* check if the access token is defined */
        if(config.getAccessToken() == null || config.getAccessToken().isEmpty()) {
            throw new ConnectException("Access token is null or empty -- check config " + ParticleConnectorConfig.ACCESS_TOKEN_CONFIG);
        }
        try {
            
            String targetDevices = client.listDevicesInProduct(this.productIdOrSlug)
                                        .getDevices()
                                        .stream()
                                        .map(DeviceInformation::getId)
                                        .collect(Collectors.joining(config.getDelimiter()));
            
            Map<String, String> newConfigMap = config.getOriginalsAsStringWithDefaults();
            
            newConfigMap.put(ParticleConnectorConfig.DEVICE_ID_CONFIG, targetDevices);
            
            ParticleConnectorConfig newConfig = new ParticleConnectorConfig(newConfigMap);
            return PartitionUtil.distributePartitionsAsMaps(newConfig, ParticleConnectorConfig.DEVICE_ID_CONFIG, config.getDelimiter(), maxTask);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConnectException(e);
        }
    }
    
}
