package org.xena.logging;

enum Level {

    ALL(0),

    INFO(1),

    WARNING(2),
    ERROR(2),

    DEBUG(3),

    OFF(4);

    private final int value;

    Level(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public boolean shouldLog(Level otherLevel) {
        return shouldLog(this, otherLevel);
    }

    public static boolean shouldLog(Level level, Level otherLevel) {
        return level.value() < otherLevel.value();
    }

}
