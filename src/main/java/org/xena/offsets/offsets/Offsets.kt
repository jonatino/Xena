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
import org.xena.offsets.OffsetManager
import org.xena.offsets.OffsetManager.clientModule
import org.xena.offsets.OffsetManager.engineModule
import org.xena.offsets.misc.PatternScanner
import org.xena.offsets.misc.PatternScanner.getAddressForPattern
import org.xena.offsets.netvars.NetVars.byName
import java.io.IOException
import java.lang.reflect.Field
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


/**
 * Created by Jonathan on 11/13/2015.
 */
object Offsets {
	
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
	var m_dwGlobalVars: Int = 0
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
	/**
	 * Engine.dll offsets
	 */
	@JvmField
	var m_dwClientState: Int = 0
	@JvmField
	var m_dwInGame: Int = 0
	@JvmField
	var m_dwMaxPlayer: Int = 0
	@JvmField
	var m_dwMapDirectory: Int = 0
	@JvmField
	var m_dwMapname: Int = 0
	@JvmField
	var m_dwPlayerInfo: Int = 0
	@JvmField
	var m_dwViewAngles: Int = 0
	@JvmField
	var m_dwEnginePosition: Int = 0
	@JvmField
	var m_flFlashMaxAlpha: Int = 0
	@JvmField
	var m_bCanReload: Int = 0
	@JvmField
	var m_bSendPacket: Int = 0
	//@JvmField val m_dwForceFullUpdate;
	@JvmField
	var m_dwLocalPlayerIndex: Int = 0
	@JvmField
	var m_iTeamNum: Int = 0
	@JvmField
	var m_bDormant: Int = 0
	@JvmField
	var m_bMoveType: Int = 0
	@JvmField
	var m_iCrossHairID: Int = 0
	@JvmField
	var m_iShotsFired: Int = 0
	@JvmField
	var m_dwBoneMatrix: Int = 0
	@JvmField
	var m_vecVelocity: Int = 0
	@JvmField
	var m_vecPunch: Int = 0
	@JvmField
	var m_lifeState: Int = 0
	@JvmField
	var m_dwModel: Int = 0
	@JvmField
	var m_dwIndex: Int = 0
	@JvmField
	var m_vecViewOffset: Int = 0
	@JvmField
	var m_bIsScoped: Int = 0
	@JvmField
	var m_bSpotted: Int = 0
	@JvmField
	var m_hActiveWeapon: Int = 0
	@JvmField
	var m_iWeaponID: Int = 0
	@JvmField
	var m_fFlags: Int = 0
	@JvmField
	var m_iHealth: Int = 0
	@JvmField
	var m_flNextPrimaryAttack: Int = 0
	@JvmField
	var m_nTickBase: Int = 0
	@JvmField
	var m_vecOrigin: Int = 0
	@JvmField
	var m_iClip1: Int = 0
	@JvmField
	var m_iClip2: Int = 0
	@JvmField
	var m_hMyWeapons: Int = 0
	
