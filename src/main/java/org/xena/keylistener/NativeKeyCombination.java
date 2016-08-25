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

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import static org.xena.keylistener.NativeKeyUtils.*;


/**
 * Created by Jonathan on 9/20/2015.
 */
public final class NativeKeyCombination {

    private final int modifiers;

    private final int[] keys;

    private final Consumer<NativeKeyEvent> event;

    public NativeKeyCombination(Consumer<NativeKeyEvent> event, int modifiers, int... keys) {
        this.modifiers = modifiers;
        this.keys = keys;
        this.event = event;
    }

    void exec(NativeKeyEvent e) {
        event.accept(e);
    }

    boolean matches(NativeKeyEvent event) {
	    if ((modifiers & CONTROL) != CONTROL && isCtrlDown() || (modifiers & CONTROL) == CONTROL && !isCtrlDown()) {
		    return false;
	    }
	    if ((modifiers & SHIFT) != SHIFT && isShiftDown() || (modifiers & SHIFT) == SHIFT && !isShiftDown()) {
		    return false;
	    }
	    if ((modifiers & ALT) != ALT && isAltDown() || (modifiers & ALT) == ALT && !isAltDown()) {
		    return false;
	    }
	    for (int i : keys) {
		    if (event.getKeyCode() == i) {
			    return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
	    if (keys[0] == -1) {
		    return "None";
	    }
        String s = "";
	    for (int key : keys) {
		    s += KeyEvent.getKeyText(key) + ", ";
	    }
	    if ((modifiers & ALT) == ALT) {
		    s = "Alt + [" + s + "]";
	    }
	    if ((modifiers & SHIFT) == SHIFT) {
		    s = "Shift + [" + s + "]";
	    }
	    if ((modifiers & CONTROL) == CONTROL) {
		    s = "Ctrl + [" + s + "]";
	    }
        return s;
    }

}
