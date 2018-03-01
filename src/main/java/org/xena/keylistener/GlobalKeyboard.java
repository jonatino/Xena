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

package org.xena.keylistener;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.xena.natives.User32;

import java.util.EventListener;
import java.util.List;
import java.util.function.Consumer;

public final class GlobalKeyboard extends NativeKeyUtils implements EventListener {
	
	private static final WinDef.LRESULT CONSUMED = new WinDef.LRESULT(1);
	private static final WinDef.LRESULT OKAY = new WinDef.LRESULT(0);
	private static WinUser.HHOOK hook;
	private static WinUser.LowLevelKeyboardProc keyboardHook;
	private final Int2ObjectArrayMap<NativeKeyEvent> keysDown = new Int2ObjectArrayMap<>();
	private final NativeKeyListener listener;
	private final ThreadLocal<NativeKeyEvent> currentEvent = ThreadLocal.withInitial(NativeKeyEvent::new);
	private final List<NativeKeyCombination> keyMaps = new ObjectArrayList<>(5);
	
	private GlobalKeyboard(NativeKeyListener listener) {
		this.listener = listener;
		try {
			new Thread() {
				@Override
				public void run() {
					keyboardHook = (nCode, wParam, info) -> {//We have to store it in a static object to prevent GC
						if (nCode >= 0) {
							NativeKeyEvent event = currentEvent.get();
							event.reset();
							event.setKeyCode(info.vkCode);
							event.setTime(info.time & 0xFFFFFFFFL);
							boolean consume = false;
							switch (wParam.intValue()) {
								case WinUser.WM_KEYDOWN:
								case WinUser.WM_SYSKEYDOWN:
									consume = dispatch(EventType.KEY_DOWN, event);
									break;
								case WinUser.WM_KEYUP:
								case WinUser.WM_SYSKEYUP:
									consume = dispatch(EventType.KEY_UP, event);
									break;
							}
							if (event.consumed() || consume) {
								return CONSUMED;
							}
						}
						return OKAY;
					};
					hook = User32.SetWindowsHookExW(WinUser.WH_KEYBOARD_LL, keyboardHook, Kernel32.INSTANCE.GetModuleHandle(null), 0);//We have to store it in a static object to prevent GC
					WinUser.MSG msg = new WinUser.MSG();
					WinUser.HWND hwnd = new WinUser.HWND();
					while (true) {
						try {
							User32.PeekMessageW(msg, hwnd, 0, 0, 0);
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
	
	public static GlobalKeyboard register(NativeKeyListener listener) {
		return new GlobalKeyboard(listener);
	}
	
	public boolean unhook() {
		return User32.UnhookWindowsHookEx(hook);
	}
	
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
				keysDown.put(event.getKeyCode(), event);
				for (int i = 0; i < keyMaps.size(); i++) {
					NativeKeyCombination key = keyMaps.get(i);
					
					if (!key.matches(event)) {
						continue;
					}
					key.exec(event);
					return event.consumed();
				}
				return listener.onKeyPressed(event);
			case KEY_UP:
				keysDown.remove(event.getKeyCode());
				return listener.onKeyReleased(event);
			default:
				throw new RuntimeException("Unknown event type! " + type);
		}
	}
	
	public Int2ObjectArrayMap keys() {
		return keysDown;
	}
	
	private enum EventType {
		KEY_DOWN,
		KEY_UP
	}
	
}
