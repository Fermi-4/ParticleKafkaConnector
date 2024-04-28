package com.github.fermi4.particle.api.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeviceInformationTest {

    private DeviceInformation deviceInfo;

    @BeforeEach
    public void setUp() {
        deviceInfo = new DeviceInformation();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(deviceInfo);
        assertNotNull(deviceInfo.getFunctions());
        assertTrue(deviceInfo.getFunctions().isEmpty());
        assertNotNull(deviceInfo.getVariables());
        assertTrue(deviceInfo.getVariables().isEmpty());
        assertNull(deviceInfo.getId());
        assertNull(deviceInfo.getName());
        assertNull(deviceInfo.getOwner());
        assertNull(deviceInfo.getLastIpAddress());
        assertNull(deviceInfo.getLastHeard());
        assertNull(deviceInfo.getLastHandshakeAt());
        assertEquals(0, deviceInfo.getProductId());
        assertFalse(deviceInfo.isOnline());
        assertFalse(deviceInfo.isConnected());
        assertEquals(0, deviceInfo.getPlatformId());
        assertFalse(deviceInfo.isCellular());
        assertNull(deviceInfo.getNotes());
        assertNull(deviceInfo.getSerialNumber());
        assertNull(deviceInfo.getStatus());
        assertNull(deviceInfo.getIccid());
        assertNull(deviceInfo.getLastIccid());
        assertNull(deviceInfo.getImei());
        assertNull(deviceInfo.getMacWifi());
        assertNull(deviceInfo.getSystemFirmwareVersion());
        assertNull(deviceInfo.getCurrentBuildTarget());
        assertNull(deviceInfo.getPinnedBuildTarget());
        assertNull(deviceInfo.getDefaultBuildTarget());
        assertFalse(deviceInfo.isFirmwareUpdatesEnabled());
        assertFalse(deviceInfo.isFirmwareUpdatesForced());
        assertNull(deviceInfo.getMobileSecret());
        assertEquals(0, deviceInfo.getFirmwareProductId());
        assertNotNull(deviceInfo.getGroups());
        assertTrue(deviceInfo.getGroups().isEmpty());
        assertEquals(0, deviceInfo.getFirmwareVersion());
        assertEquals(0, deviceInfo.getDesiredFirmwareVersion());
        assertEquals(0, deviceInfo.getTargetedFirmwareReleaseVersion());
        assertFalse(deviceInfo.isDevelopment());
        assertFalse(deviceInfo.isQuarantined());
        assertFalse(deviceInfo.isDenied());
        assertNull(deviceInfo.getDeviceProtectionStatus());
    }

    @Test
    public void testGettersAndSetters() {
        String id = "device1";
        String name = "Test Device";
        String owner = "owner1";
        String lastIpAddress = "192.168.1.1";
        Date lastHeard = new Date();
        Date lastHandshakeAt = new Date();
        int productId = 1234;
        boolean online = true;
        boolean connected = false;
        int platformId = 5678;
        boolean cellular = true;
        String notes = "Device notes";
        List<String> functions = new ArrayList<>();
        functions.add("func1");
        Map<String, Object> variables = new HashMap<>();
        variables.put("var1", "value1");
        String serialNumber = "SN123456";
        String status = "active";
        String iccid = "ICCID123";
        String lastIccid = "LastICCID123";
        String imei = "IMEI123";
        String macWifi = "MAC123";
        String systemFirmwareVersion = "1.0.0";
        String currentBuildTarget = "v1.1";
        String pinnedBuildTarget = "v1.0";
        String defaultBuildTarget = "v1.2";
        boolean firmwareUpdatesEnabled = true;
        boolean firmwareUpdatesForced = false;
        String mobileSecret = "secret123";
        int firmwareProductId = 9876;
        List<String> groups = new ArrayList<>();
        groups.add("group1");
        int firmwareVersion = 5;
        int desiredFirmwareVersion = 6;
        int targetedFirmwareReleaseVersion = 7;
        boolean development = true;
        boolean quarantined = false;
        boolean denied = false;
        String deviceProtectionStatus = "protected";

        deviceInfo.setId(id);
        deviceInfo.setName(name);
        deviceInfo.setOwner(owner);
        deviceInfo.setLastIpAddress(lastIpAddress);
        deviceInfo.setLastHeard(lastHeard);
        deviceInfo.setLastHandshakeAt(lastHandshakeAt);
        deviceInfo.setProductId(productId);
        deviceInfo.setOnline(online);
        deviceInfo.setConnected(connected);
        deviceInfo.setPlatformId(platformId);
        deviceInfo.setCellular(cellular);
        deviceInfo.setNotes(notes);
        deviceInfo.setFunctions(functions);
        deviceInfo.setVariables(variables);
        deviceInfo.setSerialNumber(serialNumber);
        deviceInfo.setStatus(status);
        deviceInfo.setIccid(iccid);
        deviceInfo.setLastIccid(lastIccid);
        deviceInfo.setImei(imei);
        deviceInfo.setMacWifi(macWifi);
        deviceInfo.setSystemFirmwareVersion(systemFirmwareVersion);
        deviceInfo.setCurrentBuildTarget(currentBuildTarget);
        deviceInfo.setPinnedBuildTarget(pinnedBuildTarget);
        deviceInfo.setDefaultBuildTarget(defaultBuildTarget);
        deviceInfo.setFirmwareUpdatesEnabled(firmwareUpdatesEnabled);
        deviceInfo.setFirmwareUpdatesForced(firmwareUpdatesForced);
        deviceInfo.setMobileSecret(mobileSecret);
        deviceInfo.setFirmwareProductId(firmwareProductId);
        deviceInfo.setGroups(groups);
        deviceInfo.setFirmwareVersion(firmwareVersion);
        deviceInfo.setDesiredFirmwareVersion(desiredFirmwareVersion);
        deviceInfo.setTargetedFirmwareReleaseVersion(targetedFirmwareReleaseVersion);
        deviceInfo.setDevelopment(development);
        deviceInfo.setQuarantined(quarantined);
        deviceInfo.setDenied(denied);
        deviceInfo.setDeviceProtectionStatus(deviceProtectionStatus);

        assertEquals(id, deviceInfo.getId());
        assertEquals(name, deviceInfo.getName());
        assertEquals(owner, deviceInfo.getOwner());
        assertEquals(lastIpAddress, deviceInfo.getLastIpAddress());
        assertEquals(lastHeard, deviceInfo.getLastHeard());
        assertEquals(lastHandshakeAt, deviceInfo.getLastHandshakeAt());
        assertEquals(productId, deviceInfo.getProductId());
        assertTrue(deviceInfo.isOnline());
        assertFalse(deviceInfo.isConnected());
        assertEquals(platformId, deviceInfo.getPlatformId());
        assertTrue(deviceInfo.isCellular());
        assertEquals(notes, deviceInfo.getNotes());
        assertEquals(functions, deviceInfo.getFunctions());
        assertEquals(variables, deviceInfo.getVariables());
        assertEquals(serialNumber, deviceInfo.getSerialNumber());
        assertEquals(status, deviceInfo.getStatus());
        assertEquals(iccid, deviceInfo.getIccid());
        assertEquals(lastIccid, deviceInfo.getLastIccid());
        assertEquals(imei, deviceInfo.getImei());
        assertEquals(macWifi, deviceInfo.getMacWifi());
        assertEquals(systemFirmwareVersion, deviceInfo.getSystemFirmwareVersion());
        assertEquals(currentBuildTarget, deviceInfo.getCurrentBuildTarget());
        assertEquals(pinnedBuildTarget, deviceInfo.getPinnedBuildTarget());
        assertEquals(defaultBuildTarget, deviceInfo.getDefaultBuildTarget());
        assertTrue(deviceInfo.isFirmwareUpdatesEnabled());
        assertFalse(deviceInfo.isFirmwareUpdatesForced());
        assertEquals(mobileSecret, deviceInfo.getMobileSecret());
        assertEquals(firmwareProductId, deviceInfo.getFirmwareProductId());
        assertEquals(groups, deviceInfo.getGroups());
        assertEquals(firmwareVersion, deviceInfo.getFirmwareVersion());
        assertEquals(desiredFirmwareVersion, deviceInfo.getDesiredFirmwareVersion());
        assertEquals(targetedFirmwareReleaseVersion, deviceInfo.getTargetedFirmwareReleaseVersion());
        assertTrue(deviceInfo.isDevelopment());
        assertFalse(deviceInfo.isQuarantined());
        assertFalse(deviceInfo.isDenied());
        assertEquals(deviceProtectionStatus, deviceInfo.getDeviceProtectionStatus());
    }

    @Test
    public void testEqualsAndHashCode() {
        DeviceInformation deviceInfo1 = new DeviceInformation();
        deviceInfo1.setId("device1");
        DeviceInformation deviceInfo2 = new DeviceInformation();
        deviceInfo2.setId("device1");

        assertEquals(deviceInfo1, deviceInfo2);
        assertEquals(deviceInfo1.hashCode(), deviceInfo2.hashCode());

        deviceInfo2.setId("device2");
        assertNotEquals(deviceInfo1, deviceInfo2);
        assertNotEquals(deviceInfo1.hashCode(), deviceInfo2.hashCode());
    }

    @Test
    public void testToString() {
        deviceInfo.setId("device1");
        deviceInfo.setName("Test Device");

        String toStringResult = deviceInfo.toString();
        assertTrue(toStringResult.contains("device1"));
        assertTrue(toStringResult.contains("Test Device"));
    }

    @Test
    public void testModifyCollections() {
        List<String> functions = deviceInfo.getFunctions();
        functions.add("func1");
        assertEquals(1, deviceInfo.getFunctions().size());
        assertTrue(deviceInfo.getFunctions().contains("func1"));

        Map<String, Object> variables = deviceInfo.getVariables();
        variables.put("var1", "value1");
        assertEquals(1, deviceInfo.getVariables().size());
        assertTrue(deviceInfo.getVariables().containsKey("var1"));
        assertEquals("value1", deviceInfo.getVariables().get("var1"));
    }

    @Test
    public void testModifyGroups() {
        List<String> groups = deviceInfo.getGroups();
        groups.add("group1");
        assertEquals(1, deviceInfo.getGroups().size());
        assertTrue(deviceInfo.getGroups().contains("group1"));
    }
}