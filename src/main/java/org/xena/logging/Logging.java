package org.xena.logging;

public final class Logging {

    private Logging() {
    }

    private static final Outputter STANDARD_OUTPUTTER = new StandardOutputter();

    public static Outputter standardOutputter() {
        return STANDARD_OUTPUTTER;
    }

    // must be under STANDARD_OUTPUTTER or Java will pass null
    private static final Logger GLOBAL_LOGGER = logger();

    public static Logger globalLogger() {
        return GLOBAL_LOGGER;
    }

    public static Logger logger() {
        return logger(standardOutputter());
    }

    public static Logger logger(Outputter outputter) {
        return new DefaultLogger(outputter);
    }

}
