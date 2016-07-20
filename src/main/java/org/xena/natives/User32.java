package org.xena.natives;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

/**
 * Created by Jonathan on 7/18/2016.
 */
public class User32 {

	static {
		Native.register("user32");
	}

	public static native int GetMessageW(WinUser.MSG var1, WinDef.HWND var2, int var3, int var4);

	public static native short GetKeyState(int vKey);

	public static native WinDef.HWND GetForegroundWindow();

	public static native int GetWindowTextW(WinDef.HWND hWnd, char[] lpString, int nMaxCount);

	public static native boolean UnhookWindowsHookEx(WinUser.HHOOK var1);

	public static native WinUser.HHOOK SetWindowsHookExW(int var1, WinUser.HOOKPROC var2, WinDef.HINSTANCE var3, int var4);

}
