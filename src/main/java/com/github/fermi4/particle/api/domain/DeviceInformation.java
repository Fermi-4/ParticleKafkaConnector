package com.github.fermi4.particle.api.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class DeviceInformation {
    private String id;
    private String name;
    private String owner;
    private String lastIpAddress;
    private Date lastHeard;
    private Date lastHandshakeAt;
    private int productId;
    private boolean online;
    private boolean connected;
    private int platformId;
    private boolean cellular;
    private String notes;
    private List<String> functions = new ArrayList<>();
    private Map<String, Object> variables = new HashMap<>();
    private String serialNumber;
    private String status;
    private String iccid;
    private String lastIccid;
    private String imei;
    private String macWifi;
    private String systemFirmwareVersion;
    private String currentBuildTarget;
    private String pinnedBuildTarget;
    private String defaultBuildTarget;
    private boolean firmwareUpdatesEnabled;
    private boolean firmwareUpdatesForced;
    private String mobileSecret;
    private int firmwareProductId;
    private List<String> groups = new ArrayList<>();
    private int firmwareVersion;
    private int desiredFirmwareVersion;
    private int targetedFirmwareReleaseVersion;
    private boolean development;
    private boolean quarantined;
    private boolean denied;
    private String deviceProtectionStatus;
}
