package com.github.fermi4.particle.config.partition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.connect.errors.ConnectException;

import com.github.fermi4.particle.config.ParticleConnectorConfig;

import io.github.fermi4.particle.v1.ParticleClient;
import io.github.fermi4.particle.v1.domain.ProductInformation;
import io.github.fermi4.particle.v1.domain.resource.ProductApiResponse;
import io.github.fermi4.particle.v1.domain.serde.ParticleDeserializer;
import okhttp3.Response;

public class AllProductsPartitioner implements ParticleTaskConfigPartitioner {

    private ParticleClient client;

    public AllProductsPartitioner(ParticleClient client) {
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

            Response response = client.listProducts();

            if (!response.isSuccessful()) {
                throw new ConnectException("Got bad response from ParticleClient::listProducts -- error code: ["
                        + response.code() + "] response: " + response.toString());
            }

            /* Deserialize payload into ProductApiResponse */
            ProductApiResponse productApiResponse = ParticleDeserializer.getParticleDeserializer()
                    .readValue(response.body().string(), ProductApiResponse.class);

            // comma separated list of product slug/id
            String targetProducts = productApiResponse.getProducts().stream()
                    .map(ProductInformation::getId)
                    .map(String::valueOf)
                    .collect(Collectors.joining(config.getDelimiter()));

            Map<String, String> newConfigMap = config.getOriginalsAsStringWithDefaults();
            newConfigMap.put(ParticleConnectorConfig.PRODUCT_ID_CONFIG, targetProducts);

            /* force the event mode config to be product id */
            newConfigMap.put(ParticleConnectorConfig.EVENT_MODE_CONFIG, ParticleConnectorConfig.EVENT_MODE_PRODUCT);

            ParticleConnectorConfig newConfig = new ParticleConnectorConfig(newConfigMap);
            return PartitionUtil.distributePartitionsAsMaps(newConfig, ParticleConnectorConfig.PRODUCT_ID_CONFIG,
                    config.getDelimiter(), maxTask);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConnectException(e);
        }
    }

}
