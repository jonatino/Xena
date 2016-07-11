package org.xena.logging;

public interface Logger {

    Logger info(Object message);

    Logger error(Object message);

    Logger warn(Object message);

    Logger debug(Object message);

    Level level();

    Logger setLevel(Level level);

}
