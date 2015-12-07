package com.dynatrace.utils.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import com.dynatrace.utils.Strings;
import com.dynatrace.utils.http.Headers;

public class HttpHeaders implements Headers {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(HttpHeaders.class.getName());

	private final Map<String, List<String>> headers = new HashMap<>();

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public Iterable<String> get(String name) {
		Objects.requireNonNull(name);
		String uCaseName = name.toUpperCase();
		Set<String> keys = headers.keySet();
		for (String key : keys) {
			if (uCaseName.equals(key.toUpperCase())) {
				return headers.get(key);
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public String getFirst(String name) {
		Objects.requireNonNull(name);
		Iterable<String> values = get(name);
		if (values == null) {
			return null;
		}
		Iterator<String> it = values.iterator();
		if (it.hasNext()) {
			return it.next();
		}
		return null;
	}
	
	private void put(String name, String value) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(value);
		List<String> values = headers.get(name);
		if (values == null) {
			values = new ArrayList<>();
			headers.put(name, values);
		}
		values.add(value);
	}
	
	private static void put(HttpHeaders headers, String line) throws IOException {
		int idx = line.indexOf(':');
		if (idx == -1) {
			throw new IOException("Invalid header line '" + line + "'");
		}
		if (idx == line.length() - 1) {
			throw new IOException("Invalid header line '" + line + "'");
		}
		String name = line.substring(0, idx).trim();
		String value = line.substring(idx + 1).trim();
		headers.put(name, value);
	}
	
	
	public static Headers parse(InputStream in) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		String line = null;
		while (!(line = Strings.readLine(in).trim()).isEmpty()) {
			put(headers, line);
		}
		return headers;
	}
	
	public Iterable<String> getNames() {
		return headers.keySet();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName() + "[");
		Set<String> keys = headers.keySet();
		for (String key : keys) {
			List<String> values = headers.get(key);
			for (String value : values) {
				sb.append(" " + key + ": \"" + value + "\"");
			}
		}
		sb.append(" ]");
		return sb.toString();
	}
	
	
}
