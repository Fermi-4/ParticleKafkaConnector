package com.github.fermi4.particle.api.serde;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.fermi4.particle.api.domain.ProductInformation;

public class ProductInformationDeserializer extends StdDeserializer<ProductInformation> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProductInformationDeserializer() {
        this(null);
    }

    public ProductInformationDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ProductInformation deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        ProductInformation productInformation = new ProductInformation();

        if (node.has("id"))
            productInformation.setId(node.get("id").asInt());
        if (node.has("platform_id"))
            productInformation.setPlatformId(node.get("platform_id").asInt());
        if (node.has("name"))
            productInformation.setName(node.get("name").asText());
        if (node.has("slug"))
            productInformation.setSlugOrProductId(node.get("slug").asText());
        if (node.has("description"))
            productInformation.setDescription(node.get("description").asText());
        if (node.has("subscription_id"))
            productInformation.setSubscriptionId(node.get("subscription_id").asInt());
        if (node.has("user"))
            productInformation.setUser(node.get("user").asText());
        if (node.has("groups")) {
            List<String> groups = new ArrayList<>();
            JsonNode groupsNode = node.get("groups");
            if (groupsNode.isArray()) {
                for (JsonNode groupNode : groupsNode) {
                    groups.add(groupNode.asText());
                }
            }
            productInformation.setGroups(groups);
        }

        if (node.has("settings")) {
            JsonNode settingsNode = node.get("settings");
            if (settingsNode.isObject()) {
                // Deserialize settings into HashMap<String, Object>
                Map<String, Object> settingsMap = new HashMap<>();
                deserializeSettings(settingsNode, settingsMap);
                productInformation.setSettings(settingsMap);
            }
        }

        return productInformation;
    }

    private void deserializeSettings(JsonNode settingsNode, Map<String, Object> settingsMap) {
        settingsNode.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode valueNode = entry.getValue();
            if (valueNode.isObject()) {
                // If value is an object, recursively deserialize it
                Map<String, Object> nestedMap = new HashMap<>();
                deserializeSettings(valueNode, nestedMap);
                settingsMap.put(key, nestedMap);
            } else {
                // Otherwise, add the value directly
                settingsMap.put(key, valueNode.asText());
            }
        });
    }
}
