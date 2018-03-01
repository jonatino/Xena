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

import org.xena.Indexer
import org.xena.Settings.*
import org.xena.clientModule
import org.xena.cs.ClientState
import org.xena.cs.GameEntity
import org.xena.cs.Me
import org.xena.cs.get
import org.xena.offsets.offsets.ClientOffsets.dwGlowObject
import org.xena.plugin.Plugin
import org.xena.plugin.PluginManifest
import org.xena.process

@PluginManifest(name = "Glow ESP", description = "Make entities glow on your screen.")
class GlowESPPlugin : Plugin() {
	
	override fun pulse(clientState: ClientState, me: Me, entities: Indexer<GameEntity>) {
		val pointerGlow = clientModule.readUnsignedInt(dwGlowObject.toLong())
		val glowObjectCount = clientModule.readUnsignedInt((dwGlowObject + 4).toLong())
		
		for (i in 0 until glowObjectCount) {
			val glowObjectPointer = pointerGlow + i * 56
			val entityAddress = process.readUnsignedInt(glowObjectPointer)
			
			val entity = entities[entityAddress] ?: continue
			
			val color = getColor(entity)
			
			for (x in 0..3) {
				process.writeFloat(glowObjectPointer + (x + 1) * 4, color[x] / 255f)
				process.writeByte(entityAddress + 0x70 + x, color[x])
			}
			process.writeBoolean(glowObjectPointer + 0x24, true)
		}
	}
	
	private fun getColor(entity: GameEntity): IntArray {
		return when {
			entity.team == 2 -> ESP_T
			entity.isBombCarrier -> ESP_BOMB_CARRY
			else -> ESP_CT
		}
	}
	
}
