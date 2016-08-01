/*
 *    Copyright 2016 Jonathan Beaudoin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xena.natives;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
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

	public static native void mouse_event(int dwFlags, int dx, int dy, int dwData, Pointer dwExtraInfo);

	public static native WinUser.HHOOK SetWindowsHookExW(int var1, WinUser.HOOKPROC var2, WinDef.HINSTANCE var3, int var4);

}
