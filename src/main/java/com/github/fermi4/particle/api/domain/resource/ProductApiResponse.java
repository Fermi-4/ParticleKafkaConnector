package com.github.fermi4.particle.api.domain.resource;

import java.util.ArrayList;
import java.util.List;

import com.github.fermi4.particle.api.domain.ProductInformation;

import lombok.Data;

@Data
public class ProductApiResponse {
    List<ProductInformation> products = new ArrayList<>();
}
