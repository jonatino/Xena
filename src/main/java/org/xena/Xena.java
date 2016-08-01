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

package org.xena;

import com.github.jonatino.process.Module;
import com.github.jonatino.process.Process;
import lombok.Getter;
import org.xena.cs.*;
import org.xena.gui.Overlay;
import org.xena.keylistener.GlobalKeyboard;
import org.xena.keylistener.NativeKeyEvent;
import org.xena.keylistener.NativeKeyListener;
import org.xena.logging.Logger;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManager;
import org.xena.plugin.official.AimAssistPlugin;
import org.xena.plugin.official.ForceAimPlugin;
import org.xena.plugin.official.GlowESPPlugin;
import org.xena.plugin.official.SkinChangerPlugin;

import java.awt.event.KeyEvent;

import static com.github.jonatino.offsets.Offsets.*;
import static java.lang.System.currentTimeMillis;

public final class Xena implements NativeKeyListener {

	private final Game game = Game.current();

	public static final int CYCLE_TIME = 0;

	@Getter
	private final PluginManager pluginManager;

	@Getter
	private final Process process;

	@Getter
	private final Module clientModule;

	@Getter
	private final Module engineModule;

	@Getter
	private boolean paused;

	@Getter
	private long lastCycle;
	private long lastRefresh;

	@Getter
	private Overlay overlay;

	@Getter
	private GlobalKeyboard keylistener;

	public Xena(Process process, Module clientModule, Module engineModule, PluginManager pluginManager) {
		this.pluginManager = pluginManager;
		this.process = process;
		this.clientModule = clientModule;
		this.engineModule = engineModule;
	}