	@JvmStatic
	fun load() {
		/*
		  Client.dll offsets
		 */
		m_dwRadarBase = getAddressForPattern(clientModule(), 0x1, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0xA1, 0x00, 0x00, 0x00, 0x00, 0x8B, 0x0C, 0xB0, 0x8B, 0x01, 0xFF, 0x50, 0x00, 0x46, 0x3B, 0x35, 0x00, 0x00, 0x00, 0x00, 0x7C, 0xEA, 0x8B, 0x0D, 0x00, 0x00, 0x00, 0x00)
		m_dwWeaponTable = getAddressForPattern(clientModule(), 0x1, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0x39, 0x86, 0x00, 0x00, 0x00, 0x00, 0x74, 0x06, 0x89, 0x86, 0x0, 0x0, 0x0, 0x0, 0x8B, 0x86)
		m_dwWeaponTableIndex = getAddressForPattern(clientModule(), 0x2, 0x0, PatternScanner.READ, 0x39, 0x86, 0x00, 0x00, 0x00, 0x00, 0x74, 0x06, 0x89, 0x86, 0x00, 0x00, 0x00, 0x00, 0x8B, 0x86)
		m_dwInput = getAddressForPattern(clientModule(), 0x1, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0xB9, 0x00, 0x00, 0x00, 0x00, 0xF3, 0x0F, 0x11, 0x04, 0x24, 0xFF, 0x50, 0x10)
		m_dwGlowObject = getAddressForPattern(clientModule(), 0x1, 0x4, PatternScanner.READ or PatternScanner.SUBTRACT, 0xA1, 0x00, 0x00, 0x00, 0x00, 0xa8, 0x01, 0x75, 0x00, 0x0f, 0x57, 0xc0, 0xc7, 0x05)
		m_dwForceJump = getAddressForPattern(clientModule(), 0x2, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0x89, 0x0D, 0x00, 0x00, 0x00, 0x00, 0x8B, 0x0D, 0x00, 0x00, 0x00, 0x00, 0x8B, 0xF2, 0x8B, 0xC1, 0x83, 0xCE, 0x08)
		m_dwForceAttack = getAddressForPattern(clientModule(), 0x2, 0xC, PatternScanner.READ or PatternScanner.SUBTRACT, 0x89, 0x0D, 0x00, 0x00, 0x00, 0x00, 0x8B, 0x0D, 0x00, 0x00, 0x00, 0x00, 0x8B, 0xF2, 0x8B, 0xC1, 0x83, 0xCE, 0x04)
		m_dwViewMatrix = getAddressForPattern(clientModule(), 0x3, 0xb0, PatternScanner.READ or PatternScanner.SUBTRACT, 0x0F, 0x10, 0x05, 0x00, 0x00, 0x00, 0x00, 0x8D, 0x85, 0x00, 0x00, 0x00, 0x00, 0xB9)
		m_dwEntityList = getAddressForPattern(clientModule(), 0x1, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0xBB, 0x00, 0x00, 0x00, 0x00, 0x83, 0xFF, 0x01, 0x0F, 0x8C, 0x00, 0x00, 0x00, 0x00, 0x3B, 0xF8)
		m_dwLocalPlayer = getAddressForPattern(clientModule(), 0x1, 0x10, PatternScanner.READ or PatternScanner.SUBTRACT, 0xA3, 0x00, 0x00, 0x00, 0x00, 0xC7, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xE8, 0x00, 0x00, 0x00, 0x00, 0x59, 0xC3, 0x6A, 0x00)
		m_bDormant = getAddressForPattern(clientModule(), 0x2, 0x0, PatternScanner.READ, 0x88, 0x9E, 0x0, 0x0, 0x0, 0x0, 0xE8, 0x0, 0x0, 0x0, 0x0, 0x53, 0x8D, 0x8E, 0x0, 0x0, 0x0, 0x0, 0xE8, 0x0, 0x0, 0x0, 0x0, 0x8B, 0x06, 0x8B, 0xCE, 0x53, 0xFF, 0x90, 0x0, 0x0, 0x0, 0x0, 0x8B, 0x46, 0x64, 0x0F, 0xB6, 0xCB, 0x5E, 0x5B, 0x66, 0x89, 0x0C, 0xC5, 0x0, 0x0, 0x0, 0x0, 0x5D, 0xC2, 0x04, 0x00)
		
		/*
		  Engine.dll offsets
		 */
		m_dwGlobalVars = getAddressForPattern(engineModule(), 0x1, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0x68, 0x0, 0x0, 0x0, 0x0, 0x68, 0x0, 0x0, 0x0, 0x0, 0xFF, 0x50, 0x08, 0x85, 0xC0)
		m_dwClientState = getAddressForPattern(engineModule(), 0x1, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0xA1, 0x00, 0x00, 0x00, 0x00, 0x33, 0xD2, 0x6A, 0x0, 0x6A, 0x0, 0x33, 0xC9, 0x89, 0xB0)
		m_dwInGame = getAddressForPattern(engineModule(), 0x2, 0x0, PatternScanner.READ, 131, 185, 0x00, 0x00, 0x00, 0x00, 6, 15, 148, 192, 195)
		m_dwMaxPlayer = getAddressForPattern(engineModule(), 0x7, 0x0, PatternScanner.READ, 0xA1, 0x00, 0x00, 0x00, 0x00, 0x8B, 0x80, 0x00, 0x00, 0x00, 0x00, 0xC3, 0xCC, 0xCC, 0xCC, 0xCC, 0x55, 0x8B, 0xEC, 0x8A, 0x45, 0x08)
		m_dwMapDirectory = getAddressForPattern(engineModule(), 0x1, 0x0, PatternScanner.READ, 0x05, 0x00, 0x00, 0x00, 0x00, 0xC3, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0x80, 0x3D)
		m_dwMapname = getAddressForPattern(engineModule(), 0x1, 0x0, PatternScanner.READ, 0x05, 0x00, 0x00, 0x00, 0x00, 0xC3, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0xA1, 0x00, 0x00, 0x00, 0x00)
		m_dwPlayerInfo = getAddressForPattern(engineModule(), 0x2, 0x0, PatternScanner.READ, 0x8B, 0x89, 0x00, 0x00, 0x00, 0x00, 0x85, 0xC9, 0x0F, 0x84, 0x00, 0x00, 0x00, 0x00, 0x8B, 0x01)
		m_dwViewAngles = getAddressForPattern(engineModule(), 0x4, 0x0, PatternScanner.READ, 0xF3, 0x0F, 0x11, 0x80, 0x00, 0x00, 0x00, 0x00, 0xD9, 0x46, 0x04, 0xD9, 0x05, 0x00, 0x00, 0x00, 0x00)
		m_dwEnginePosition = getAddressForPattern(engineModule(), 0x4, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0xF3, 0x0F, 0x11, 0x15, 0x00, 0x00, 0x00, 0x00, 0xF3, 0x0F, 0x11, 0x0D, 0x00, 0x00, 0x00, 0x00, 0xF3, 0x0F, 0x11, 0x05, 0x00, 0x00, 0x00, 0x00, 0xF3, 0x0F, 0x11, 0x3D, 0x00, 0x00, 0x00, 0x00)
		m_dwLocalPlayerIndex = getAddressForPattern(engineModule(), 0x2, 0x0, PatternScanner.READ, 0x8B, 0x80, 0x00, 0x00, 0x00, 0x00, 0x40, 0xC3)
		m_bSendPacket = getAddressForPattern(engineModule(), 0, 0, PatternScanner.SUBTRACT, 0x01, 0x8B, 0x01, 0x8B, 0x40, 0x10)
		//m_dwForceFullUpdate = PatternScanner.getAddressForPattern(OffsetManager.engineModule(), 0x3, 0, PatternScanner.READ | PatternScanner.SUBTRACT, 0xB0, 0xFF, 0xB7, 0x00, 0x00, 0x00, 0x00, 0xE8);
		
		m_fFlags = byName("DT_BasePlayer", "m_fFlags")
		m_iHealth = byName("DT_BasePlayer", "m_iHealth")
		m_vecViewOffset = byName("DT_BasePlayer", "m_vecViewOffset[0]")
		m_hActiveWeapon = byName("DT_BasePlayer", "m_hActiveWeapon")
		m_nTickBase = byName("DT_BasePlayer", "m_nTickBase")
		m_vecVelocity = byName("DT_BasePlayer", "m_vecVelocity[0]")
		m_lifeState = byName("DT_BasePlayer", "m_lifeState")
		
		m_flFlashMaxAlpha = byName("DT_CSPlayer", "m_flFlashMaxAlpha")
		m_iShotsFired = byName("DT_CSPlayer", "m_iShotsFired")
		m_bIsScoped = byName("DT_CSPlayer", "m_bIsScoped")
		
		m_hMyWeapons = byName("DT_CSPlayer", "m_hMyWeapons")
		
		
		m_flNextPrimaryAttack = byName("DT_BaseCombatWeapon", "m_flNextPrimaryAttack")
		m_iClip1 = byName("DT_BaseCombatWeapon", "m_iClip1")
		m_iClip2 = byName("DT_BaseCombatWeapon", "m_iClip2")
		
		m_bSpotted = byName("DT_BaseEntity", "m_bSpotted")
		m_vecOrigin = byName("DT_BaseEntity", "m_vecOrigin")
		m_iTeamNum = byName("DT_BaseEntity", "m_iTeamNum")
		
		m_vecPunch = byName("DT_BasePlayer", "m_aimPunchAngle")
		
		m_iWeaponID = byName("DT_WeaponCSBase", "m_fAccuracyPenalty") + 0x2C
		
		m_dwBoneMatrix = byName("DT_BaseAnimating", "m_nForceBone") + 0x1c
		
		m_iCrossHairID = byName("DT_CSPlayer", "m_bHasDefuser") + 0x5C
		
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
		
		m_dwModel = 0x6C
		m_dwIndex = 0x64
		m_bMoveType = 0x258
	}
	
	@JvmStatic
	fun dump() {
		val text = ArrayList<String>()
		for (field in Offsets::class.java.fields) {
			text.add(field.name + " -> " + Strings.hex(getValue(field)))
		}
		try {
			Files.write(Paths.get("Offsets.txt"), text)
		} catch (e: IOException) {
			e.printStackTrace()
		}
		
	}
	
	private fun getValue(field: Field): Int {
		try {
			return field.get(Offsets::class.java) as Int
		} catch (t: Throwable) {
			t.printStackTrace()
		}
		
		return -1
	}
	
}
