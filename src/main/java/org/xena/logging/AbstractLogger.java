package org.xena.logging;

abstract class AbstractLogger implements Logger {

    private final Outputter outputter;

    private volatile Level level;

    AbstractLogger(Outputter outputter, Level defaultLevel) {
        this.outputter = outputter;
        this.level = defaultLevel;
    }

    private Logger log(Level level, Object message) {
        if (level().shouldLog(level)) {
            outputter.showLog(level, message);
        }
        return this;
    }

    @Override
    public final Logger info(Object message) {
        return log(Level.INFO, message);
    }

    @Override
    public final Logger error(Object message) {
        return log(Level.ERROR, message);
    }

    @Override
    public final Logger warn(Object message) {
        return log(Level.WARNING, message);
    }

    @Override
    public final Logger debug(Object message) {
        return log(Level.DEBUG, message);
    }

    @Override
    public final Level level() {
        return level;
    }

    @Override
    public final Logger setLevel(Level level) {
        this.level = level;
        return this;
    }

}
