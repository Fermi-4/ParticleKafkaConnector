package com.github.fermi4.particle.e2e;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.ACCESS_TOKEN_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.DEVICE_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_DEVICE;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.TOPIC_CONFIG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import com.github.fermi4.particle.api.http.ParticleApiFactory;
import com.github.fermi4.particle.api.ParticleClient;
import com.github.fermi4.particle.api.ParticleClientFactory;
import com.github.fermi4.particle.config.ParticleConnectorConfig;
import com.github.fermi4.particle.config.partition.AllDevicesPartitioner;
import com.github.fermi4.particle.config.partition.AllProductsPartitioner;
import com.github.fermi4.particle.config.partition.DevicesInGroupsPartitioner;
import com.github.fermi4.particle.config.partition.DevicesInProductPartitioner;

import okhttp3.OkHttpClient;

@EnabledIfEnvironmentVariable(named = "PARTICLE_DEVICE_ID", matches = ".*")
@EnabledIfEnvironmentVariable(named = "PARTICLE_PRODUCT_ID", matches = ".*")
@EnabledIfEnvironmentVariable(named = "PARTICLE_ACCESS_KEY", matches = ".*")
@EnabledIfEnvironmentVariable(named = "PARTICLE_PRODUCT_GROUP1", matches = ".*")
@EnabledIfEnvironmentVariable(named = "PARTICLE_PRODUCT_GROUP2", matches = ".*")
public class DiscoverPartitionerTest {

    private final String TOPIC = "test_topic";
	private final String ACCESS_KEY = System.getenv("PARTICLE_ACCESS_KEY");
	private final String PRODUCT_ID = System.getenv("PARTICLE_PRODUCT_ID");
    private final List<String> PRODUCT_GROUPS = List.of(
    		System.getenv("PARTICLE_PRODUCT_GROUP1"),
    		System.getenv("PARTICLE_PRODUCT_GROUP2")
    	);
    
    ParticleClient client = ParticleClient.builder()
			.mapper(ParticleClientFactory.createObjectMapper())
			.client(new OkHttpClient())
			.accessToken(ACCESS_KEY)
			.endpointFactory(ParticleApiFactory.v1())
			.build();
    
	@Test
    public void testDevicesInProduct() {
		

        int MAX_TASK = 10;
        int EXPECTED_NUM_CONFIG = 2;
        
        /* create device config */
		Map<String, String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);
		map.put(DEVICE_ID_CONFIG, "");
        map.put(ParticleConnectorConfig.PRODUCT_ID_CONFIG, PRODUCT_ID);
		ParticleConnectorConfig config = new ParticleConnectorConfig(map);
        DevicesInProductPartitioner partitioner = new DevicesInProductPartitioner(client, PRODUCT_ID);
        List<Map<String, String>> configs = partitioner.partition(config, MAX_TASK);

        Assertions.assertEquals(EXPECTED_NUM_CONFIG, configs.size());
    }

	@Test
    public void testDevicesInProductAndGroups() {

        int MAX_TASK = 10;
        int EXPECTED_NUM_CONFIG = 1;
        
        /* create device config */
		Map<String, String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);
		map.put(DEVICE_ID_CONFIG, "");
        map.put(ParticleConnectorConfig.PRODUCT_ID_CONFIG, PRODUCT_ID);
		ParticleConnectorConfig config = new ParticleConnectorConfig(map);
		DevicesInGroupsPartitioner partitioner = new DevicesInGroupsPartitioner(client, PRODUCT_ID, PRODUCT_GROUPS);
        List<Map<String, String>> configs = partitioner.partition(config, MAX_TASK);
        System.out.println(configs);
        Assertions.assertEquals(EXPECTED_NUM_CONFIG, configs.size());
    }
	
    @Test
    public void testAllDevices() {

        int MAX_TASK = 10;
        int EXPECTED_NUM_CONFIG = 4;
        
        /* create device config */
		Map<String, String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);
		map.put(DEVICE_ID_CONFIG, "");
        ParticleConnectorConfig config = new ParticleConnectorConfig(map);
        AllDevicesPartitioner partitioner = new AllDevicesPartitioner(client);
        List<Map<String, String>> configs = partitioner.partition(config, MAX_TASK);

        // should only create 4 configs
        Assertions.assertEquals(EXPECTED_NUM_CONFIG, configs.size());
    }

    @Test
    public void testAllProducts() {

        int MAX_TASK = 10;
        int EXPECTED_NUM_CONFIG = 2;
        /* create device config */
		Map<String, String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);
		map.put(DEVICE_ID_CONFIG, "");
        ParticleConnectorConfig config = new ParticleConnectorConfig(map);
        AllProductsPartitioner partitioner = new AllProductsPartitioner(client);
        List<Map<String, String>> configs = partitioner.partition(config, MAX_TASK);

        // should only create 2 configs
        Assertions.assertEquals(EXPECTED_NUM_CONFIG, configs.size());
    }

}
