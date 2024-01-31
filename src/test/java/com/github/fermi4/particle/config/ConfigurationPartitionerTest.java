package com.github.fermi4.particle.config;

import static com.github.fermi4.particle.config.ParticleConnectorConfig.ACCESS_TOKEN_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.DEVICE_ID_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_CONFIG;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.EVENT_MODE_DEVICE;
import static com.github.fermi4.particle.config.ParticleConnectorConfig.TOPIC_CONFIG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.config.partition.DeviceIdPartitioner;
import com.github.fermi4.particle.config.partition.ParticleTaskConfigPartitioner;
import com.github.fermi4.particle.config.partition.PartitionerFactory;

public class ConfigurationPartitionerTest {
	
	private final String TOPIC = "test_topic";
	private final String ACCESS_KEY = "test_access_key";
	
	@Test
	public void TestDevicePartitioner() {
		/* expected */
		int EXPECTED_CONFIGS = 5;
		int NUM_DEVICES=10;
		int MAX_TASK = 5;

		int EXPECTED_DEVICE_PER_TASK = NUM_DEVICES/MAX_TASK;
		
		/* create fake list of devices */
		List<String> devices = new ArrayList<>();
		for (int i = 0; i < NUM_DEVICES; i++) {
			devices.add(Integer.toString(i));
		}
		
		/* comma sep string of devices */
		String deviceString = devices.stream().reduce((s1,s2)->s1+","+s2).orElse("");
		System.out.println(String.format("Testing device string [%s]", deviceString));
		
		/* create device config */
		Map<String, String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);
		map.put(DEVICE_ID_CONFIG, deviceString);
		ParticleConnectorConfig config = new ParticleConnectorConfig(map);
		
		/* get a partitioner based on the configuration */
		ParticleTaskConfigPartitioner partitioner = PartitionerFactory.get(config);
		
		/* this should be DeviceIdPartitioner */
		Assertions.assertTrue(DeviceIdPartitioner.class.isAssignableFrom(partitioner.getClass()));
		
		/* print out generated task configs and assert expected number of devices in each */
		List<Map<String, String>> partitionedConfigs = partitioner.partition(config, MAX_TASK);
		
		for (Iterator<Map<String, String>> iterator = partitionedConfigs.iterator(); iterator.hasNext();) {
			Map<String, String> singleTaskConfig = iterator.next();
			
			System.out.println("\nTask configuration created:");
			singleTaskConfig.forEach((k,v)->{
				System.out.println("\t" + k + "=" + v);
			});
		}
		
		/* assert that we got the expected number of task configurations */
		Assertions.assertTrue(partitionedConfigs.size() == EXPECTED_CONFIGS);
	}
	

	@Test
	public void TestDevicePartitioner_Devices_LessThan_MaxTask() {
		/* expected */
		int EXPECTED_CONFIGS = 3;
		int NUM_DEVICES=3;
		int MAX_TASK = 5;

		int EXPECTED_DEVICE_PER_TASK = 1;
		
		/* create fake list of devices */
		List<String> devices = new ArrayList<>();
		for (int i = 0; i < NUM_DEVICES; i++) {
			devices.add(Integer.toString(i));
		}
		
		/* comma sep string of devices */
		String deviceString = devices.stream().reduce((s1,s2)->s1+","+s2).orElse("");
		System.out.println(String.format("Testing device string [%s]", deviceString));
		
		/* create device config */
		Map<String, String> map = new HashMap<>();
		map.put(EVENT_MODE_CONFIG, EVENT_MODE_DEVICE);
		map.put(TOPIC_CONFIG, TOPIC);
		map.put(ACCESS_TOKEN_CONFIG, ACCESS_KEY);
		map.put(DEVICE_ID_CONFIG, deviceString);
		ParticleConnectorConfig config = new ParticleConnectorConfig(map);
		
		/* get a partitioner based on the configuration */
		ParticleTaskConfigPartitioner partitioner = PartitionerFactory.get(config);
		
		/* this should be DeviceIdPartitioner */
		Assertions.assertTrue(DeviceIdPartitioner.class.isAssignableFrom(partitioner.getClass()));
		
		/* print out generated task configs and assert expected number of devices in each */
		List<Map<String, String>> partitionedConfigs = partitioner.partition(config, MAX_TASK);
		for (Iterator<Map<String, String>> iterator = partitionedConfigs.iterator(); iterator.hasNext();) {
			Map<String, String> singleTaskConfig = iterator.next();
			Assertions.assertTrue(singleTaskConfig.get(DEVICE_ID_CONFIG).split(",").length == EXPECTED_DEVICE_PER_TASK);
			System.out.println("\nTask configuration created:");
			singleTaskConfig.forEach((k,v)->{
				System.out.println("\t" + k + "=" + v);
			});
		}
		
		/* assert that we got the expected number of task configurations */
		Assertions.assertTrue(partitionedConfigs.size() == EXPECTED_CONFIGS);
	}
	
	
	
	
	
}
