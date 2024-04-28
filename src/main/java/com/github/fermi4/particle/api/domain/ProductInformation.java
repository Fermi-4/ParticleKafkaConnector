package com.github.fermi4.particle.api.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ProductInformation {
    private int id;
    private int platformId;
    private String name;
    private String slugOrProductId;
    private String description;
    private int subscriptionId;
    private String user;
    private List<String> groups = new ArrayList<>();
    private Map<String, Object> settings = new HashMap<>();
}
