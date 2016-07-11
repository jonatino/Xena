package org.xena.logging;

final class DefaultLogger extends AbstractLogger {

    private DefaultLogger(Outputter outputter, Level defaultLevel) {
        super(outputter, defaultLevel);
    }

    DefaultLogger(Outputter outputter) {
        this(outputter, Level.ALL);
    }

}
