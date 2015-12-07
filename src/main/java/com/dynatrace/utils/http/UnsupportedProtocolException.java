package com.dynatrace.utils.http;

import java.io.IOException;

public class UnsupportedProtocolException extends IOException {

	private static final long serialVersionUID = 1L;

	public UnsupportedProtocolException() {
	}
	
	public UnsupportedProtocolException(String protocol) {
		super(protocol + " is not a supported HTTP protocol");
	}
	
}
