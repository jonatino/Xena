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

package org.xena.plugin.official

import com.github.jonatino.netvars.NetVars
import com.github.jonatino.offsets.Offsets.m_dwGlowObject
import org.xena.Indexer
import org.xena.Xena
import org.xena.cs.*
import org.xena.logging.Logger
import org.xena.plugin.Plugin
import org.xena.plugin.PluginManifest
import org.xena.plugin.utils.AngleUtils

@PluginManifest(name = "Glow ESP", description = "Make entities glow on your screen.")
class GlowESPPlugin(logger: Logger, xena: Xena) : Plugin(logger, xena) {
	
	private val angleUtils: AngleUtils
	
	init {
		angleUtils = AngleUtils(this, 0f, 0f, 0f, 0f, 0f)
/*		bsp = new BSPParser("E:\\Games\\Steam\\SteamApps\\common\\Counter-Strike Global Offensive\\csgo\\maps\\de_dust2.bsp");
		bsp.parse();*/
	}
	
	override fun pulse(clientState: ClientState, me: Me, entities: Indexer<GameEntity>) {
		val pointerGlow = client().readUnsignedInt(m_dwGlowObject.toLong())
		val glowObjectCount = client().readUnsignedInt((m_dwGlowObject + 4).toLong())
		
		for (i in 0..glowObjectCount - 1) {
			val glowObjectPointer = pointerGlow + i * 56
			val entityAddress = process().readUnsignedInt(glowObjectPointer)
			
			if (entityAddress < 0x200) {
				continue
			}
			
			val entity = entities[entityAddress]
			if (entity != null) {
				try {
					val spottedMask = process().readInt(entity.address()) and (1 shl clientState.localPlayerIndex.toInt())
					//System.out.println(spottedMask);
					if (spottedMask == 0) {
						//continue;
					}
					val c = getColor(entity)
					for (x in 0..3) {
						process().writeFloat(glowObjectPointer + (x + 1) * 4, c[x] / 255f)
						process().writeByte(entityAddress + 0x70 + x.toLong(), c[x])
					}
					
					process().writeBoolean(glowObjectPointer + 0x24, true)
					process().writeBoolean(glowObjectPointer + 0x25, false)
					process().writeBoolean(glowObjectPointer + 0x26, false)
				} catch (ignored: Throwable) {
					ignored.printStackTrace()
				}
				
			}
		}
	}
	
	private fun getColor(entity: GameEntity): IntArray {
		if (entity.team == 3) {
			return TEAM_CT
		}
		if (entity.isBombCarrier) {
			return BOMB_CARRY
		}
		if (entity.type === EntityType.CC4) {
			return BOMB_DROPPED
		}
		return TEAM_T
	}
	
	companion object {
		
		private val TEAM_CT by lazy { intArrayOf(114, 155, 221, 153) }
		private val TEAM_T by lazy { intArrayOf(224, 175, 86, 153) }
		private val BOMB_CARRY by lazy { intArrayOf(255, 0, 0, 200) }
		private val BOMB_DROPPED by lazy { intArrayOf(133, 142, 30, 200) }
		private val m_bSpottedByMask by lazy { NetVars.byName("DT_BaseEntity", "m_bSpottedByMask") }
	}
	
}
