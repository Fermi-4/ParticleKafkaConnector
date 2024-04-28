package com.github.fermi4.particle.api.serde;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.fermi4.particle.api.domain.DeviceInformation;
import com.github.fermi4.particle.api.domain.resource.DeviceApiResponse;

// TODO: rename this class and write test to verify that it works.. rename domain object
public class DeviceApiResponseDeserializer extends StdDeserializer<DeviceApiResponse> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DeviceApiResponseDeserializer() {
        this(null);
    }
    public DeviceApiResponseDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public DeviceApiResponse deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JacksonException {
                
            DeviceApiResponse response = new DeviceApiResponse();

            JsonNode node = jp.getCodec().readTree(jp);
            List<DeviceInformation> deviceInformation = new ArrayList<>();
            
            if(node.has("devices")) {
                ObjectMapper objectMapper = new ObjectMapper();
                SimpleModule module = new SimpleModule();
                module.addDeserializer(DeviceInformation.class, new DeviceInformationDeserializer());
                objectMapper.registerModule(module);

                JsonNode devicesNode = node.get("devices");
                for(JsonNode device : devicesNode) {
                    DeviceInformation deviceInfo = objectMapper.readValue(device.traverse(jp.getCodec()), DeviceInformation.class);
                    if (deviceInfo != null) {
                        deviceInformation.add(deviceInfo);
                    }
                }
            }
            response.setDevices(deviceInformation);

            if (node.has("meta")) {
                JsonNode metaNode = node.get("meta");
                if (metaNode.has("total_pages")) {
                    response.setTotalPages(metaNode.get("total_pages").asInt());
                }
            }

            List<DeviceApiResponse.Customer> customers = new ArrayList<>();
            if (node.has("customers")) {
                ObjectMapper objectMapper = new ObjectMapper();
                for(JsonNode customerNode : node.get("customers")) {
                    customers.add(objectMapper.readValue(customerNode.traverse(jp.getCodec()), DeviceApiResponse.Customer.class));
                }
            }
            response.setCustomers(customers);
            return response;

    }    
}
