package com.dynatrace.utils.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import com.dynatrace.utils.Strings;

/**
 * 
 * The HTTP Methods currently supported by {@link HttpHub}.
 *  
 * @author reinhard.pilz@dynatrace.com
 *
 */
public enum Method {

	GET, POST, PUT, DELETE, HEAD;
	
	/**
	 * Expects the given name to provide a matching value for one
	 * of the supported {@link Method}s and returns that instance.<br />
	 * <br />
	 * Evaluation is case insensitive.
	 * 
	 * @param name the {@link String} expected to provide a matching value
	 * 		for one of the supported {@link Method}s
	 * 
	 * @return a {@link Method} matching the contents of the given
	 * 		{@link InputStream}
	 * @throws IOException if reading from the given {@link InputStream} fails
	 * @throws UnsupportedMethodException if the value provided by the given
	 * 		{@link InputStream} does not match any of the supported methods.
	 */
	public static Method fromString(String name) throws IOException {
		Objects.requireNonNull(name);
		if (Strings.empty(name)) {
			throw new UnsupportedMethodException();
		}
		Method[] methods = values();
		for (Method method : methods) {
			if (method.name().toUpperCase().equals(name)){
				return method;
			}
		}
		throw new UnsupportedMethodException(name);
	}
	
}