	void run(Logger logger, int cycleMS) throws InterruptedException {
		keylistener = GlobalKeyboard.register(this);

		//pluginManager.enable(new RadarPlugin(logger, process, clientModule, engineModule, taskManager));
		pluginManager.add(new GlowESPPlugin(logger, this));
		//pluginManager.add(new RCS(logger, this, taskManager));
		pluginManager.add(new ForceAimPlugin(logger, this));
		pluginManager.add(new SkinChangerPlugin(logger, this));
		//pluginManager.add(new SpinBotPlugin(logger, this));
		//pluginManager.add(new NoFlashPlugin(logger, this, taskManager));
		pluginManager.add(new AimAssistPlugin(logger, this));

		overlay = Overlay.open(this);

		logger.info("We're all set. Welcome to the new Xena platform!");
		logger.info("Use numpad or ALT+nums to toggle corresponding plugins.");

		while (!Thread.interrupted()) {
			try {
				long stamp = currentTimeMillis();

				checkGameStatus();

				updateClientState(game.clientState());
				if (System.currentTimeMillis() - lastRefresh >= 2000) {
					clearPlayers();
					updateEntityList();
					lastRefresh = System.currentTimeMillis();
				}
				game.entities().forEach(GameEntity::update);

				for (Plugin plugin : pluginManager) {
					if (plugin.canPulse()) {
						plugin.pulse(game.clientState(), game.me(), game.entities());
					}
				}

				lastCycle = currentTimeMillis() - stamp;
				long sleep = cycleMS - lastCycle;
				if (sleep > 0) {
					Thread.sleep(sleep);
				}
			} catch (Throwable t) {
				t.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

	private void checkGameStatus() throws InterruptedException {
		while (true) {
			long myAddress = clientModule.readUnsignedInt(m_dwLocalPlayer);
			if (myAddress < 0x200) {
				clearPlayers();
				Thread.sleep(3000);
				continue;
			}

			long myTeam = process.readUnsignedInt(myAddress + m_iTeamNum);
			if (myTeam != 2 && myTeam != 3) {
				clearPlayers();
				Thread.sleep(3000);
				continue;
			}

			long objectCount = clientModule.readUnsignedInt(m_dwGlowObject + 4);
			if (objectCount <= 0) {
				clearPlayers();
				Thread.sleep(3000);
				continue;
			}

			long myIndex = process.readUnsignedInt(myAddress + m_dwIndex) - 1;
			if (myIndex < 0 || myIndex >= objectCount) {
				clearPlayers();
				Thread.sleep(3000);
				continue;
			}

			long enginePointer = engineModule.readUnsignedInt(m_dwClientState);
			if (enginePointer <= 0) {
				clearPlayers();
				Thread.sleep(3000);
				continue;
			}

			long inGame = process.readUnsignedInt(enginePointer + m_dwInGame);
			if (inGame != 6) {
				clearPlayers();
				Thread.sleep(3000);
				continue;
			}

			if (myAddress <= 0 || myIndex < 0 || myIndex > 0x200 || myIndex > objectCount || objectCount <= 0) {
				clearPlayers();
				Thread.sleep(3000);
				continue;
			}
			break;
		}
	}

	private void updateClientState(ClientState clientState) {
		long address = engineModule.readUnsignedInt(m_dwClientState);
		if (address < 0) {
			throw new IllegalStateException("Could not find client state");
		}
		clientState.setAddress(address);
		clientState.setInGame(process.readUnsignedInt(address + m_dwInGame));
		clientState.setMaxPlayer(process.readUnsignedInt(address + m_dwMaxPlayer));
		clientState.setLocalPlayerIndex(process.readUnsignedInt(address + m_dwLocalPlayerIndex));
	}

	public void clearPlayers() {
		game.removePlayers();
	}

	private void updateEntityList() {
		long pointerGlow = clientModule.readUnsignedInt(m_dwGlowObject);
		long entityCount = clientModule.readUnsignedInt(m_dwGlowObject + 4);
		long myAddress = clientModule.readUnsignedInt(m_dwLocalPlayer);

		System.out.println(process.findModule("engine.dll").address());
		System.out.println(process.findModule("client.dll").address());
		for (int i = 0; i < entityCount; i++) {
			long entityAddress = clientModule.readUnsignedInt(m_dwEntityList + (i * 0x10));
			long glowObjectPointer = pointerGlow + (i * 56);

			if (entityAddress == myAddress) {
				System.out.println("Found my address");
			}
			if (entityAddress == 0) {
				//entityAddress = process.readUnsignedInt(glowObjectPointer);
			}

			if (entityAddress < 0x200) {
				continue;
			}

			EntityType type = getEntityType(entityAddress);
			if (type == null) {
				continue;
			}

			//System.out.println(type);
			long team = process.readUnsignedInt(entityAddress + m_iTeamNum);
			if (type == EntityType.CC4 && team != 2) {
				continue;
			}
			if (team != 2 && team != 3 || (type != EntityType.CCSPlayer && type != EntityType.CC4)) {
				continue;
			}

			GameEntity entity = game.get(entityAddress);
			if (entity == null) {
				if (myAddress == entityAddress) {
					entity = Game.current().me();
				} else if (type == EntityType.CCSPlayer) {
					entity = new Player();
				} else {
					entity = new GameEntity();
				}
				entity.setAddress(entityAddress);
				game.register(entity);
				System.out.println("Entity: " + entity + ", " + game.entities().size());
			}
			entity.setClassId(type.getId());
			entity.setGlowPointer(glowObjectPointer);
		}
	}

	public EntityType getEntityType(long address) {
		try {
			long vt = process.readUnsignedInt(address + 0x8);
			if (vt <= 0) {
				return null;
			}
			long fn = process.readUnsignedInt(vt + 0x8);
			if (fn <= 0) {
				return null;
			}
			long cls = process.readUnsignedInt(fn + 0x1);
			if (cls <= 0) {
				return null;
			}
			long classId = process.readUnsignedInt(cls + 20);
			return EntityType.byId(classId);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean onKeyPressed(NativeKeyEvent event) {
		if (overlay != null && overlay.isVisible()) {
			if (event.keyCode() == KeyEvent.VK_F9 && !event.hasModifiers()) {
				overlay.minimize();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onKeyReleased(NativeKeyEvent event) {
		return false;
	}

}
