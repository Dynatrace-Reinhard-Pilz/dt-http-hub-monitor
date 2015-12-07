package com.dynatrace.utils.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import com.dynatrace.utils.http.HttpHub;
import com.dynatrace.utils.http.Protocol;
import com.dynatrace.utils.http.Response;

/**
 * Passes through HTTP response traffic of HTTP requests intercepted by a
 * {@link HttpHub}.<br />
 * <br />
 * The HTTP traffic is being forwarded unmodified to a given
 * {@link OutputStream} upon execution of this {@link Thread}.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public final class HttpResponse extends HttpData<Response> implements Response {
	
	private static final Logger LOGGER =
			Logger.getLogger(HttpResponse.class.getName());
	
	private final Protocol protocol;
	private final int status;
	private final String message;
	
	/**
	 * c'tor
	 * 
	 * @param in the {@link InputStream} providing the HTTP response traffic
	 * 		to forward. Once handed over this {@link InputStream} may already
	 * 		be at least partially consumed. Callers should not read from it
	 * 		anymore.
	 * @param out the {@link OutputStream} to forward the HTTP response traffic
	 * 		to
	 * 
	 * @throws IOException in case evaluating response data fails
	 */
	public HttpResponse(InputStream in, OutputStream out) throws IOException {
		super(in, out);
		String firstLine = getFirstLine().trim();
		int idx = 0;
		idx = firstLine.indexOf(' ');
		if (idx == -1) {
			throw new IOException("protocol given");
		}
		this.protocol = Protocol.fromString(firstLine.substring(0, idx));
		firstLine = firstLine.substring(idx + 1);
		idx = firstLine.indexOf(' ');
		if (idx == -1) {
			status = Integer.parseInt(firstLine.trim());
			message = null;
		} else {
			status = Integer.parseInt(firstLine.substring(0, idx).trim());
			message = firstLine.substring(idx + 1).trim();
		}
	}
	
	public Protocol getProtocol() {
		return protocol;
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getMessage() {
		return message;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Logger logger() {
		return LOGGER;
	}
	
}
