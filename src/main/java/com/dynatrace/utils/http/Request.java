package com.dynatrace.utils.http;

import java.net.URI;

/**
 * The request part of HTTP traffic which has been forwarded
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public interface Request {

	/**
	 * @return the HTTP method of the HTTP request
	 */
	Method getMethod();
	
	/**
	 * @return the URI (without protocol, host or port)
	 */
	URI getURI();
	
	/**
	 * @return the HTTP protocol ({@code HTTP1.0} or {@code HTTP1.1}) 
	 */
	Protocol getProtocol();
	
	/**
	 * @return the HTTP headers sent 
	 */
	Headers getHeaders();
}
