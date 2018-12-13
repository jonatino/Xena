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

import org.xena.cs.*
import org.xena.gui.Overlay
import org.xena.keylistener.GlobalKeyboard
import org.xena.keylistener.NativeKeyEvent
import org.xena.keylistener.NativeKeyListener
import org.xena.offsets.OffsetManager
import org.xena.offsets.offsets.ClientOffsets.dwEntityList
import org.xena.offsets.offsets.ClientOffsets.dwGameRulesProxy
import org.xena.offsets.offsets.ClientOffsets.dwGlowObject
import org.xena.offsets.offsets.ClientOffsets.dwLocalPlayer
import org.xena.offsets.offsets.EngineOffsets.dwClientState_State
import org.xena.offsets.offsets.EngineOffsets.dwInGame
import org.xena.offsets.offsets.EngineOffsets.dwLocalPlayerIndex
import org.xena.offsets.offsets.EngineOffsets.dwMaxPlayer
import org.xena.offsets.offsets.NetVarOffsets.dwIndex
import org.xena.offsets.offsets.NetVarOffsets.iTeamNum
import org.xena.offsets.offsets.NetVarOffsets.m_SurvivalGameRuleDecisionTypes
import org.xena.plugin.PluginManager
import org.xena.plugin.official.AimAssistPlugin
import org.xena.plugin.official.ForceAimPlugin
import org.xena.plugin.official.GlowESPPlugin
import org.xena.plugin.official.NoFlashPlugin
import org.xena.utils.Logging
import java.lang.System.currentTimeMillis
import javax.swing.SwingUtilities


@JvmField
val process = OffsetManager.process()
@JvmField
val engineModule = OffsetManager.engineModule()
@JvmField
val clientModule = OffsetManager.clientModule()

object Xena : NativeKeyListener {
	
	const val CYCLE_TIME = 8
	
	val pluginManager = PluginManager()
	
	var gameMode = GameTypes.UNKNOWN
	
	var lastCycle = 0L
	
	var lastRefresh = 0L
	
	val overlay: Overlay by lazy { Overlay.open(this) }
	
	val keylistener: GlobalKeyboard by lazy { GlobalKeyboard.register(this) }
	
	@JvmStatic
	@Throws(InterruptedException::class)
	fun run(cycleMS: Int) {
		//pluginManager.add(RadarPlugin()); //This may cause bans be careful
		pluginManager.add(GlowESPPlugin())
		pluginManager.add(ForceAimPlugin())
		//pluginManager.add(SkinChangerPlugin())
		//pluginManager.add(SpinBotPlugin())
		pluginManager.add(NoFlashPlugin())
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
					Logging.debug("Failed entity.size() check ${entities.size()}")
					Thread.sleep(1000)
					continue
				}
				
				entities.forEach { it?.update() }
				
				val rulesProxyAddress = clientModule.readUnsignedInt(dwGameRulesProxy.toLong())
				val gameRuleTypes = process.readUnsignedInt(rulesProxyAddress + m_SurvivalGameRuleDecisionTypes.toLong())
				val gameMode = if (gameRuleTypes != 0L) GameTypes.DANGERZONE else GameTypes.MATCHMAKING
				updateGameMode(gameMode)
				
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
			var failed = false
			
			val myAddress = clientModule.readUnsignedInt(dwLocalPlayer.toLong())
			if (!failed && myAddress < 0x200) {
				clearPlayers()
				failed = true
				Logging.debug("Failed myAddress check $myAddress")
			}
			
			val myTeam = process.readUnsignedInt(myAddress + iTeamNum).toInt()
			if (!failed && (myTeam != 2 && myTeam != 3)) {
				clearPlayers()
				failed = true
				Logging.debug("Failed myTeam check $myTeam")
			}
			
			val objectCount = clientModule.readUnsignedInt((dwGlowObject + 4).toLong())
			if (!failed && objectCount <= 0) {
				clearPlayers()
				failed = true
				Logging.debug("Failed objectCount check $objectCount")
			}
			
			val myIndex = process.readUnsignedInt(myAddress + dwIndex) - 1
			if (!failed && (myIndex < 0 || myIndex >= objectCount)) {
				clearPlayers()
				failed = true
				Logging.debug("Failed myIndex check $myIndex")
			}
			
			val enginePointer = engineModule.readUnsignedInt(dwClientState_State.toLong())
			if (!failed && enginePointer <= 0) {
				clearPlayers()
				failed = true
				Logging.debug("Failed enginePointer check $enginePointer")
			}
			
			val inGame = process.readUnsignedInt(enginePointer + dwInGame).toInt()
			if (!failed && inGame != 6) {
				clearPlayers()
				failed = true
				Logging.debug("Failed inGame check $inGame")
			}
			
			if (!failed && (myAddress <= 0 || myIndex < 0 || myIndex > 0x200 || myIndex > objectCount || objectCount <= 0)) {
				clearPlayers()
				failed = true
				Logging.debug("Failed myAddress check $myAddress, myIndex check $myIndex, objectCount check $objectCount")
			}
			
			if (failed) {
				updateGameMode(GameTypes.UNKNOWN)
				Thread.sleep(10000)
				continue
			}
			break
		}
	}
	
	private fun updateClientState(clientState: ClientState) {
		val address = engineModule.readUnsignedInt(dwClientState_State.toLong())
		if (address < 0) {
			throw IllegalStateException("Could not find client state")
		}
		clientState.setAddress(address)
		clientState.inGame = process.readUnsignedInt(address + dwInGame)
		clientState.maxPlayer = process.readUnsignedInt(address + dwMaxPlayer)
		clientState.localPlayerIndex = process.readUnsignedInt(address + dwLocalPlayerIndex)
	}
	
	private fun clearPlayers() = removePlayers()
	
	private fun updateEntityList() {
		val entityCount = clientModule.readUnsignedInt((dwGlowObject + 4).toLong())
		val myAddress = clientModule.readUnsignedInt(dwLocalPlayer.toLong())
		
		for (i in 0 until entityCount) {
			val entityAddress = clientModule.readUnsignedInt(dwEntityList + (i - 1) * 0x10)
			
			if (entityAddress < 0x200) continue
			
			val type = EntityType.byAddress(entityAddress) ?: continue
			
			val team = process.readInt(entityAddress + iTeamNum)
			
			if (team != 2 && team != 3 || type !== EntityType.CCSPlayer) {
				continue
			}
			
			var entity = entities[entityAddress]
			if (entity == null) {
				entity = if (myAddress == entityAddress) {
					me
				} else {
					Player()
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
	
	private fun updateGameMode(newGameMode: GameTypes) {
		if (newGameMode != gameMode) {
			SwingUtilities.invokeLater {
				overlay.repaint()
			}
		}
		gameMode = newGameMode
	}
	
	fun isDangerZone() = gameMode == GameTypes.DANGERZONE
	
	enum class GameTypes {
		UNKNOWN, MATCHMAKING, DANGERZONE
	}
}
