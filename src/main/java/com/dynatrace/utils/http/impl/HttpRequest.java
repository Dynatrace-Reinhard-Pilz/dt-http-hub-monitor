package com.dynatrace.utils.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.dynatrace.utils.http.HttpHub;
import com.dynatrace.utils.http.Method;
import com.dynatrace.utils.http.Protocol;
import com.dynatrace.utils.http.Request;

/**
 * Captures the incoming HTTP request intercepted by a {@link HttpHub}, stores
 * request details like the HTTP method and URI.<br />
 * <br />
 * Upon execution of this {@link Thread} the incoming traffic is getting
 * forwarding unmodified to an {@link OutputStream}, which is likely sending
 * data to the target host which is eventually handling the HTTP request
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public final class HttpRequest extends HttpData<Request> implements Request {

	private static final Logger LOGGER =
			Logger.getLogger(HttpRequest.class.getName());
	
	/**
	 * The HTTP method of the incoming HTTP request
	 */
	private final Method method;
	
	/**
	 * The URI of the incoming HTTP request
	 */
	private final URI uri;
	
	/**
	 * The HTTP protocol ({@code HTTP1.0} or {@code HTTP1.1}) of the incoming
	 * HTTP request
	 */
	private final Protocol protocol;
	
	/**
	 * c'tor
	 * 
	 * @param in the {@link InputStream} providing the HTTP traffic coming
	 * 		from the HTTP client. Once handed over the {@link InputStream} is
	 * 		already being consumed. Therefore callers cannot reuse it.
	 * @param out the {@link OutputStream} to forward the HTTP traffic to
	 * 
	 * @throws IOException in case evaluating HTTP Method, URI or HTTP protocol
	 * 		fails or contains invalid data
	 * @throws URISyntaxException in case the requested URI is invalid
	 */
	public HttpRequest(InputStream in, OutputStream out)
		throws IOException, URISyntaxException
	{
		super(in, out);
		String firstLine = getFirstLine();
		StringTokenizer strTok = new StringTokenizer(firstLine, " ");
		if (!strTok.hasMoreTokens()) {
			throw new IOException("no method given");
		}
		this.method = Method.fromString(strTok.nextToken().trim());
		if (!strTok.hasMoreTokens()) {
			throw new IOException("no URI given");
		}
		this.uri = new URI(strTok.nextToken().trim());
		if (!strTok.hasMoreTokens()) {
			throw new IOException("protocol given");
		}
		this.protocol = Protocol.fromString(strTok.nextToken().trim());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Method getMethod() {
		return method;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public URI getURI() {
		return uri;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Protocol getProtocol() {
		return protocol;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.method + " " + this.uri;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Logger logger() {
		return LOGGER;
	}

}
