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

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Jonathan on 8/29/2015.
 */
public final class NativeKeyEvent extends NativeKeyUtils {
	
	@Setter
	@Getter
	private int keyCode;
	
	@Setter
	@Getter
	private long time;
	
	@Getter
	private boolean consumed;
	
	NativeKeyEvent() {
	}
	
	public boolean consumed() {
		return consumed;
	}
	
	public void consume() {
		consumed = true;
	}
	
	public void reset() {
		consumed = false;
		keyCode = -1;
		time = -1;
	}
	
	public boolean within(int min, int max) {
		return keyCode >= min && keyCode <= max;
	}
	
	public boolean hasModifiers() {
		return isCtrlDown() || isAltDown() || isShiftDown();
	}
	
	public String codeString() {
		return String.valueOf((char) keyCode);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": keycode=" + keyCode + ", key=" + codeString() + ", time=" + time + ", consumed=" + consumed;
	}
}
