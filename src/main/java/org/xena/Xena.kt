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

import com.github.jonatino.OffsetManager
import com.github.jonatino.offsets.Offsets.*
import org.xena.cs.*
import org.xena.gui.Overlay
import org.xena.keylistener.GlobalKeyboard
import org.xena.keylistener.NativeKeyEvent
import org.xena.keylistener.NativeKeyListener
import org.xena.plugin.PluginManager
import org.xena.plugin.official.AimAssistPlugin
import org.xena.plugin.official.ForceAimPlugin
import org.xena.plugin.official.GlowESPPlugin
import org.xena.plugin.official.NoFlashPlugin
import java.lang.System.currentTimeMillis


@JvmField
val process = OffsetManager.process()
@JvmField
val engineModule = OffsetManager.engineModule()
@JvmField
val clientModule = OffsetManager.clientModule()

object Xena : NativeKeyListener {
	
	const val CYCLE_TIME = 8
	
	val pluginManager = PluginManager()
	
	var lastCycle = 0L
	var lastRefresh = 0L
	
	val overlay by lazy { Overlay.open(this) }
	
	val keylistener by lazy { GlobalKeyboard.register(this) }
	
	@JvmStatic
	@Throws(InterruptedException::class)
	fun run(cycleMS: Int) {
		//pluginManager.enable(new RadarPlugin());
		pluginManager.add(GlowESPPlugin())
		pluginManager.add(ForceAimPlugin())
		//pluginManager.add(SkinChangerPlugin())
		//pluginManager.add(SpinBotPlugin())
		pluginManager.add(NoFlashPlugin());
		pluginManager.add(AimAssistPlugin())
		
		println("We're all set. Welcome to the new Xena platform!")
		println("Use numpad or ALT+nums to toggle corresponding plugins.")
		
		overlay.repaint()
		
		System.gc()
		
		while (!Thread.interrupted()) {
			try {
				val stamp = currentTimeMillis()
				
				checkGameStatus()
				
				updateClientState(clientState)
				if (System.currentTimeMillis() - lastRefresh >= 10000) {
					clearPlayers()
					lastRefresh = System.currentTimeMillis()
				}
				
				updateEntityList()
				
				if (entities.size() <= 0) {
					Thread.sleep(1000)
					continue
				}
				
				entities.forEach({ it?.update() })
				
				for (plugin in pluginManager) {
					if (plugin.canPulse()) {
						plugin.pulse(clientState, me, entities)
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
	
	fun clearPlayers() = removePlayers()
	
	private fun updateEntityList() {
		val entityCount = clientModule.readUnsignedInt((m_dwGlowObject + 4).toLong())
		val myAddress = clientModule.readUnsignedInt(m_dwLocalPlayer.toLong())
		
		for (i in 0..entityCount - 1) {
			val entityAddress = clientModule.readUnsignedInt(m_dwEntityList + i * 0x10)
			
			if (entityAddress < 0x200) continue
			
			val type = EntityType.byAddress(entityAddress) ?: continue
			
			val team = process.readInt(entityAddress + m_iTeamNum)
			if (team != 2 && team != 3 || type !== EntityType.CCSPlayer) {
				continue
			}
			
			var entity = entities[entityAddress]
			if (entity == null) {
				if (myAddress == entityAddress) {
					entity = me
				} else {
					entity = Player()
				}
				entity.setAddress(entityAddress)
				Game.register(entity)
			}
			entity.team = team
		}
	}
	
	override fun onKeyPressed(event: NativeKeyEvent): Boolean {
		if (overlay.isVisible) {
			if (event.keyCode == Settings.HIDE_GUI_KEY && !event.hasModifiers()) {
				overlay.minimize()
				return true
			}
		}
		return false
	}
	
	override fun onKeyReleased(event: NativeKeyEvent) = false
	
}
