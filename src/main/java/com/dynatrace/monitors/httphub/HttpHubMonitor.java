package com.dynatrace.monitors.httphub;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.diagnostics.pdk.Monitor;
import com.dynatrace.diagnostics.pdk.MonitorEnvironment;
import com.dynatrace.diagnostics.pdk.Status;
import com.dynatrace.diagnostics.pdk.Status.StatusCode;
import com.dynatrace.utils.http.HttpHub;
import com.dynatrace.utils.http.HttpTraffic;
import com.dynatrace.utils.http.HttpTrafficListener;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class HttpHubMonitor implements Monitor, HttpTrafficListener {
	
	private static final Logger LOGGER =
			Logger.getLogger(HttpHubMonitor.class.getName());
	
	private static final String CONFIG_HOST =
			"com.dynatrace.monitors.httphub.config.host".intern();
	private static final String CONFIG_PORT =
			"com.dynatrace.monitors.httphub.config.port".intern();
	private static final String CONFIG_LISTEN_PORT =
			"com.dynatrace.monitors.httphub.config.bindport".intern();
	
	private HttpHub hub = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status execute(MonitorEnvironment env) throws Exception {
		// book your measurements here
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handle(HttpTraffic traffic) {
		// collect your measurements here
	}

	@Override
	public Status setup(MonitorEnvironment env) throws Exception {
		String host = env.getConfigString(CONFIG_HOST);
		long port = env.getConfigLong(CONFIG_PORT);
		long listenPort = env.getConfigLong(CONFIG_LISTEN_PORT);
		try {
			startHub(host, (int) port, (int) listenPort);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unable to launch HTTP Hub", e);
			return new Status(StatusCode.ErrorInfrastructure);
		}
		return new Status(StatusCode.Success);
	}
	
	private void shutdownHub() {
		if (hub == null) {
			hub.close();
		}
	}
	
	private void startHub(String host, int port, int listenPort) throws IOException {
		shutdownHub();
		hub = new HttpHub(listenPort, port, host);
		hub.start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void teardown(MonitorEnvironment env) throws Exception {
		shutdownHub();
	}

}
