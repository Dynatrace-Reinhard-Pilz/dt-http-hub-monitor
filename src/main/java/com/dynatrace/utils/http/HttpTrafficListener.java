package com.dynatrace.utils.http;

/**
 * Implementations of {@link HttpTrafficListener} are getting provided the
 * captured details of forwarded HTTP Traffic by a running {@link HttpHub}.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public interface HttpTrafficListener {

	/**
	 * Notifies the listener that HTTP traffic has happened
	 * 
	 * @param traffic holding details about the HTTP traffic
	 */
	void handle(HttpTraffic traffic);
	
}
