package com.dynatrace.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Throwables {

	public static String toString(Throwable t) {
		if (t == null) {
			return Strings.EMPTY;
		}
		try (
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
		) {
			t.printStackTrace(pw);
			pw.flush();
			sw.flush();
			return sw.getBuffer().toString(); 
		} catch (IOException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}
}
