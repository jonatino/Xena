package org.xena.keylistener;

import com.beaudoin.jmm.natives.win32.User32;

/**
 * Created by Jonathan on 8/30/2015.
 */
public class NativeKeyUtils {

    public static final int CONTROL = 1;
    public static final int SHIFT = 2;
    public static final int ALT = 4;

    public static final int LEFT_SHIFT = 160;
    public static final int RIGHT_SHIFT = 161;

    public static final int LEFT_CTRL = 162;
    public static final int RIGHT_CTRL = 163;

    public static final int LEFT_ALT = 164;
    public static final int RIGHT_ALT = 165;


    public static boolean isLeftAltDown() {
        return isKeyDown(LEFT_ALT);
    }

    public static boolean isRightAltDown() {
        return isKeyDown(RIGHT_ALT);
    }

    public static boolean isAltDown() {
        return isLeftAltDown() || isRightAltDown();
    }

    public static boolean isLeftShiftDown() {
        return isKeyDown(LEFT_SHIFT);
    }

    public static boolean isRightShiftDown() {
        return isKeyDown(RIGHT_SHIFT);
    }

    public static boolean isShiftDown() {
        return isLeftShiftDown() || isRightShiftDown();
    }

    public static boolean isLeftCtrlDown() {
        return isKeyDown(LEFT_CTRL);
    }

    public static boolean isRightCtrlDown() {
        return isKeyDown(RIGHT_CTRL);
    }

    public static boolean isCtrlDown() {
        return isLeftCtrlDown() || isRightCtrlDown();
    }

    public static boolean isKeyDown(int keycode) {
        return stateOf(keycode) < 0;
    }

    public static int stateOf(int keycode) {
        return User32.GetKeyState(keycode);
    }

}
