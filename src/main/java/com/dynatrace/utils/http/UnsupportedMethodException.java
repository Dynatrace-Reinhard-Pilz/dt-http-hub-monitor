package com.dynatrace.utils.http;

import java.io.IOException;

public class UnsupportedMethodException extends IOException {

	private static final long serialVersionUID = 1L;

	public UnsupportedMethodException() {
	}
	
	public UnsupportedMethodException(String method) {
		super(method + " is not a supported HTTP method");
	}
	
}
