package org.xena.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

final class StandardOutputter implements Outputter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm:ss a");

    @Override
    public void showLog(Level level, Object message) {
        StackTraceElement trace = new Throwable().getStackTrace()[3];
        System.err.println(LocalDateTime.now().format(FORMATTER) + " " + trace.getClassName() + " " + trace.getMethodName());
        System.err.println(level.name() + ": " + message);
    }

}
