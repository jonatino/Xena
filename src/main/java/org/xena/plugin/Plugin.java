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

package org.xena.plugin;

import org.xena.Indexer;
import org.xena.Xena;
import org.xena.cs.ClientState;
import org.xena.cs.GameEntity;
import org.xena.cs.Me;
import org.xena.keylistener.GlobalKeyboard;
import org.xena.keylistener.NativeKeyCombination;

import java.awt.event.KeyEvent;

public abstract class Plugin {
	
	private static int pluginUid;
	private final int uid;
	
	private long sleep;
	
	private boolean enabled = true;
	
	public Plugin() {
		this.uid = pluginUid++;
		
		Xena.INSTANCE.getKeylistener().registerHotkey(new NativeKeyCombination((e) -> {
			toggle();
			Xena.INSTANCE.getOverlay().repaint();
			e.consume();
		}, GlobalKeyboard.ALT, KeyEvent.VK_NUMPAD0 + uid, KeyEvent.VK_0 + uid));
	}
	
	public void toggle() {
		enabled = !enabled;
	}
	
	public boolean canPulse() {
		return enabled && System.currentTimeMillis() >= sleep;
	}
	
	protected void sleep(long ms) {
		sleep = System.currentTimeMillis() + ms;
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void disable() {
		enabled = false;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public abstract void pulse(ClientState clientState, Me me, Indexer<GameEntity> entities);
	
	public int uid() {
		return uid;
	}
	
	@Override
	public String toString() {
		return uid + ": " + getClass().getSimpleName();
	}
	
}
