package com.dynatrace.monitors.httphub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dynatrace.test.http.HttpServer;
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.Logging;
import com.dynatrace.utils.http.HttpHub;
import com.dynatrace.utils.http.HttpTraffic;
import com.dynatrace.utils.http.HttpTrafficListener;
import com.dynatrace.utils.http.Request;
import com.dynatrace.utils.http.Response;

/**
 * Tests for class {@link HttpHub}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class HttpHubTest implements HttpTrafficListener {
	
	private static final Logger LOGGER =
			Logger.getLogger(HttpHubTest.class.getName());
	
	private static final int HTTP_PORT = 7555;
	private static final int PROXY_PORT = 7554;
	
	private CountDownLatch latch = new CountDownLatch(1);
	
	@BeforeClass
	public static void beforeClass() {
		Logging.init();
	}

	@Test
	public void testHttpProxy() throws Exception {
		try (
			HttpServer server = new HttpServer(HTTP_PORT);
			HttpHub hub = new HttpHub(PROXY_PORT, HTTP_PORT, "localhost");
		) {
			hub.addListener(this);
			hub.start();
			LOGGER.log(Level.INFO, " ------- PROXIED ------");
			byte[] proxied = get("http://localhost:" + PROXY_PORT + "/hello");
			LOGGER.log(Level.INFO, " ------- NON PROXIED ------");
			byte[] nonproxied = get("http://localhost:" + HTTP_PORT + "/hello");
			Assert.assertArrayEquals(nonproxied, proxied);
			latch.await(500, TimeUnit.MILLISECONDS);
		}
	}

	private static byte[] get(String url) throws IOException {
		return get(new URL(url));
	}
	
	private static byte[] get(URL url) throws IOException {
		URLConnection conn =  url.openConnection();
		try (
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = conn.getInputStream();
		) {
			int contentLength = conn.getContentLength();
			LOGGER.log(Level.INFO, "Content-Length: " + contentLength);
			Closeables.copy(in, out);
			byte[] bytes = out.toByteArray();
			LOGGER.log(Level.INFO, "response: "  + new String(bytes));
			return bytes;
		}
	}

	@Override
	public void handle(HttpTraffic traffic) {
		LOGGER.log(Level.INFO, "handle(" + traffic + ")");
		Request request = traffic.getRequest();
		System.err.println(request);
		Response response = traffic.getResponse();
		try {
			Closeables.copy(response.getBody(), System.err);
		} catch (IOException e) {
			Assert.fail();
		}
		latch.countDown();
	}
}
