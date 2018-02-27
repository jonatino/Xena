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
import org.xena.offsets.OffsetManager.engineModule
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
object EngineOffsets {
	
	/**
	 * Engine.dll offsets
	 */
	@JvmField
	var dwClientState_State: Int = 0
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
	@JvmField
	var m_dwGlobalVars: Int = 0
	
	@JvmStatic
	fun load() {
		/*
		  Engine.dll offsets
		 */
		m_dwGlobalVars = byPattern(engineModule(), 0x1, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0x68, 0x0, 0x0, 0x0, 0x0, 0x68, 0x0, 0x0, 0x0, 0x0, 0xFF, 0x50, 0x08, 0x85, 0xC0)
		dwClientState_State = byPattern(engineModule(), 0x1, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0xA1, 0x00, 0x00, 0x00, 0x00, 0x33, 0xD2, 0x6A, 0x0, 0x6A, 0x0, 0x33, 0xC9, 0x89, 0xB0)
		m_dwInGame = byPattern(engineModule(), 0x2, 0x0, PatternScanner.READ, 0x83, 0xB8, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0F, 0x94, 0xC0, 0xC3)
		m_dwMaxPlayer = byPattern(engineModule(), 0x7, 0x0, PatternScanner.READ, 0xA1, 0x00, 0x00, 0x00, 0x00, 0x8B, 0x80, 0x00, 0x00, 0x00, 0x00, 0xC3, 0xCC, 0xCC, 0xCC, 0xCC, 0x55, 0x8B, 0xEC, 0x8A, 0x45, 0x08)
		m_dwMapDirectory = byPattern(engineModule(), 0x1, 0x0, PatternScanner.READ, 0x05, 0x00, 0x00, 0x00, 0x00, 0xC3, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0x80, 0x3D)
		m_dwMapname = byPattern(engineModule(), 0x1, 0x0, PatternScanner.READ, 0x05, 0x00, 0x00, 0x00, 0x00, 0xC3, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0xA1, 0x00, 0x00, 0x00, 0x00)
		m_dwPlayerInfo = byPattern(engineModule(), 0x2, 0x0, PatternScanner.READ, 0x8B, 0x89, 0x00, 0x00, 0x00, 0x00, 0x85, 0xC9, 0x0F, 0x84, 0x00, 0x00, 0x00, 0x00, 0x8B, 0x01)
		m_dwViewAngles = byPattern(engineModule(), 0x4, 0x0, PatternScanner.READ, 0xF3, 0x0F, 0x11, 0x80, 0x00, 0x00, 0x00, 0x00, 0xD9, 0x46, 0x04, 0xD9, 0x05, 0x00, 0x00, 0x00, 0x00)
		m_dwEnginePosition = byPattern(engineModule(), 0x4, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0xF3, 0x0F, 0x11, 0x15, 0x00, 0x00, 0x00, 0x00, 0xF3, 0x0F, 0x11, 0x0D, 0x00, 0x00, 0x00, 0x00, 0xF3, 0x0F, 0x11, 0x05, 0x00, 0x00, 0x00, 0x00, 0xF3, 0x0F, 0x11, 0x3D, 0x00, 0x00, 0x00, 0x00)
		m_dwLocalPlayerIndex = byPattern(engineModule(), 0x2, 0x0, PatternScanner.READ, 0x8B, 0x80, 0x00, 0x00, 0x00, 0x00, 0x40, 0xC3)
		m_bSendPacket = byPattern(engineModule(), 0, 0, PatternScanner.SUBTRACT, 0x01, 0x8B, 0x01, 0x8B, 0x40, 0x10)
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
		
		m_dwModel = 0x6C
		m_dwIndex = 0x64
		m_bMoveType = 0x258
	}
	
	@JvmStatic
	fun dump() {
		val text = EngineOffsets::class.java.fields.map { it.name + " -> " + Strings.hex(getValue(it)) }
		try {
			Files.write(Paths.get("EngineOffsets.txt"), text)
		} catch (e: IOException) {
			e.printStackTrace()
		}
		
	}
	
	private fun getValue(field: Field): Int {
		try {
			return field.get(EngineOffsets::class.java) as Int
		} catch (t: Throwable) {
			t.printStackTrace()
		}
		
		return -1
	}
	
}
