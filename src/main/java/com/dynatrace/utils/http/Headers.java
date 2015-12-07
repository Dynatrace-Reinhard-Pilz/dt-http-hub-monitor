package com.dynatrace.utils.http;

/**
 * Provides HTTP request or response headers
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public interface Headers {

	/**
	 * Returns the captured headers for the given name
	 * 
	 * @param name the name of the HTTP header
	 * 
	 * @return the captured headers for the given name or {@code null} if
	 * 		no such headers have been captured
	 */
	Iterable<String> get(String name);
	
	/**
	 * Returns the first header captured for the given name
	 * 
	 * @param name the name of the HTTP header
	 * 
	 * @return the first header captured for the given name or {@code null} if
	 * 		no such header has been captured
	 */
	String getFirst(String name);
	
	Iterable<String> getNames();
}
