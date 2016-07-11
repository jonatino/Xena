package org.xena.keylistener;

public interface NativeKeyListener {

    boolean onKeyPressed(NativeKeyEvent event);

    boolean onKeyReleased(NativeKeyEvent event);

}
