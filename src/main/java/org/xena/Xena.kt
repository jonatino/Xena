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

package org.xena

import com.github.jonatino.offsets.Offsets.*
import com.github.jonatino.process.Module
import com.github.jonatino.process.Process
import org.xena.cs.*
import org.xena.gui.Overlay
import org.xena.keylistener.GlobalKeyboard
import org.xena.keylistener.NativeKeyEvent
import org.xena.keylistener.NativeKeyListener
import org.xena.logging.Logger
import org.xena.plugin.PluginManager
import org.xena.plugin.official.AimAssistPlugin
import org.xena.plugin.official.ForceAimPlugin
import org.xena.plugin.official.GlowESPPlugin
import org.xena.plugin.official.SkinChangerPlugin
import java.awt.event.KeyEvent
import java.lang.System.currentTimeMillis
import java.util.function.Consumer

class Xena(val process: Process, val clientModule: Module, val engineModule: Module, val pluginManager: PluginManager) : NativeKeyListener {
	
	val game = Game.current()
	
	val paused: Boolean = false
	
	var lastCycle: Long = 0
	var lastRefresh: Long = 0
	
	val overlay: Overlay by lazy { Overlay.open(this) }
	
	val keylistener: GlobalKeyboard by lazy { GlobalKeyboard.register(this) }
	
	@Throws(InterruptedException::class)
	fun run(logger: Logger, cycleMS: Int) {
		//pluginManager.enable(new RadarPlugin(logger, this));
		pluginManager.add(GlowESPPlugin(logger, this))
		//pluginManager.add(new RCS(logger, this));
		pluginManager.add(ForceAimPlugin(logger, this))
		pluginManager.add(SkinChangerPlugin(logger, this))
		//pluginManager.add(new SpinBotPlugin(logger, this));
		//pluginManager.add(new NoFlashPlugin(logger, this));
		pluginManager.add(AimAssistPlugin(logger, this))
		
		logger.info("We're all set. Welcome to the new Xena platform!")
		logger.info("Use numpad or ALT+nums to toggle corresponding plugins.")
		
		overlay.repaint()
		
		while (!Thread.interrupted()) {
			try {
				val stamp = currentTimeMillis()
				
				checkGameStatus()
				
				updateClientState(game.clientState())
				/*			if (System.currentTimeMillis() - lastRefresh >= 2000) {
								clearPlayers()
								updateEntityList()
								lastRefresh = System.currentTimeMillis()
							}*/
				updateEntityList()
				if (game.entities().size() <= 0) {
					Thread.sleep(1000)
					continue
				}
				
				
				
				
				game.entities().forEach(Consumer<GameEntity> { it.update() })
				
				for (plugin in pluginManager) {
					if (plugin.canPulse()) {
						plugin.pulse(game.clientState(), game.me(), game.entities())
					}
				}
				
				lastCycle = currentTimeMillis() - stamp
				val sleep = cycleMS - lastCycle
				if (sleep > 0) {
					Thread.sleep(sleep)
				}
			} catch (t: Throwable) {
				t.printStackTrace()
				Thread.currentThread().interrupt()
			}
			
		}
	}
	
	@Throws(InterruptedException::class)
	private fun checkGameStatus() {
		while (true) {
			val myAddress = clientModule.readUnsignedInt(m_dwLocalPlayer.toLong())
			if (myAddress < 0x200) {
				clearPlayers()
				Thread.sleep(10000)
				continue
			}
			
			val myTeam = process.readUnsignedInt(myAddress + m_iTeamNum).toInt()
			if (myTeam != 2 && myTeam != 3) {
				clearPlayers()
				Thread.sleep(10000)
				continue
			}
			
			val objectCount = clientModule.readUnsignedInt((m_dwGlowObject + 4).toLong())
			if (objectCount <= 0) {
				clearPlayers()
				Thread.sleep(10000)
				continue
			}
			
			val myIndex = process.readUnsignedInt(myAddress + m_dwIndex) - 1
			if (myIndex < 0 || myIndex >= objectCount) {
				clearPlayers()
				Thread.sleep(10000)
				continue
			}
			
			val enginePointer = engineModule.readUnsignedInt(m_dwClientState.toLong())
			if (enginePointer <= 0) {
				clearPlayers()
				Thread.sleep(10000)
				continue
			}
			
			val inGame = process.readUnsignedInt(enginePointer + m_dwInGame).toInt()
			if (inGame != 6) {
				clearPlayers()
				Thread.sleep(10000)
				continue
			}
			
			if (myAddress <= 0 || myIndex < 0 || myIndex > 0x200 || myIndex > objectCount || objectCount <= 0) {
				clearPlayers()
				Thread.sleep(10000)
				continue
			}
			break
		}
	}
	
	private fun updateClientState(clientState: ClientState) {
		val address = engineModule.readUnsignedInt(m_dwClientState.toLong())
		if (address < 0) {
			throw IllegalStateException("Could not find client state")
		}
		clientState.setAddress(address)
		clientState.inGame = process.readUnsignedInt(address + m_dwInGame)
		clientState.maxPlayer = process.readUnsignedInt(address + m_dwMaxPlayer)
		clientState.localPlayerIndex = process.readUnsignedInt(address + m_dwLocalPlayerIndex)
	}
	
	fun clearPlayers() = game.removePlayers()
	
	private fun updateEntityList() {
		val pointerGlow = clientModule.readUnsignedInt(m_dwGlowObject.toLong())
		val entityCount = clientModule.readUnsignedInt((m_dwGlowObject + 4).toLong())
		val myAddress = clientModule.readUnsignedInt(m_dwLocalPlayer.toLong())
		
		for (i in 0..entityCount - 1) {
			val entityAddress = clientModule.readUnsignedInt((m_dwEntityList + i * 0x10).toLong())
			//val glowObjectPointer = pointerGlow + i * 56
			
			if (entityAddress == 0.toLong()) {
				//entityAddress = process.readUnsignedInt(glowObjectPointer);
			}
			
			if (entityAddress < 0x200) {
				continue
			}
			
			val type = EntityType.byAddress(entityAddress) ?: continue
			val team = process.readUnsignedInt(entityAddress + m_iTeamNum).toInt()
			if (team != 2 && team != 3 || type !== EntityType.CCSPlayer) {
				continue
			}
			
			var entity = game.get(entityAddress)
			if (entity == null) {
				if (myAddress == entityAddress) {
					entity = Game.current().me()
				} else if (type === EntityType.CCSPlayer) {
					entity = Player()
				} else {
					throw RuntimeException("Unknown entity! $team, $type")
				}
				entity!!.setAddress(entityAddress)
				game.register(entity)
				println(entity)
			}
			entity.team = team
			entity.classId = type.id
		}
	}
	
	override fun onKeyPressed(event: NativeKeyEvent): Boolean {
		if (overlay.isVisible) {
			if (event.keyCode == KeyEvent.VK_F9 && !event.hasModifiers()) {
				overlay.minimize()
				return true
			}
		}
		return false
	}
	
	override fun onKeyReleased(event: NativeKeyEvent) = false
	
	companion object {
		
		val CYCLE_TIME = 8
		
	}
	
}
