package com.github.fermi4.particle.api.domain.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.api.domain.DeviceInformation;
import com.github.fermi4.particle.api.domain.resource.DeviceApiResponse.Customer;

public class DeviceApiResponseTest {

    private DeviceApiResponse response;
    private DeviceInformation deviceInfo;
    private Customer customer;

    @BeforeEach
    public void setUp() {
        response = new DeviceApiResponse();
        deviceInfo = new DeviceInformation();
        customer = new Customer();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(response);
        assertEquals(0, response.getCurrentPage());
        assertEquals(0, response.getTotalPages());
        assertNotNull(response.getDevices());
        assertTrue(response.getDevices().isEmpty());
        assertNotNull(response.getCustomers());
        assertTrue(response.getCustomers().isEmpty());
    }

    @Test
    public void testSettersAndGetters() {
        int currentPage = 1;
        int totalPages = 5;
        List<DeviceInformation> devices = new ArrayList<>();
        devices.add(deviceInfo);

        List<Customer> customers = new ArrayList<>();
        customers.add(customer);

        response.setCurrentPage(currentPage);
        response.setTotalPages(totalPages);
        response.setDevices(devices);
        response.setCustomers(customers);

        assertEquals(currentPage, response.getCurrentPage());
        assertEquals(totalPages, response.getTotalPages());
        assertEquals(devices, response.getDevices());
        assertEquals(customers, response.getCustomers());
    }

    @Test
    public void testModifyCollections() {
        deviceInfo.setId("device1");
        response.getDevices().add(deviceInfo);

        assertEquals(1, response.getDevices().size());
        assertEquals("device1", response.getDevices().get(0).getId());

        customer.setId("customer1");
        customer.setUsername("username1");
        response.getCustomers().add(customer);

        assertEquals(1, response.getCustomers().size());
        assertEquals("customer1", response.getCustomers().get(0).getId());
        assertEquals("username1", response.getCustomers().get(0).getUsername());
    }

    @Test
    public void testCustomerSettersAndGetters() {
        String id = "customer1";
        String username = "username1";

        customer.setId(id);
        customer.setUsername(username);

        assertEquals(id, customer.getId());
        assertEquals(username, customer.getUsername());
    }

    @Test
    public void testCustomerToString() {
        customer.setId("customer1");
        customer.setUsername("username1");

        String toStringResult = customer.toString();
        assertTrue(toStringResult.contains("customer1"));
        assertTrue(toStringResult.contains("username1"));
    }

    @Test
    public void testCustomerEqualsAndHashCode() {
        Customer customer1 = new Customer();
        customer1.setId("customer1");
        customer1.setUsername("username1");

        Customer customer2 = new Customer();
        customer2.setId("customer1");
        customer2.setUsername("username1");

        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());

        customer2.setId("customer2");
        assertNotEquals(customer1, customer2);
        assertNotEquals(customer1.hashCode(), customer2.hashCode());
    }

    @Test
    public void testDeviceApiResponseToString() {
        response.setCurrentPage(1);
        response.setTotalPages(5);

        deviceInfo.setId("device1");
        response.getDevices().add(deviceInfo);

        customer.setId("customer1");
        response.getCustomers().add(customer);

        String toStringResult = response.toString();
        assertTrue(toStringResult.contains("currentPage=1"));
        assertTrue(toStringResult.contains("totalPages=5"));
        assertTrue(toStringResult.contains("device1"));
        assertTrue(toStringResult.contains("customer1"));
    }

    @Test
    public void testDeviceApiResponseEqualsAndHashCode() {
        DeviceApiResponse response1 = new DeviceApiResponse();
        DeviceApiResponse response2 = new DeviceApiResponse();

        response1.setCurrentPage(1);
        response1.setTotalPages(2);
        deviceInfo.setId("device1");
        response1.getDevices().add(deviceInfo);
        customer.setId("customer1");
        response1.getCustomers().add(customer);

        response2.setCurrentPage(1);
        response2.setTotalPages(2);
        DeviceInformation deviceInfo2 = new DeviceInformation();
        deviceInfo2.setId("device1");
        response2.getDevices().add(deviceInfo2);
        Customer customer2 = new Customer();
        customer2.setId("customer1");
        response2.getCustomers().add(customer2);

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());

        response2.setCurrentPage(3);
        assertNotEquals(response1, response2);
        assertNotEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    public void testAddDevicesAndCustomers() {
        DeviceInformation device1 = new DeviceInformation();
        device1.setId("device1");
        DeviceInformation device2 = new DeviceInformation();
        device2.setId("device2");

        response.getDevices().add(device1);
        response.getDevices().add(device2);

        assertEquals(2, response.getDevices().size());
        assertEquals("device1", response.getDevices().get(0).getId());
        assertEquals("device2", response.getDevices().get(1).getId());

        Customer customer1 = new Customer();
        customer1.setId("customer1");
        Customer customer2 = new Customer();
        customer2.setId("customer2");

        response.getCustomers().add(customer1);
        response.getCustomers().add(customer2);

        assertEquals(2, response.getCustomers().size());
        assertEquals("customer1", response.getCustomers().get(0).getId());
        assertEquals("customer2", response.getCustomers().get(1).getId());
    }

    @Test
    public void testRemoveDevicesAndCustomers() {
        DeviceInformation device1 = new DeviceInformation();
        device1.setId("device1");
        response.getDevices().add(device1);

        response.getDevices().remove(device1);
        assertTrue(response.getDevices().isEmpty());

        Customer customer1 = new Customer();
        customer1.setId("customer1");
        response.getCustomers().add(customer1);

        response.getCustomers().remove(customer1);
        assertTrue(response.getCustomers().isEmpty());
    }
}
