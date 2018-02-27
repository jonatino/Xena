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
import org.xena.cs.ClientState
import org.xena.cs.GameEntity
import org.xena.cs.Me
import org.xena.offsets.offsets.ClientOffsets.flFallbackWear
import org.xena.offsets.offsets.ClientOffsets.iEntityQuality
import org.xena.offsets.offsets.ClientOffsets.iItemIDHigh
import org.xena.offsets.offsets.ClientOffsets.nFallbackPaintKit
import org.xena.offsets.offsets.ClientOffsets.nFallbackSeed
import org.xena.offsets.offsets.ClientOffsets.nFallbackStatTrak
import org.xena.plugin.Plugin
import org.xena.plugin.PluginManifest
import org.xena.process

@PluginManifest(name = "Skin Changer", description = "Skin changer plugin.")
class SkinChangerPlugin : Plugin() {
	
	private val DEFAULT_SKIN_SEED = 0
	private val DEFAULT_STATTRAK = -1 // -1 for no StatTrak, 0+ for StatTrak amount
	private val DEFAULT_WEAR = 0.0001f // lower = less wear, higher = more wear
	private val DEFAULT_QUALITY = 1
	
	override fun pulse(clientState: ClientState, me: Me, entities: Indexer<GameEntity>) {
		/*  for (weaponData in me.weaponIds) {
			  val weapon = Weapons.byID(weaponData[0].toInt())
			  if (weapon != null && weapon.customSkin) {
				  for (i in 0..4) {
					  appySkin(weaponData[1], weapon.skin)
				  }
			  }
		  }*/
		//if (NativeKeyUtils.isKeyDown(KeyEvent.VK_F1))
		//engineModule.writeInt(clientState.address() + m_dwForceFullUpdate, -1)
	}
	
	private fun appySkin(weaponAddress: Long, skinID: Int, skinSeed: Int = DEFAULT_SKIN_SEED, statTrak: Int = DEFAULT_STATTRAK, wear: Float = DEFAULT_WEAR, quality: Int = DEFAULT_QUALITY) {
		process.writeInt(weaponAddress + iItemIDHigh, 1)
		process.writeInt(weaponAddress + nFallbackPaintKit, skinID)
		process.writeInt(weaponAddress + nFallbackSeed, skinSeed)
		process.writeInt(weaponAddress + nFallbackStatTrak, statTrak)
		process.writeInt(weaponAddress + iEntityQuality, quality)
		process.writeFloat(weaponAddress + flFallbackWear, wear)
	}
	
}
