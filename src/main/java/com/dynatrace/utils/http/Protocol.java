package com.dynatrace.utils.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import com.dynatrace.utils.Strings;

/**
 * HTTP protocols currently supported by {@link HttpHub}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public enum Protocol {
	
	HTTP11("HTTP/1.1"), HTTP10("HTTP/1.0");
	
	/**
	 * the value expected within HTTP traffic
	 */
	private final String id;
	
	/**
	 * c'tor
	 * 
	 * @param id the value expected within HTTP traffic
	 */
	private Protocol(String id) {
		Objects.requireNonNull(id);
		this.id = id;
	}
	
	/**
	 * Expects to get either {@code HTTP/1.1} or {@code HTTP/1.0} provided by
	 * the given {@link String} and returns the matching {@link Protocol}
	 * for that value.<br />
	 * 
	 * 
	 * @param in the {@link InputStream} expected to provide the protocol
	 * 
	 * @return either {@link Protocol.HTTP11} or {@link Protocol.HTTP10},
	 * 		depending on the contents delivered by the given {@link InputStream}
	 * 
	 * @throws IOException if evaluating the contents of the given
	 * 		{@link InputStream} fails
	 * @throws UnsupportedProtocolException if the contents of the given
	 * 		{@link InputStream} do not represent any of the supported protocols
	 */
	public static Protocol fromString(String name) throws IOException {
		if (Strings.empty(name)) {
			throw new UnsupportedProtocolException();
		}
		for (Protocol protocol : values()) {
			if (protocol.id.equals(name)) {
				return protocol;
			}
		}
		throw new UnsupportedProtocolException(name);
	}
}
