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
import com.github.fermi4.particle.api.domain.ProductInformation;
import com.github.fermi4.particle.api.domain.resource.ProductApiResponse;

public class ProductApiResponseDeserializer extends StdDeserializer<ProductApiResponse> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProductApiResponseDeserializer() {
        this(null);
    }
    public ProductApiResponseDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ProductApiResponse deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JacksonException {
                
            ProductApiResponse response = new ProductApiResponse();

            JsonNode node = jp.getCodec().readTree(jp);
            List<ProductInformation> productInformation = new ArrayList<>();
            
            if(node.has("products")) {
                ObjectMapper objectMapper = new ObjectMapper();
                SimpleModule module = new SimpleModule();
                module.addDeserializer(ProductInformation.class, new ProductInformationDeserializer());
                objectMapper.registerModule(module);

                JsonNode productsNode = node.get("products");
                for(JsonNode product : productsNode) {
                    ProductInformation productInfo = objectMapper.readValue(product.traverse(jp.getCodec()), ProductInformation.class);
                    if (productInfo != null) {
                        productInformation.add(productInfo);
                    }
                }
            }
            response.setProducts(productInformation);

            return response;

    }    
}
