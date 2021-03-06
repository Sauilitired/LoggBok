package com.github.sauilitired.loggbok;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.format.DateTimeFormatter;

@SuppressWarnings({"WeakerAccess", "unused"}) public abstract class SimpleLogger
    implements Logger, AutoCloseable {

    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private final String logFormat;
    private final LogLevels logLevels;
    private String name;
    private DateTimeFormatter dateTimeFormatter;
    private LogFormatter logFormatter = new StandardFormatter();

    public SimpleLogger(final String logFormat, final LogLevels logLevels) {
        this(Thread.currentThread().getName(), logFormat, logLevels);
    }

    public SimpleLogger(final String name, final String logFormat, final LogLevels logLevels) {
        this(name, logFormat, logLevels, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public SimpleLogger(final String name, final String logFormat, final LogLevels logLevels,
        final DateTimeFormatter dateTimeFormatter) {
        this.name = name;
        this.logFormat = logFormat;
        this.logLevels = logLevels;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public String getLogFormatted(final LogEntry logEntry) {
        return getLogFormat().replace("%level%", this.getLogLevels().getLevel(logEntry.getLevel()))
            .replace("%name%", this.getName())
            .replace("%time%", this.dateTimeFormatter.format(logEntry.getTimestamp()))
            .replace("%thread%", this.threadMXBean.getThreadInfo(logEntry.getThreadId()).getThreadName())
            .replace("%message%", this.logFormatter.format(logEntry));
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return this.dateTimeFormatter;
    }

    public void setDateTimeFormatter(final DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public String getLogFormat() {
        return this.logFormat;
    }

    public LogLevels getLogLevels() {
        return this.logLevels;
    }

    public LogFormatter getLogFormatter() {
        return this.logFormatter;
    }

    public void setLogFormatter(final LogFormatter logFormatter) {
        this.logFormatter = logFormatter;
    }

    @Override public final void close() {
        try {
            this.stop();
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to close the logger.", e);
        }
    }

}
