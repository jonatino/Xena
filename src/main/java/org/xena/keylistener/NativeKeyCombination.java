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

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import static org.xena.keylistener.NativeKeyUtils.*;


/**
 * Created by Jonathan on 9/20/2015.
 */
public final class NativeKeyCombination {

    @Getter
    private final int modifiers;

    @Getter
    private final int[] keys;

    @Getter
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
        if ((getModifiers() & CONTROL) != CONTROL && isCtrlDown() || (getModifiers() & CONTROL) == CONTROL && !isCtrlDown()) {
            return false;
        }
        if ((getModifiers() & SHIFT) != SHIFT && isShiftDown() || (getModifiers() & SHIFT) == SHIFT && !isShiftDown()) {
            return false;
        }
        if ((getModifiers() & ALT) != ALT && isAltDown() || (getModifiers() & ALT) == ALT && !isAltDown()) {
            return false;
        }
        for (int i : getKeys()) {
            if (event.keyCode() == i) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (getKeys()[0] == -1) {
            return "None";
        }
        String s = "";
        for (int key : getKeys()) {
            s += KeyEvent.getKeyText(key) + ", ";
        }
        if ((getModifiers() & ALT) == ALT) {
            s = "Alt + [" + s + "]";
        }
        if ((getModifiers() & SHIFT) == SHIFT) {
            s = "Shift + [" + s + "]";
        }
        if ((getModifiers() & CONTROL) == CONTROL) {
            s = "Ctrl + [" + s + "]";
        }
        return s;
    }

}
