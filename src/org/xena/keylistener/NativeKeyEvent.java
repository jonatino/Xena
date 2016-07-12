package org.xena.keylistener;

/**
 * Created by Jonathan on 8/29/2015.
 */
public final class NativeKeyEvent extends NativeKeyUtils {

    private final int keyCode;
    private final long time;
    private boolean swallowed;

    public NativeKeyEvent(int keyCode, long time) {
        this.keyCode = keyCode;
        this.time = time;
    }

    public boolean consumed() {
        return swallowed;
    }

    public void consume() {
        swallowed = true;
    }

    public int keyCode() {
        return keyCode;
    }

    public boolean within(int min, int max) {
        return keyCode >= min && keyCode <= max;
    }

    public long time() {
        return time;
    }

    public boolean hasModifiers() {
        return isCtrlDown() || isAltDown() || isShiftDown();
    }

    public String codeString() {
        return String.valueOf((char) keyCode);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": keycode=" + keyCode + ", key=" + codeString() + ", time=" + time + ", swallowed=" + swallowed;
    }
}
