package com.dynatrace.utils.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.http.impl.RequestCycle;

/**
 * A {@link HttpHub} listens for incoming HTTP traffic and forwards that traffic
 * unmodified to another host on another port.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class HttpHub extends Thread implements AutoCloseable {
	
	private static final Logger LOGGER =
			Logger.getLogger(HttpHub.class.getName());
	
	private final ExecutorService executor = Executors.newCachedThreadPool();
	
	/**
	 * thread safe signal for running or having been shut down
	 */
	private final AtomicBoolean isRunning = new AtomicBoolean(false);
	
	/**
	 * The {@link ServerSocket} to listen for HTTP traffic on
	 */
	private final ServerSocket bindSocket;
	
	/**
	 * The port to connect to when forwarding HTTP traffic
	 */
	private final int targetPort;
	
	/**
	 * The host to connect to when forwarding HTTP traffic
	 */
	private final String targetAddress;
	
	/**
	 * Listeners to notify about HTTP traffic once it has been completed
	 */
	private final Collection<HttpTrafficListener> listeners = new ArrayList<>();
    
	/**
	 * c'tor
	 * 
	 * @param bindPort the port to listen on for HTTP traffic
	 * @param targetPort the port to connect to when forwarding HTTP traffic
	 * @param targetAddress the host to connect to when forwarding HTTP traffic
	 * 
	 * @throws IOException if binding to the {@code bindPort} fails
	 */
    public HttpHub(int bindPort, int targetPort, String targetAddress)
    	throws IOException
    {
    	super(HttpHub.class.getSimpleName());
    	setDaemon(true);
		bindSocket = new ServerSocket(bindPort);
		this.targetAddress = targetAddress;
		this.targetPort = targetPort;
	}
    
    public void addListener(HttpTrafficListener listener) {
    	if (listener == null) {
    		return;
    	}
    	synchronized (listeners) {
    		if (listeners.contains(listener)) {
    			return;
    		}
    		listeners.add(listener);
    	}
    }
    
    private void notifyListener(
    	final HttpTraffic traffic,
    	final HttpTrafficListener listener
    ) {
    	if (traffic == null) {
    		return;
    	}
    	if (listener == null) {
    		return;
    	}
    	executor.execute(new Runnable() {
    		@Override
    		public void run() {
    			listener.handle(traffic);
    		}
    	});
    }
    
    private void notifyListeners(HttpTraffic traffic) {
    	if (traffic == null) {
    		return;
    	}
    	LOGGER.log(Level.INFO, "notifyListener(" + traffic + ")");
    	synchronized (listeners) {
    		for (HttpTrafficListener listener : listeners) {
				notifyListener(traffic, listener);
			}
    	}
    }
    
    /**
     * {@inheritDoc}
     */
	@Override
	public void run() {
		try {
			isRunning.set(true);
	        LOGGER.log(
	        	Level.INFO,
	        	"Listening at port " + bindSocket.getLocalPort()
	        );
	        while (isRunning.get()) {
	            try (
	            	Socket socket = new Socket(targetAddress, targetPort);
	            	RequestCycle requestCycle = new RequestCycle(
	            		bindSocket.accept(),
	            		socket,
	            		executor
	            	);
	            ) {
	            	HttpTraffic traffic = requestCycle.call();
	            	LOGGER.log(Level.INFO, "traffic object: " + traffic);
	            	notifyListeners(traffic);
	            } catch (SocketException e) {
	            	if ("socket closed".equals(e.getMessage())) {
	            		LOGGER.log(Level.INFO, "socket closed");
	            		return;
	            	}
	            	if ("Socket is closed".equals(e.getMessage())) {
	            		LOGGER.log(Level.INFO, "Socket is closed");
	            		return;
	            	}
	            	throw e;
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, "Non HTTP traffic received", e);
				}
	            if (Thread.interrupted()) {
	            	return;
	            }
	        }
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to listen to socket", e);
		}
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		isRunning.set(false);
		LOGGER.log(Level.FINEST, "shutting down");
		Closeables.close(bindSocket);
		executor.shutdown();
		LOGGER.log(Level.FINEST, "closed");
	}
}
