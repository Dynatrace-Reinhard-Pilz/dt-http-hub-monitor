package com.dynatrace.utils.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class InputStreamWrapper extends InputStream {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(InputStreamWrapper.class.getName());
	
	private final InputStream in;
	
	public InputStreamWrapper(InputStream in) {
		this.in = in;
	}
	
	public InputStream getInputStream() {
		return in;
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return in.read(b);
	}
	
	@Override
	public int available() throws IOException {
		return in.available();
	}
	
	@Override
	public long skip(long n) throws IOException {
		return in.skip(n);
	}
	
	@Override
	public boolean markSupported() {
		return in.markSupported();
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		in.mark(readlimit);
	}
	
	@Override
	public synchronized void reset() throws IOException {
		in.reset();
	}
	
	@Override
	public void close() throws IOException {
	}

}
