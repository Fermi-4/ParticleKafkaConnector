package com.github.fermi4.particle.e2e;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import com.github.fermi4.particle.api.http.ParticleApiFactory;
import com.github.fermi4.particle.api.ParticleClient;
import com.github.fermi4.particle.api.ParticleClientFactory;
import com.github.fermi4.particle.api.domain.DeviceInformation;
import com.github.fermi4.particle.api.domain.resource.DeviceApiResponse;
import com.github.fermi4.particle.api.domain.resource.ProductApiResponse;

import okhttp3.OkHttpClient;


@EnabledIfEnvironmentVariable(named = "PARTICLE_DEVICE_ID", matches = ".*")
@EnabledIfEnvironmentVariable(named = "PARTICLE_PRODUCT_ID", matches = ".*")
@EnabledIfEnvironmentVariable(named = "PARTICLE_ACCESS_KEY", matches = ".*")
@EnabledIfEnvironmentVariable(named = "PARTICLE_PRODUCT_GROUP1", matches = ".*")
@EnabledIfEnvironmentVariable(named = "PARTICLE_PRODUCT_GROUP2", matches = ".*")
public class ParticleClientV1APITest {
	
	private final String ACCESS_KEY = System.getenv("PARTICLE_ACCESS_KEY");
	private final String PRODUCT_ID = System.getenv("PARTICLE_PRODUCT_ID");
	private final String DEVICE_ID = System.getenv("PARTICLE_DEVICE_ID");
	private final List<String> PRODUCT_GROUPS = List.of(
    		System.getenv("PARTICLE_PRODUCT_GROUP1"),
    		System.getenv("PARTICLE_PRODUCT_GROUP2")
    	);
	
	ParticleClient client  = ParticleClient.builder()
			.mapper(ParticleClientFactory.createObjectMapper())
			.client(new OkHttpClient())
			.accessToken(ACCESS_KEY)
			.endpointFactory(ParticleApiFactory.v1())
			.build();
	
    @Test
    public void testListDevicesForProductInGroups() throws IOException {
    	Assertions.assertNotNull(PRODUCT_ID);
    	Assertions.assertNotNull(PRODUCT_GROUPS);
        DeviceApiResponse response = client.listDevicesInProductInGroups(PRODUCT_ID, PRODUCT_GROUPS);
        Assertions.assertEquals(1, response.getDevices().size());
    }

    @Test
    public void testGetAllDevicesInProduct() throws IOException {
    	Assertions.assertNotNull(PRODUCT_ID);
        DeviceApiResponse response = client.listDevicesInProduct(PRODUCT_ID);
        Assertions.assertEquals(2, response.getDevices().size());
    }
    
    @Test
    public void testGetAllDevices() throws IOException {
        int EXPECTED_NUM_DEVICES = 4;
    	List<DeviceInformation> response = client.listAllDevices();
        Assertions.assertEquals(EXPECTED_NUM_DEVICES, response.size());        
    }
    
    @Test
    public void testGetAllProducts() throws IOException {
        int EXPECTED_NUM_PRODUCTS = 2;
    	ProductApiResponse response = client.listAllProducts();
        Assertions.assertEquals(EXPECTED_NUM_PRODUCTS, response.getProducts().size());        
    }
    
    @Test
    public void testGetDeviceInformation() throws IOException {
    	Assertions.assertNotNull(DEVICE_ID);
    	DeviceInformation response = client.getDeviceInformation(DEVICE_ID);
    	System.out.println(response);
        Assertions.assertEquals(DEVICE_ID, response.getId());
    }
}
