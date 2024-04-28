package com.github.fermi4.particle.api.domain.resource;

import java.util.ArrayList;
import java.util.List;

import com.github.fermi4.particle.api.domain.DeviceInformation;

import lombok.Data;

@Data
public class DeviceApiResponse {

    int currentPage;
    int totalPages;
    List<DeviceInformation> devices = new ArrayList<>();
    List<Customer> customers = new ArrayList<>();

    @Data
    public static class Customer {
        private String id;
        private String username;
    }
}
