package com.dynatrace.test.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.utils.Closeables;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public class HttpServer implements com.sun.net.httpserver.HttpHandler, AutoCloseable {
	
	private static final Logger LOGGER =
			Logger.getLogger(HttpServer.class.getName());
	
	private final com.sun.net.httpserver.HttpServer server;
	private Request lastRequest = null;
	
	public HttpServer(int port) throws IOException {
		InetSocketAddress address = new InetSocketAddress(port);
		server = com.sun.net.httpserver.HttpServer.create(address, 0);
		server.setExecutor(null);
		HttpContext context = server.createContext("/");
		context.setHandler(this);
		server.start();
		LOGGER.log(Level.INFO, "Listening on port " + port);
	}
	
	public Request getLastRequest() {
		synchronized (server) {
			return lastRequest;
		}
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try (
			InputStream in = exchange.getRequestBody();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
		) {
			Closeables.copy(in, out);
			synchronized (server) {
				lastRequest = new Request(
					exchange.getRequestMethod(),
					exchange.getRequestURI(),
					exchange.getRequestHeaders(),
					exchange.getRemoteAddress(),
					out.toByteArray()
				);
				LOGGER.log(Level.INFO, lastRequest.toString());
			}
		}
		byte[] bytes = lastRequest.getUri().toString().getBytes();
		try (
			InputStream in = new ByteArrayInputStream(bytes);
			OutputStream out = exchange.getResponseBody();
		) {
			exchange.sendResponseHeaders(200, bytes.length);
			Closeables.copy(in, out);
			out.close();
		}
	}

	@Override
	public void close() {
		LOGGER.log(Level.INFO, "shutting down");
		server.stop(0);
		LOGGER.log(Level.INFO, "closed");
	}
	
	@Override
	public String toString() {
		return HttpServer.class.getSimpleName() + "[" + server.getAddress() + "]";
	}

}
