package com.dynatrace.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Strings {

	public static final String EMPTY = "".intern();
	
	public static boolean nonEmpty(String s) {
		if (s == null) {
			return false;
		}
		return !s.isEmpty();
	}
	
	public static boolean empty(String s) {
		return (s == null) || s.isEmpty();
	}
	
	public static String read(InputStream in, char delim) throws IOException {
		Objects.requireNonNull(in);
		byte[] buffer = new byte[4096];
		int i = 0;
		for (i = 0; i < buffer.length; i++) {
			int b = in.read();
			if (b == delim) {
				break;
			}
			buffer[i] = (byte)b;
		}
		return new String(buffer, 0, i);
	}
	
	public static String readLine(InputStream in) throws IOException {
		Objects.requireNonNull(in);
		boolean cr = false;
		boolean lf = false;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int b = 0;
		while ((b = in.read()) != -1) {
			if (b == 13) {
				cr = true;
			} else if (b == 10) {
				lf = true;
			} else {
				cr = false;
				lf = false;
			}
			out.write(b);
			if (cr && lf) {
				break;
			}
		}
		return new String(out.toByteArray(), "US-ASCII");
	}

	
	public static String readLine(InputStream in, char delim) throws IOException {
		Objects.requireNonNull(in);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int b = 0;
		while ((b = in.read()) != -1) {
			if (b == '\r') {
				in.read();
				break;
			}
			if (b == '\n') {
				break;
			}
			if (b == delim) {
				break;
			}
			out.write(b);
		}
		return new String(out.toByteArray(), "US-ASCII");
	}
	
	public static String truncate(String s, char c) {
		if (s == null) {
			return null;
		}
		int idx = s.lastIndexOf(c);
		if (idx == -1) {
			return s;
		}
		if (idx == s.length() - 1) {
			return s;
		}
		return s.substring(idx + 1);
	}
	
	public static int readInt(InputStream in, char delim) throws IOException {
		String sInt = readLine(in, delim);
		if (empty(sInt)) {
			throw new IOException("no status code found");
		}
		try {
			return Integer.parseInt(sInt);
		} catch (NumberFormatException e) {
			throw new IOException("invalid status code '" + sInt + "'", e);
		}
	}
	
}
