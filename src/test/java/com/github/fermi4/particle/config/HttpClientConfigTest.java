package com.github.fermi4.particle.config;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class HttpClientConfigTest {

    @Test
    public void testDefaultConstructor() {
        HttpClientConfig config = new HttpClientConfig();

        assertNull(config.getProxyHost());
        assertEquals(0, config.getProxyPort());
        assertEquals(0L, config.getConnectTimeout());
        assertEquals(0L, config.getWriteTimeout());
        assertEquals(0L, config.getReadTimeout());
    }

    @Test
    public void testAllSettersAndGetters() {
        HttpClientConfig config = new HttpClientConfig();
        config.setProxyHost("proxy.example.com");
        config.setProxyPort(8080);
        config.setConnectTimeout(30000L);
        config.setWriteTimeout(15000L);
        config.setReadTimeout(20000L);

        assertEquals("proxy.example.com", config.getProxyHost());
        assertEquals(8080, config.getProxyPort());
        assertEquals(30000L, config.getConnectTimeout());
        assertEquals(15000L, config.getWriteTimeout());
        assertEquals(20000L, config.getReadTimeout());
    }

    @Test
    public void testEquals() {
        HttpClientConfig config1 = new HttpClientConfig();
        config1.setProxyHost("proxy.example.com");
        config1.setProxyPort(8080);
        config1.setConnectTimeout(30000L);
        config1.setWriteTimeout(15000L);
        config1.setReadTimeout(20000L);

        HttpClientConfig config2 = new HttpClientConfig();
        config2.setProxyHost("proxy.example.com");
        config2.setProxyPort(8080);
        config2.setConnectTimeout(30000L);
        config2.setWriteTimeout(15000L);
        config2.setReadTimeout(20000L);

        assertEquals(config1, config2);
    }

    @Test
    public void testNotEquals() {
        HttpClientConfig config1 = new HttpClientConfig();
        config1.setProxyHost("proxy.example.com");
        config1.setProxyPort(8080);
        config1.setConnectTimeout(30000L);
        config1.setWriteTimeout(15000L);
        config1.setReadTimeout(20000L);

        HttpClientConfig config2 = new HttpClientConfig();
        config2.setProxyHost("proxy.example.com");
        config2.setProxyPort(9090); // Different port
        config2.setConnectTimeout(30000L);
        config2.setWriteTimeout(15000L);
        config2.setReadTimeout(20000L);

        assertNotEquals(config1, config2);
    }

    @Test
    public void testHashCode() {
        HttpClientConfig config1 = new HttpClientConfig();
        config1.setProxyHost("proxy.example.com");
        config1.setProxyPort(8080);
        config1.setConnectTimeout(30000L);
        config1.setWriteTimeout(15000L);
        config1.setReadTimeout(20000L);

        HttpClientConfig config2 = new HttpClientConfig();
        config2.setProxyHost("proxy.example.com");
        config2.setProxyPort(8080);
        config2.setConnectTimeout(30000L);
        config2.setWriteTimeout(15000L);
        config2.setReadTimeout(20000L);

        assertEquals(config1.hashCode(), config2.hashCode());
    }

    @Test
    public void testHashCodeNotEquals() {
        HttpClientConfig config1 = new HttpClientConfig();
        config1.setProxyHost("proxy.example.com");
        config1.setProxyPort(8080);
        config1.setConnectTimeout(30000L);
        config1.setWriteTimeout(15000L);
        config1.setReadTimeout(20000L);

        HttpClientConfig config2 = new HttpClientConfig();
        config2.setProxyHost("proxy.example.com");
        config2.setProxyPort(9090); // Different port
        config2.setConnectTimeout(30000L);
        config2.setWriteTimeout(15000L);
        config2.setReadTimeout(20000L);

        assertNotEquals(config1.hashCode(), config2.hashCode());
    }

    @Test
    public void testToString() {
        HttpClientConfig config = new HttpClientConfig();
        config.setProxyHost("proxy.example.com");
        config.setProxyPort(8080);
        config.setConnectTimeout(30000L);
        config.setWriteTimeout(15000L);
        config.setReadTimeout(20000L);
        String expected = "HttpClientConfig [proxyHost=proxy.example.com, proxyPort=8080, connectTimeout=30000, writeTimeout=15000, readTimeout=20000]";
        System.out.println(config.toString());
        assertEquals(expected, config.toString());
    }

    @Test
    public void testToStringWithNullValues() {
        HttpClientConfig config = new HttpClientConfig();
        config.setProxyHost(null);
        config.setProxyPort(0);
        config.setConnectTimeout(0L);
        config.setWriteTimeout(0L);
        config.setReadTimeout(0L);
        String expected = "HttpClientConfig [proxyHost=null, proxyPort=0, connectTimeout=0, writeTimeout=0, readTimeout=0]";
        System.out.println(config.toString());
        assertEquals(expected, config.toString());
    }
}
