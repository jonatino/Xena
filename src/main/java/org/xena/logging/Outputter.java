package org.xena.logging;

@FunctionalInterface
interface Outputter {

    void showLog(Level level, Object message);

}
