package com.dynatrace.utils.http;

import java.io.InputStream;

/**
 * Represents captured HTTP response traffic
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public interface Response {

	/**
	 * @return the HTTP headers received by the HTTP Server
	 */
	Headers getHeaders();
	
	/**
	 * @return the HTTP Response body
	 */
	InputStream getBody();
}
