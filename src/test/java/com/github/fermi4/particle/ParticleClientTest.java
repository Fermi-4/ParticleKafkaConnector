package com.github.fermi4.particle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fermi4.particle.api.ParticleClient;
import com.github.fermi4.particle.api.domain.DeviceInformation;
import com.github.fermi4.particle.api.domain.resource.ProductApiResponse;
import com.github.fermi4.particle.api.http.ParticleApiFactory;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ParticleClientTest {

    @Mock
    private OkHttpClient mockClient;

    @Mock
    private ParticleApiFactory mockEndpointFactory;

    @Mock
    private ObjectMapper mockMapper;

    @Mock
    private Call mockCall;

    @Mock
    private Response mockResponse;

    @InjectMocks
    private ParticleClient particleClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        particleClient = ParticleClient.builder()
                .client(mockClient)
                .accessToken("test-token")
                .mapper(mockMapper)
                .endpointFactory(mockEndpointFactory)
                .build();
    }

    @Test
    public void testGetDeviceInformation() throws IOException {
        String deviceId = "test-device-id";
        DeviceInformation mockDeviceInfo = new DeviceInformation();
        HttpUrl mockUrl = mock(HttpUrl.class);
        Call mockCall = mock(Call.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);

        when(mockEndpointFactory.getDeviceInformation("test-token", deviceId)).thenReturn(mockUrl);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn("{\"id\":\"test-device-id\"}");
        when(mockMapper.readValue(anyString(), eq(DeviceInformation.class))).thenReturn(mockDeviceInfo);

        DeviceInformation deviceInfo = particleClient.getDeviceInformation(deviceId);

        assertEquals(mockDeviceInfo, deviceInfo);
    }

    @Test
    public void testGetDeviceInformationThrowsException() throws IOException {
        String deviceId = "test-device-id";
        HttpUrl mockUrl = mock(HttpUrl.class);

        when(mockEndpointFactory.getDeviceInformation("test-token", deviceId)).thenReturn(mockUrl);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockMapper.readValue(anyString(), eq(Response.class))).thenReturn(mockResponse);




        assertThrows(IOException.class, () -> particleClient.getDeviceInformation(deviceId));
    }

    @Test
    public void testListAllDevices() throws IOException {



        DeviceInformation[] mockDeviceArray = new DeviceInformation[]{ new DeviceInformation() };
        HttpUrl mockUrl = mock(HttpUrl.class);

        when(mockEndpointFactory.listAllDevices("test-token")).thenReturn(mockUrl);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(ResponseBody.create("[{\"id\":\"test-device-id\"}]", MediaType.get("application/json")));
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockMapper.readValue(anyString(), eq(DeviceInformation[].class))).thenReturn(mockDeviceArray);

        List<DeviceInformation> devices = particleClient.listAllDevices();

        assertEquals(1, devices.size());
    }

    @Test
    public void testListAllDevicesThrowsException() throws IOException {
        HttpUrl mockUrl = mock(HttpUrl.class);

        when(mockEndpointFactory.listAllDevices("test-token")).thenReturn(mockUrl);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockMapper.readValue(anyString(), eq(Response.class))).thenReturn(mockResponse);

        assertThrows(IOException.class, () -> particleClient.listAllDevices());
    }

    @Test
    public void testListAllProducts() throws IOException {
        ProductApiResponse mockProductResponse = new ProductApiResponse();
        HttpUrl mockUrl = mock(HttpUrl.class);

        when(mockEndpointFactory.listAllProducts("test-token")).thenReturn(mockUrl);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(ResponseBody.create("{\"products\":[]}", MediaType.get("application/json")));
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockMapper.readValue(anyString(), eq(ProductApiResponse.class))).thenReturn(mockProductResponse);

        ProductApiResponse productResponse = particleClient.listAllProducts();

        assertEquals(mockProductResponse, productResponse);
    }

    @Test
    public void testListAllProductsThrowsException() throws IOException {
        HttpUrl mockUrl = mock(HttpUrl.class);

        when(mockEndpointFactory.listAllProducts("test-token")).thenReturn(mockUrl);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockMapper.readValue(anyString(), eq(Response.class))).thenReturn(mockResponse);

        assertThrows(IOException.class, () -> particleClient.listAllProducts());
    }
}
