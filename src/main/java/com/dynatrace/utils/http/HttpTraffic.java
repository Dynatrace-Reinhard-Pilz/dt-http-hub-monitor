package com.dynatrace.utils.http;

/**
 * Captured data for intercepted HTTP traffic
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public interface HttpTraffic {

	/**
	 * @return an object holding the request details of the intercepted
	 * 		HTTP traffic
	 */
	Request getRequest();
	
	/**
	 * @return an object holding the response details of the intercepted
	 * 		HTTP traffic
	 */
	Response getResponse();
}
