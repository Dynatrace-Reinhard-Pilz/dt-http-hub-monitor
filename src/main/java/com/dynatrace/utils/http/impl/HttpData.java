package com.dynatrace.utils.http.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.Strings;
import com.dynatrace.utils.http.Headers;

public abstract class HttpData<T> implements Callable<T>, Closeable {
	
	private static final String HEADER_CONTENT_LENGTH = "Content-Length";
	
	/**
	 * The {@link InputStream} providing the incoming HTTP traffic coming from
	 * the HTTP Client
	 */
	private final InputStreamWrapper in;
	
	/**
	 * The {@link OutputStream} to forward the incoming HTTP traffic to
	 */
	private final OutputStream out;
	
	/**
	 * The HTTP headers sent by the HTTP client
	 */
	private final Headers headers;
	
	private final String firstLine;
	
	private final ByteArrayOutputStream body = new ByteArrayOutputStream();
	
	private final int contentLength;
	
	public HttpData(InputStream in, OutputStream out) throws IOException {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		this.in = new InputStreamWrapper(in);;
		this.out = out;
		this.firstLine = Strings.readLine(this.in).trim();
		println(firstLine, out);
		this.headers = HttpHeaders.parse(this.in);
		this.contentLength = getContentLength();
		Iterable<String> headerNames = this.headers.getNames();
		for (String headerName : headerNames) {
			Iterable<String> values = this.headers.get(headerName);
			for (String value : values) {
				String line = headerName + ": " + value;
				println(line, out);
			}
		}
		println("", out);
	}
	
	private void println(String s, OutputStream out) throws IOException {
		logger().log(Level.FINEST, "FORWARDING: " + s);
		out.write(s.getBytes("US-ASCII"));
		out.write('\r');
		out.write('\n');
		out.flush();
	}
	
	protected abstract Logger logger();
	
	protected final String getFirstLine() {
		return firstLine;
	}

	public Headers getHeaders() {
		return headers;
	}
	
	public InputStream getBody() {
		return new ByteArrayInputStream(body.toByteArray());
	}
	
	protected boolean containsBody() {
		return contentLength != -1;
	}
	
	protected final int getContentLength() {
		String sContentLength = headers.getFirst(HEADER_CONTENT_LENGTH);
		if (sContentLength == null) {
			return -1;
		}
		try {
			int contentLength = Integer.parseInt(sContentLength);
			logger().log(Level.FINEST, HEADER_CONTENT_LENGTH + ": " + contentLength);
			return contentLength;
		} catch (Throwable t) {
			logger().log(Level.WARNING, "Invalid Content-Length Header '" + sContentLength + "'", t);
			return -1;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T call() throws Exception {
		logger().log(Level.FINER, headers.toString());
		if ((contentLength != -1) && containsBody()) {
			Closeables.copy(in, contentLength, out, body);
			out.flush();
		} else {
			out.flush();
		}
		return (T) this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
		logger().log(Level.FINER, "shutting down");
		Closeables.close(in.getInputStream());
		Closeables.close(out);
		logger().log(Level.FINER, "closed");
	}	

}
