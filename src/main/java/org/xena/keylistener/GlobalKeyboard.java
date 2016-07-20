package org.xena.keylistener;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import org.xena.natives.User32;

import java.util.*;
import java.util.function.Consumer;

public final class GlobalKeyboard extends NativeKeyUtils implements EventListener {

    private final Map<Integer, NativeKeyEvent> keysDown = new HashMap<>();

    private static WinUser.HHOOK hook;
    private static WinUser.LowLevelKeyboardProc keyboardHook;

    private final NativeKeyListener listener;

    public static GlobalKeyboard register(NativeKeyListener listener) {
        return new GlobalKeyboard(listener);
    }

    public boolean unhook() {
        return User32.UnhookWindowsHookEx(hook);
    }

    private GlobalKeyboard(NativeKeyListener listener) {
        this.listener = listener;
        try {
            new Thread() {
                @Override
                public void run() {
                    keyboardHook = (nCode, wParam, info) -> {//We have to store it in a static object to prevent GC
                        if (nCode >= 0) {
                            NativeKeyEvent event = new NativeKeyEvent(info.vkCode, info.time & 0xFFFFFFFFL);
                            boolean swallow = false;
                            switch (wParam.intValue()) {
                                case WinUser.WM_KEYDOWN:
                                case WinUser.WM_SYSKEYDOWN:
                                    swallow = dispatch(EventType.KEY_DOWN, event);
                                    break;
                                case WinUser.WM_KEYUP:
                                case WinUser.WM_SYSKEYUP:
                                    swallow = dispatch(EventType.KEY_UP, event);
                                    break;
                            }
                            if (event.consumed() || swallow) {
                                return new WinDef.LRESULT(1);
                            }
                        }
                        return new WinDef.LRESULT(0);
                    };
	                hook = User32.SetWindowsHookExW(WinUser.WH_KEYBOARD_LL, keyboardHook, Kernel32.INSTANCE.GetModuleHandle(null), 0);//We have to store it in a static object to prevent GC
	                while (User32.GetMessageW(null, null, 0, 0) != 0) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        } finally {
            unhook();
        }
    }

    private final List<NativeKeyCombination> keyMaps = new ArrayList<>(5);

    public NativeKeyCombination registerHotkey(int modifiers, int key, Consumer<NativeKeyEvent> c) {
        return registerHotkey(new NativeKeyCombination(c, modifiers, key));
    }

    public NativeKeyCombination registerHotkey(NativeKeyCombination comb) {
        keyMaps.add(comb);
        return comb;
    }


    public boolean removeHotkey(NativeKeyCombination comb) {
        return keyMaps.remove(comb);
    }

    public boolean dispatch(EventType type, NativeKeyEvent event) {
        switch (type) {
            case KEY_DOWN:
                keysDown.put(event.keyCode(), event);
                for (NativeKeyCombination key : keyMaps) {
                    if (!key.matches(event)) {
                        continue;
                    }
                    key.exec(event);
                    return event.consumed();
                }
                return listener.onKeyPressed(event);
            case KEY_UP:
                keysDown.remove(event.keyCode());
                return listener.onKeyReleased(event);
            default:
                throw new RuntimeException("Unknown event type! " + type);
        }
    }

    public Map<Integer, NativeKeyEvent> keys() {
        return keysDown;
    }

    private enum EventType {
        KEY_DOWN,
        KEY_UP
    }

}
