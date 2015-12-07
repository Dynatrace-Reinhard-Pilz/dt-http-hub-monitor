package com.dynatrace.utils.http.impl;

import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.http.HttpTraffic;
import com.dynatrace.utils.http.Request;
import com.dynatrace.utils.http.Response;

/**
 * Manages forwarding traffic from and to a socket to another socket.<br />
 * <br />
 * After successful execution this object also represents the result object
 * in form of captured data.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class RequestCycle implements Callable<HttpTraffic>, HttpTraffic, AutoCloseable {
	
	private final Logger LOGGER =
			Logger.getLogger(RequestCycle.class.getName());
	
	/**
	 * The socket from where HTTP traffic originated and which is supposed
	 * to receive the response
	 */
    private final Socket clientSocket;
    
    /**
     * The socket to forward HTTP traffic to and from which to capture the
     * response
     */
    private final Socket serverSocket;
    
    /**
     * Required to ensure that forwarding the request traffic and and response
     * traffic works asynchronously
     */
    private final ExecutorService executor;
    private Request request = null;
    private Response response = null;

    /**
     * c'tor
     * 
     * @param clientSocket the socket HTTP traffic originates from
     * @param serverSocket the socket to forward HTTP traffic to
     * @param executor the executor to use for managing the forwarding of
     * 		HTTP request data and HTTP response data asynchronously
     */
    public RequestCycle(
    	Socket clientSocket,
    	Socket serverSocket,
    	ExecutorService executor
    ) {
    	Objects.requireNonNull(clientSocket);
    	Objects.requireNonNull(serverSocket);
    	Objects.requireNonNull(executor);
    	this.clientSocket = clientSocket;
    	this.serverSocket = serverSocket;
    	this.executor = executor;
    }
    
    /**
     * {@inheritDoc}
     */
	@Override
	public HttpTraffic call() throws Exception {
		HttpRequest request = null;
		HttpResponse response = null;
		try {
			request = new HttpRequest(
				clientSocket.getInputStream(),
				serverSocket.getOutputStream()
			);
			Future<Request> fRequest = executor.submit(request);
			response = new HttpResponse(
				serverSocket.getInputStream(),
				clientSocket.getOutputStream()
			);
			Future<Response> fResponse = executor.submit(response);
			this.request = fRequest.get();
			this.response = fResponse.get();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to handle HTTP request", e);
		} finally {
			Closeables.close(request);
			Closeables.close(response);
		}
		return this;
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Request getRequest() {
		return request;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response getResponse() {
		return response;
	}
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		LOGGER.log(Level.FINER, "shutting down");
		Closeables.close(serverSocket);
		Closeables.close(clientSocket);
		LOGGER.log(Level.FINER, "closed");
	}
	
	@Override
	public String toString() {
		if (this.request == null) {
			return super.toString();
		}
		return request.toString();
	}

}
