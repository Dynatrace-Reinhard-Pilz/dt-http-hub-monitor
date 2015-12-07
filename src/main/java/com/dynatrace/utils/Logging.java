package com.dynatrace.utils;

import java.io.PrintStream;
import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Logging extends Handler {
	
	private static final Logging HANDLER = new Logging();

	public static void init() {
		Logger rootLogger = Logger.getLogger("com.dynatrace.");
		removeHandlers(rootLogger);
		rootLogger.addHandler(HANDLER);
		rootLogger.setUseParentHandlers(false);
	}
	
	private static void removeHandlers(Logger logger) {
		if (logger == null) {
			return;
		}
		removeHandlers(logger, logger.getHandlers());
	}
	
	private static void removeHandlers(Logger logger, Handler[] handlers) {
		if (handlers == null) {
			return;
		}
		for (Handler handler : handlers) {
			if (handler == null) {
				continue;
			}
			logger.removeHandler(handler);
		}
	}

	@Override
	public synchronized void publish(LogRecord record) {
		if (record == null) {
			return;
		}
		Level level = record.getLevel();
		if (level.equals(Level.INFO)) {
			publish(record, System.out);
		} else {
			publish(record, System.err);
		}
	}
	
	private void publish(LogRecord record, PrintStream out) {
		Objects.requireNonNull(record);
		Objects.requireNonNull(out);
		Level level = record.getLevel();
		String loggerName = Strings.truncate(record.getLoggerName(), '.');
		out.println(level.getName() + " [" + loggerName + "] " + record.getMessage());
		Throwable thrown = record.getThrown();
		if (thrown != null) {
			out.println(Throwables.toString(thrown));
		}
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws SecurityException {
	}
}
