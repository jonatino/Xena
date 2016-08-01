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
