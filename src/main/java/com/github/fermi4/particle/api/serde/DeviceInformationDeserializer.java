package com.github.fermi4.particle.api.serde;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.fermi4.particle.api.domain.DeviceInformation;

public class DeviceInformationDeserializer extends StdDeserializer<DeviceInformation> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DeviceInformationDeserializer() {
        this(null);
    }

    public DeviceInformationDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public DeviceInformation deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        DeviceInformation device = new DeviceInformation();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        
        if(node.has("last_heard")) {
            try {
                Date lastHeard = dateFormat.parse(node.get("last_heard").asText());
                device.setLastHeard(lastHeard);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        if (node.has("last_handshake_at")) {
            try {
                Date lastHandshakeAt = dateFormat.parse(node.get("last_handshake_at").asText());
                device.setLastHandshakeAt(lastHandshakeAt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (node.has("functions")) {
            List<String> functions = new ArrayList<>();
            JsonNode functionsNode = node.get("functions");
            if (functionsNode.isArray()) {
                for (JsonNode functionNode : functionsNode) {
                    functions.add(functionNode.asText());
                }
            }
            device.setFunctions(functions);
        }

        if (node.has("variables")) {
            JsonNode variablesNode = node.get("variables");
            if (variablesNode.isObject()) {
                Iterator<String> variableNames = variablesNode.fieldNames();
                while (variableNames.hasNext()) {
                    String variableName = variableNames.next();
                    device.getVariables().put(variableName, variablesNode.get(variableName).asText());
                }
            }
        }

        if(node.has("id"))
            device.setId(node.get("id").asText());
        if(node.has("name"))
            device.setName(node.get("name").asText());
        if(node.has("owner"))
            device.setOwner(node.get("owner").asText());
        if(node.has("last_ip_address"))
            device.setLastIpAddress(node.get("last_ip_address").asText());
        if (node.has("product_id"))
            device.setProductId(node.get("product_id").asInt());
        if (node.has("online"))
            device.setOnline(node.get("online").asBoolean());
        if (node.has("platform_id"))
            device.setPlatformId(node.get("platform_id").asInt());
        if (node.has("cellular"))
            device.setCellular(node.get("cellular").asBoolean());
        if (node.has("online"))
            device.setOnline(node.get("online").asBoolean());
        if (node.has("notes") && !node.get("notes").isNull())
            device.setNotes(node.get("notes").asText());
        if (node.has("status"))
            device.setStatus(node.get("status").asText());
        if (node.has("serial_number"))
            device.setSerialNumber(node.get("serial_number").asText());
        if (node.has("iccid"))
            device.setIccid(node.get("iccid").asText());
        if (node.has("imei"))
            device.setImei(node.get("imei").asText());
        if (node.has("mac_wifi"))
            device.setMacWifi(node.get("mac_wifi").asText());
        if (node.has("mobile_secret"))
            device.setMobileSecret(node.get("mobile_secret").asText());
        if (node.has("system_firmware_version"))
            device.setSystemFirmwareVersion(node.get("system_firmware_version").asText());
        if (node.has("firmware_updates_enabled"))
            device.setFirmwareUpdatesEnabled(node.get("firmware_updates_enabled").asBoolean());
        if (node.has("firmware_updates_forced"))
            device.setFirmwareUpdatesForced(node.get("firmware_updates_forced").asBoolean());
        if (node.has("device_protection")) {
            JsonNode deviceProtectionNode = node.get("device_protection");
            if (deviceProtectionNode.has("status"))
                device.setDeviceProtectionStatus(deviceProtectionNode.get("status").asText());
        }

        return device;
    }
}
