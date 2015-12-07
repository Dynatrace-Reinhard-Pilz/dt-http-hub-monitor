package com.dynatrace.test.http;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class Request {
	
	private final InetSocketAddress address;
	private final Map<String, List<String>> headers;
	private final byte[] content;
	private final String method;
	private final URI uri;
	
	public Request(String method, URI uri, Map<String, List<String>> headers, InetSocketAddress address, byte[] content) {
		this.method = method;
		this.uri = uri;
		this.headers = headers;
		this.address = address;
		this.content = content;
	}
	
	public InetSocketAddress getAddress() {
		return address;
	}
	
	public Map<String, List<String>> getHeaders() {
		return headers;
	}
	
	public byte[] getContent() {
		return content;
	}
	
	public String getMethod() {
		return method;
	}
	
	public URI getUri() {
		return uri;
	}
	
	@Override
	public String toString() {
		return method + " " + uri;
	}

}
