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

import com.sun.jna.Pointer
import org.xena.cs.*
import org.xena.gui.Overlay
import org.xena.keylistener.GlobalKeyboard
import org.xena.keylistener.NativeKeyEvent
import org.xena.keylistener.NativeKeyListener
import org.xena.natives.User32
import org.xena.offsets.OffsetManager
import org.xena.offsets.offsets.ClientOffsets.dwEntityList
import org.xena.offsets.offsets.ClientOffsets.dwGameRulesProxy
import org.xena.offsets.offsets.ClientOffsets.dwGlowObject
import org.xena.offsets.offsets.ClientOffsets.dwLocalPlayer
import org.xena.offsets.offsets.EngineOffsets.dwClientState
import org.xena.offsets.offsets.EngineOffsets.dwInGame
import org.xena.offsets.offsets.EngineOffsets.dwLocalPlayerIndex
import org.xena.offsets.offsets.EngineOffsets.dwMaxPlayer
import org.xena.offsets.offsets.EngineOffsets.dwSignOnState
import org.xena.offsets.offsets.NetVarOffsets.dwIndex
import org.xena.offsets.offsets.NetVarOffsets.iTeamNum
import org.xena.offsets.offsets.NetVarOffsets.m_SurvivalGameRuleDecisionTypes
import org.xena.plugin.PluginManager
import org.xena.plugin.official.*
import org.xena.utils.Logging
import org.xena.utils.SignOnState
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
	
	var state = SignOnState.MAIN_MENU
	
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
		pluginManager.add(BunnyHopPlugin())
		
		println("We're all set. Welcome to the new Xena platform!")
		println("Use numpad or ALT+nums to toggle corresponding plugins.")
		
		overlay.repaint()
		
		System.gc()
		
		val hwd = User32.FindWindowA(null, "Counter-Strike: Global Offensive")
		
		while (!Thread.interrupted()) {
			try {
				val stamp = currentTimeMillis()
				
				checkGameStatus()
				
				updateClientState(clientState)
				
				updateState(clientState.state)
				
				if (clientState.state != SignOnState.IN_GAME) {
					Thread.sleep(10000)
					continue
				}
				
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
				
				if (Pointer.nativeValue(hwd.pointer) == User32.GetForegroundWindow()) {
					for (plugin in pluginManager) {
						if (plugin.canPulse()) {
							plugin.pulse(clientState, me, entities)
						}
					}
				} else {
					Logging.debug("CSGO window not in focus, skipping cycle...")
					Thread.sleep(1000)
					continue
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
			try {
				val myAddress = clientModule.readUnsignedInt(dwLocalPlayer.toLong())
				if (myAddress < 0x200) {
					clearPlayers()
					Logging.debug("Failed myAddress check $myAddress")
					failed()
					continue
				}
				
				val myTeam = process.readUnsignedInt(myAddress + iTeamNum).toInt()
				if (myTeam != 2 && myTeam != 3) {
					clearPlayers()
					Logging.debug("Failed myTeam check $myTeam")
					failed()
					continue
				}
				
				val objectCount = clientModule.readUnsignedInt((dwGlowObject + 4).toLong())
				if (objectCount <= 0) {
					clearPlayers()
					Logging.debug("Failed objectCount check $objectCount")
					failed()
					continue
				}
				
				val myIndex = process.readUnsignedInt(myAddress + dwIndex) - 1
				if (myIndex < 0 || myIndex >= objectCount) {
					clearPlayers()
					Logging.debug("Failed myIndex check $myIndex")
					failed()
					continue
				}
				
				val enginePointer = engineModule.readUnsignedInt(dwClientState.toLong())
				if (enginePointer <= 0) {
					clearPlayers()
					Logging.debug("Failed enginePointer check $enginePointer")
					failed()
					continue
				}
				
				val inGame = process.readUnsignedInt(enginePointer + dwInGame).toInt()
				if (inGame != 6) {
					clearPlayers()
					Logging.debug("Failed inGame check $inGame")
					failed()
					continue
				}
				
				if (myAddress <= 0 || myIndex < 0 || myIndex > 0x200 || myIndex > objectCount || objectCount <= 0) {
					clearPlayers()
					Logging.debug("Failed myAddress check $myAddress, myIndex check $myIndex, objectCount check $objectCount")
					failed()
					continue
				}
			} catch (e: Exception) {
				failed()
				if (Settings.DEBUG)
					e.printStackTrace()
			}

			break
		}
	}
	
	private fun failed() {
		updateGameMode(GameTypes.UNKNOWN)
		Thread.sleep(5000)
	}
	
	private fun updateClientState(clientState: ClientState) {
		val address = engineModule.readUnsignedInt(dwClientState.toLong())
		if (address < 0) {
			throw IllegalStateException("Could not find client state")
		}
		clientState.setAddress(address)
		clientState.inGame = process.readUnsignedInt(address + dwInGame)
		clientState.maxPlayer = process.readUnsignedInt(address + dwMaxPlayer)
		clientState.localPlayerIndex = process.readUnsignedInt(address + dwLocalPlayerIndex)
		clientState.state = SignOnState[process.readInt(address + dwSignOnState)]
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
			
			if (type == EntityType.CFists) {
				updateGameMode(GameTypes.DANGERZONE)
			}
			
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
	
	private fun updateState(newGameState: SignOnState) {
		if (newGameState != state) {
			SwingUtilities.invokeLater {
				overlay.repaint()
			}
		}
		state = newGameState
	}
	
	fun isDangerZone() = gameMode == GameTypes.DANGERZONE
	
	enum class GameTypes {
		UNKNOWN, MATCHMAKING, DANGERZONE
	}
	
}
