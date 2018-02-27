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

package org.xena.offsets.offsets

import com.github.jonatino.misc.Strings
import org.xena.offsets.OffsetManager.clientModule
import org.xena.offsets.misc.PatternScanner
import org.xena.offsets.misc.PatternScanner.byPattern
import org.xena.offsets.netvars.NetVars.byName
import java.io.IOException
import java.lang.reflect.Field
import java.nio.file.Files
import java.nio.file.Paths


/**
 * Created by Jonathan on 11/13/2015.
 */
object ClientOffsets {
	
	/**
	 * Client.dll offsets
	 */
	@JvmField
	var m_dwRadarBase: Int = 0
	@JvmField
	var m_dwWeaponTable: Int = 0
	@JvmField
	var m_dwWeaponTableIndex: Int = 0
	@JvmField
	var m_dwInput: Int = 0
	@JvmField
	var m_dwGlowObject: Int = 0
	@JvmField
	var m_dwForceJump: Int = 0
	@JvmField
	var m_dwForceAttack: Int = 0
	@JvmField
	var m_dwViewMatrix: Int = 0
	@JvmField
	var m_dwEntityList: Int = 0
	@JvmField
	var m_dwLocalPlayer: Int = 0
	@JvmField
	var m_nFallbackPaintKit: Int = 0
	@JvmField
	var m_nFallbackSeed: Int = 0
	@JvmField
	var m_nFallbackStatTrak: Int = 0
	@JvmField
	var m_iEntityQuality: Int = 0
	@JvmField
	var m_flFallbackWear: Int = 0
	@JvmField
	var m_iItemDefinitionIndex: Int = 0
	@JvmField
	var m_OriginalOwnerXuidLow: Int = 0
	@JvmField
	var m_iItemIDHigh: Int = 0
	@JvmField
	var m_iAccountID = byName("DT_WeaponCSBase", "m_iAccountID")
	@JvmField
	var iViewModelIndex: Int = 0
	@JvmField
	var iWorldModelIndex: Int = 0
	@JvmField
	var m_iWorldDroppedModelIndex: Int = 0
	@JvmField
	var m_hViewModel: Int = 0
	@JvmField
	var m_nModelIndex: Int = 0
	@JvmField
	var m_bDormant: Int = 0
	
	
	@JvmStatic
	fun load() {
		/*
		  Client.dll offsets
		 */
		m_dwRadarBase = byPattern(clientModule(), 0x1, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0xA1, 0x00, 0x00, 0x00, 0x00, 0x8B, 0x0C, 0xB0, 0x8B, 0x01, 0xFF, 0x50, 0x00, 0x46, 0x3B, 0x35, 0x00, 0x00, 0x00, 0x00, 0x7C, 0xEA, 0x8B, 0x0D, 0x00, 0x00, 0x00, 0x00)
		m_dwWeaponTable = byPattern(clientModule(), 0x1, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0x39, 0x86, 0x00, 0x00, 0x00, 0x00, 0x74, 0x06, 0x89, 0x86, 0x0, 0x0, 0x0, 0x0, 0x8B, 0x86)
		m_dwWeaponTableIndex = byPattern(clientModule(), 0x2, 0x0, PatternScanner.READ, 0x39, 0x86, 0x00, 0x00, 0x00, 0x00, 0x74, 0x06, 0x89, 0x86, 0x00, 0x00, 0x00, 0x00, 0x8B, 0x86)
		m_dwInput = byPattern(clientModule(), 0x1, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0xB9, 0x00, 0x00, 0x00, 0x00, 0xF3, 0x0F, 0x11, 0x04, 0x24, 0xFF, 0x50, 0x10)
		m_dwGlowObject = byPattern(clientModule(), 0x1, 0x4, PatternScanner.READ or PatternScanner.SUBTRACT, 0xA1, 0x00, 0x00, 0x00, 0x00, 0xa8, 0x01, 0x75, 0x00, 0x0f, 0x57, 0xc0, 0xc7, 0x05)
		m_dwForceJump = byPattern(clientModule(), 0x2, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0x89, 0x0D, 0x00, 0x00, 0x00, 0x00, 0x8B, 0x0D, 0x00, 0x00, 0x00, 0x00, 0x8B, 0xF2, 0x8B, 0xC1, 0x83, 0xCE, 0x08)
		m_dwForceAttack = byPattern(clientModule(), 0x2, 0xC, PatternScanner.READ or PatternScanner.SUBTRACT, 0x89, 0x0D, 0x00, 0x00, 0x00, 0x00, 0x8B, 0x0D, 0x00, 0x00, 0x00, 0x00, 0x8B, 0xF2, 0x8B, 0xC1, 0x83, 0xCE, 0x04)
		m_dwViewMatrix = byPattern(clientModule(), 0x3, 0xb0, PatternScanner.READ or PatternScanner.SUBTRACT, 0x0F, 0x10, 0x05, 0x00, 0x00, 0x00, 0x00, 0x8D, 0x85, 0x00, 0x00, 0x00, 0x00, 0xB9)
		m_dwEntityList = byPattern(clientModule(), 0x1, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0xBB, 0x00, 0x00, 0x00, 0x00, 0x83, 0xFF, 0x01, 0x0F, 0x8C, 0x00, 0x00, 0x00, 0x00, 0x3B, 0xF8)
		m_dwLocalPlayer = byPattern(clientModule(), 0x1, 0x10, PatternScanner.READ or PatternScanner.SUBTRACT, 0xA3, 0x00, 0x00, 0x00, 0x00, 0xC7, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xE8, 0x00, 0x00, 0x00, 0x00, 0x59, 0xC3, 0x6A, 0x00)
		m_bDormant = byPattern(clientModule(), 0x2, 0x0, PatternScanner.READ, 0x88, 0x9E, 0x0, 0x0, 0x0, 0x0, 0xE8, 0x0, 0x0, 0x0, 0x0, 0x53, 0x8D, 0x8E, 0x0, 0x0, 0x0, 0x0, 0xE8, 0x0, 0x0, 0x0, 0x0, 0x8B, 0x06, 0x8B, 0xCE, 0x53, 0xFF, 0x90, 0x0, 0x0, 0x0, 0x0, 0x8B, 0x46, 0x64, 0x0F, 0xB6, 0xCB, 0x5E, 0x5B, 0x66, 0x89, 0x0C, 0xC5, 0x0, 0x0, 0x0, 0x0, 0x5D, 0xC2, 0x04, 0x00)
		
		m_iAccountID = byName("DT_WeaponCSBase", "m_iAccountID")
		m_nFallbackPaintKit = byName("DT_WeaponCSBase", "m_nFallbackPaintKit")
		m_nFallbackSeed = byName("DT_WeaponCSBase", "m_nFallbackSeed")
		m_nFallbackStatTrak = byName("DT_WeaponCSBase", "m_nFallbackStatTrak")
		m_iEntityQuality = byName("DT_WeaponCSBase", "m_iEntityQuality")
		m_flFallbackWear = byName("DT_WeaponCSBase", "m_flFallbackWear")
		m_iItemDefinitionIndex = byName("DT_WeaponCSBase", "m_iItemDefinitionIndex")
		m_OriginalOwnerXuidLow = byName("DT_WeaponCSBase", "m_OriginalOwnerXuidLow")
		m_iItemIDHigh = byName("DT_WeaponCSBase", "m_iItemIDHigh")
		m_iAccountID = byName("DT_WeaponCSBase", "m_iAccountID")
		iViewModelIndex = byName("DT_WeaponCSBase", "m_iViewModelIndex")
		iWorldModelIndex = byName("DT_WeaponCSBase", "m_iWorldModelIndex")
		m_iWorldDroppedModelIndex = byName("DT_WeaponCSBase", "m_iWorldDroppedModelIndex")
		m_hViewModel = byName("DT_CSPlayer", "m_hViewModel[0]")
		m_nModelIndex = byName("DT_BaseViewModel", "m_nModelIndex")
	}
	
	@JvmStatic
	fun dump() {
		val text = ClientOffsets::class.java.fields.map { it.name + " -> " + Strings.hex(getValue(it)) }
		try {
			Files.write(Paths.get("ClientOffsets.txt"), text)
		} catch (e: IOException) {
			e.printStackTrace()
		}
		
	}
	
	private fun getValue(field: Field): Int {
		try {
			return field.get(ClientOffsets::class.java) as Int
		} catch (t: Throwable) {
			t.printStackTrace()
		}
		
		return -1
	}
	
}
