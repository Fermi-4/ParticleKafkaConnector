package com.github.fermi4.particle.config;

public class HttpClientConfig {
	private String proxyHost;
	private int proxyPort;
	private long connectTimeout;
	private long writeTimeout;
	private long readTimeout;

	public HttpClientConfig() {
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public long getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(long connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public long getWriteTimeout() {
		return writeTimeout;
	}

	public void setWriteTimeout(long writeTimeout) {
		this.writeTimeout = writeTimeout;
	}

	public long getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(long readTimeout) {
		this.readTimeout = readTimeout;
	}

	@Override
	public String toString() {
		return "HttpClientConfig [proxyHost=" + proxyHost + ", proxyPort=" + proxyPort + ", connectTimeout="
				+ connectTimeout + ", writeTimeout=" + writeTimeout + ", readTimeout=" + readTimeout
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((proxyHost == null) ? 0 : proxyHost.hashCode());
		result = prime * result + proxyPort;
		result = prime * result + (int) (connectTimeout ^ (connectTimeout >>> 32));
		result = prime * result + (int) (writeTimeout ^ (writeTimeout >>> 32));
		result = prime * result + (int) (readTimeout ^ (readTimeout >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HttpClientConfig other = (HttpClientConfig) obj;
		if (proxyHost == null) {
			if (other.proxyHost != null)
				return false;
		} else if (!proxyHost.equals(other.proxyHost))
			return false;
		if (proxyPort != other.proxyPort)
			return false;
		if (connectTimeout != other.connectTimeout)
			return false;
		if (writeTimeout != other.writeTimeout)
			return false;
		if (readTimeout != other.readTimeout)
			return false;
		return true;
	}

}
