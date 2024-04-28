package com.github.fermi4.particle.domain;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.fermi4.particle.api.domain.DeviceInformation;
import com.github.fermi4.particle.api.serde.DeviceInformationDeserializer;

public class DeviceInformationTest {

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return dateFormat.format(date);
    }
    
    @Test
    public void testDeserialization() {
        String json = "{\"id\":\"0123456789abcdef01234567\",\"name\":\"gongbot\",\"owner\":\"someone@particle.io\",\"last_ip_address\":\"176.83.211.237\",\"last_heard\":\"2015-07-17T22:28:40.907Z\",\"last_handshake_at\":\"2015-07-15T20:08:00.456Z\",\"product_id\":13,\"online\":true,\"platform_id\":13,\"cellular\":true,\"notes\":null,\"functions\":[\"gong\",\"goto\"],\"variables\":{\"Gongs\":\"int32\"},\"status\":\"normal\",\"serial_number\":\"AAAAAA111111111\",\"iccid\":\"89314404000111111111\",\"imei\":\"357520000000000\",\"mac_wifi\":\"00:00:00:AA:BB:00\",\"mobile_secret\":\"NVYM5RK6AHCBELA\",\"system_firmware_version\":\"1.5.0\",\"firmware_updates_enabled\":true,\"firmware_updates_forced\":false,\"device_protection\":{\"status\":\"disabled\"}}";

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new SimpleModule().addDeserializer(DeviceInformation.class, new DeviceInformationDeserializer()));

        try {
            DeviceInformation device = objectMapper.readValue(json, DeviceInformation.class);

            assertNotNull(device);
           
            assertEquals("0123456789abcdef01234567", device.getId());
            assertEquals("gongbot", device.getName());
            assertEquals("someone@particle.io", device.getOwner());
            assertEquals("176.83.211.237", device.getLastIpAddress());
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException occurred: " + e.getMessage());
        }
    }

    @Test
    public void testDeserialization_ArrayOf2() {
        String json = "[{\"id\":\"53ff6f0650723\",\"name\":\"plumber_laser\",\"last_ip_address\":\"10.0.0.1\",\"last_heard\":\"2020-05-28T14:46:07.586Z\",\"last_handshake_at\":\"2020-05-27T18:13:01.059Z\",\"product_id\":6,\"online\":true,\"platform_id\":6,\"cellular\":false,\"notes\":\"laser!\",\"functions\":[\"fire\"],\"variables\":{\"power\":\"int32\"},\"status\":\"normal\",\"serial_number\":\"PH1234\",\"mac_wifi\":\"00:00:00:AA:BB:00\",\"system_firmware_version\":\"1.5.0\"},{\"id\":\"53ff291839887\",\"name\":\"particle_love\",\"last_ip_address\":\"10.0.0.1\",\"last_heard\":\"2020-05-28T14:46:07.586Z\",\"last_handshake_at\":\"2020-05-27T18:13:01.059Z\",\"product_id\":10,\"online\":false,\"platform_id\":10,\"notes\":null,\"functions\":[],\"variables\":{},\"cellular\":true,\"status\":\"normal\",\"serial_number\":\"E261234\",\"iccid\":\"1111111111111111111\",\"imei\":\"333333333333333\",\"system_firmware_version\":\"1.5.0\"}]";

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new SimpleModule().addDeserializer(DeviceInformation.class, new DeviceInformationDeserializer()));

        try {
            DeviceInformation[] devices = objectMapper.readValue(json, DeviceInformation[].class);

            assertNotNull(devices);
            assertEquals(2, devices.length);

            DeviceInformation device1 = devices[0];
            assertEquals("53ff6f0650723", device1.getId());
            assertEquals("plumber_laser", device1.getName());
            assertEquals("10.0.0.1", device1.getLastIpAddress());
            assertEquals("2020-05-28T14:46:07.586Z", formatDate(device1.getLastHeard()));
            assertEquals("2020-05-27T18:13:01.059Z", formatDate(device1.getLastHandshakeAt()));
            assertEquals(6, device1.getProductId());
            assertTrue(device1.isOnline());
            assertEquals(6, device1.getPlatformId());
            assertFalse(device1.isCellular());
            assertEquals("laser!", device1.getNotes());
            assertEquals(1, device1.getFunctions().size());
            assertEquals("fire", device1.getFunctions().get(0));
            assertEquals(1, device1.getVariables().size());
            assertTrue(device1.getVariables().containsKey("power"));
            assertEquals("int32", device1.getVariables().get("power"));
            assertEquals("normal", device1.getStatus());
            assertEquals("PH1234", device1.getSerialNumber());
            assertEquals("00:00:00:AA:BB:00", device1.getMacWifi());
            assertEquals("1.5.0", device1.getSystemFirmwareVersion());

            DeviceInformation device2 = devices[1];
            assertEquals("53ff291839887", device2.getId());
            assertEquals("particle_love", device2.getName());
            assertEquals("10.0.0.1", device2.getLastIpAddress());
            assertEquals("2020-05-28T14:46:07.586Z", formatDate(device2.getLastHeard()));
            assertEquals("2020-05-27T18:13:01.059Z", formatDate(device2.getLastHandshakeAt()));
            assertEquals(10, device2.getProductId());
            assertFalse(device2.isOnline());
            assertEquals(10, device2.getPlatformId());
            assertNull(device2.getNotes());
            assertEquals(0, device2.getFunctions().size());
            assertEquals(0, device2.getVariables().size());
            assertTrue(device2.isCellular());
            assertEquals("normal", device2.getStatus());
            assertEquals("E261234", device2.getSerialNumber());
            assertEquals("1111111111111111111", device2.getIccid());
            assertEquals("333333333333333", device2.getImei());
            assertEquals("1.5.0", device2.getSystemFirmwareVersion());

        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException occurred: " + e.getMessage());
        }
    }

    @Test
    public void testDeserialization_ArrayOf1() {
        String json = "[{\"id\":\"0123456789abcdef01234567\",\"name\":\"gongbot\",\"owner\":\"someone@particle.io\",\"last_ip_address\":\"176.83.211.237\",\"last_heard\":\"2015-07-17T22:28:40.907Z\",\"last_handshake_at\":\"2015-07-15T20:08:00.456Z\",\"product_id\":13,\"online\":true,\"platform_id\":13,\"cellular\":true,\"notes\":null,\"functions\":[\"gong\",\"goto\"],\"variables\":{\"Gongs\":\"int32\"},\"status\":\"normal\",\"serial_number\":\"AAAAAA111111111\",\"iccid\":\"89314404000111111111\",\"imei\":\"357520000000000\",\"mac_wifi\":\"00:00:00:AA:BB:00\",\"mobile_secret\":\"NVYM5RK6AHCBELA\",\"system_firmware_version\":\"1.5.0\",\"firmware_updates_enabled\":true,\"firmware_updates_forced\":false,\"device_protection\":{\"status\":\"disabled\"}}]";

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new SimpleModule().addDeserializer(DeviceInformation.class, new DeviceInformationDeserializer()));

        try {
            DeviceInformation[] devices = objectMapper.readValue(json, DeviceInformation[].class);

            assertNotNull(devices);
            assertEquals(1, devices.length);

            DeviceInformation device = devices[0];
            assertEquals("0123456789abcdef01234567", device.getId());
            assertEquals("gongbot", device.getName());
            assertEquals("someone@particle.io", device.getOwner());
            assertEquals("176.83.211.237", device.getLastIpAddress());
            
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException occurred: " + e.getMessage());
        }
    }
}