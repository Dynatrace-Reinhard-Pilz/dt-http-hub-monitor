package com.dynatrace.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Closeables {
	
	private static final Logger LOGGER =
			Logger.getLogger(Closeables.class.getName());
	
    private static final int BUFFER_SIZE = 32768;

	public static void copy(InputStream in, OutputStream out) throws IOException {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		byte buffer[] = new byte[BUFFER_SIZE];
		long start = System.currentTimeMillis();
		int numBytesRead = in.read(buffer, 0, BUFFER_SIZE);
		long end = System.currentTimeMillis();
		LOGGER.log(Level.FINEST, "Reading " + numBytesRead + " bytes from " + in + " lasted " + (end - start) + " ms");
		while (numBytesRead != -1) {
			start = System.currentTimeMillis();
			out.write(buffer, 0, numBytesRead);
			LOGGER.log(Level.FINEST, "Writing " + numBytesRead + " bytes to " + out + " lasted " + (end - start) + " ms");
			end = System.currentTimeMillis();
			start = System.currentTimeMillis();
			numBytesRead = in.read(buffer, 0, BUFFER_SIZE);
			end = System.currentTimeMillis();
			LOGGER.log(Level.FINEST, "Reading " + numBytesRead + " bytes from " + in + " lasted " + (end - start) + " ms");
		}
		out.flush();
	}
	
	public static void copy(InputStream in, OutputStream... outs) throws IOException {
		copy(in, -1, outs);
	}
	
	public static void copy(InputStream in, int len, OutputStream... outs) throws IOException {
		Objects.requireNonNull(in);
		Objects.requireNonNull(outs);
		for (OutputStream out : outs) {
			if (out == null) {
				throw new IllegalArgumentException(
					"none of the OutputStreams must be null"
				);
			}
		}
		int numBytesToGo = BUFFER_SIZE;
		if (len != -1) {
			numBytesToGo = len;
		}
		byte buffer[] = new byte[BUFFER_SIZE];
		long start = System.currentTimeMillis();
		int numBytesRead = in.read(buffer, 0, Math.min(BUFFER_SIZE, numBytesToGo));
		long end = System.currentTimeMillis();
		LOGGER.log(Level.FINEST, "Reading " + numBytesRead + " bytes from " + in + " lasted " + (end - start) + " ms");
		while (numBytesRead != -1) {
			if (len != -1) {
				numBytesToGo = numBytesToGo - numBytesRead;
			}
			for (OutputStream out : outs) {
				start = System.currentTimeMillis();
				out.write(buffer, 0, numBytesRead);
				end = System.currentTimeMillis();
				String name = null;
				if (out.getClass().getSimpleName().equals("SocketOutputStream")) {
					name = "SocketOutputStream";
				} else if (out instanceof ByteArrayOutputStream) {
					name = "ByteArrayOutputStream";
				}
				LOGGER.log(Level.FINEST, "Writing " + numBytesRead + " bytes to " + name + " lasted " + (end - start) + " ms");
			}
			if (numBytesToGo == 0) {
				break;
			}
			start = System.currentTimeMillis();
			numBytesRead = in.read(buffer, 0, Math.min(BUFFER_SIZE, numBytesToGo));
			end = System.currentTimeMillis();
			LOGGER.log(Level.FINEST, "Reading " + numBytesRead + " bytes from " + in + " lasted " + (end - start) + " ms");
		}
		start = System.currentTimeMillis();
		for (OutputStream out : outs) {
			out.flush();
		}
		end = System.currentTimeMillis();
		LOGGER.log(Level.FINEST, "Flushing OutputStreams lasted " + (end - start) + " ms");
	}
	
	public static void close(Closeable closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to close " + closeable, e);
		}
	}
	
	public static void copyUntil(InputStream in, OutputStream out, byte[] until)
		throws IOException
	{
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		Objects.requireNonNull(until);
		int untilIdx = 0;
		int byt = 0;
		while ((byt = in.read()) != -1) {
//			LOGGER.log(Level.INFO, Character.valueOf((char) byt).toString());
			if (byt == until[untilIdx]) {
				untilIdx++;
			}
			out.write(byt);
			if (untilIdx == until.length) {
				break;
			}
		}
		out.flush();
	}
	
	public static byte[] copyUntil(InputStream in, byte[] until)
		throws IOException
	{
		Objects.requireNonNull(in);
		Objects.requireNonNull(until);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			copyUntil(in, out, until);
			return out.toByteArray();
		}
	}
	
	public static InputStream copy(InputStream in, byte[] until)
		throws IOException
	{
		Objects.requireNonNull(in);
		Objects.requireNonNull(until);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			copyUntil(in, out, until);
			return new ByteArrayInputStream(out.toByteArray());
		}
	}
	
	public static void dump(InputStream in) throws IOException {
		int b = 0;
		while ((b = in.read()) != -1) {
			System.err.println(Character.valueOf((char) b) + " (" + b + ")");
		}
	}
}
