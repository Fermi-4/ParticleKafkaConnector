package com.github.fermi4.particle.config;

import lombok.Data;

@Data
public class HttpClientConfig {
	    private String proxyHost;
	    private int proxyPort;
	    private long connectTimeout;
	    private long writeTimeout;
	    private long readTimeout;
}
