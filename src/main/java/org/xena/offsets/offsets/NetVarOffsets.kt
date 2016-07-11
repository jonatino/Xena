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
import org.xena.offsets.netvars.NetVars.byName
import java.io.IOException
import java.lang.reflect.Field
import java.nio.file.Files
import java.nio.file.Paths


/**
 * Created by Jonathan on 2/27/2018.
 */
object NetVarOffsets {
	
	/**
	 * Netvare offsets
	 */
	@JvmField var nFallbackPaintKit: Int = 0
	@JvmField var nFallbackSeed: Int = 0
	@JvmField var nFallbackStatTrak: Int = 0
	@JvmField var iEntityQuality: Int = 0
	@JvmField var flFallbackWear: Int = 0
	@JvmField var iItemDefinitionIndex: Int = 0
	@JvmField var OriginalOwnerXuidLow: Int = 0
	@JvmField var iItemIDHigh: Int = 0
	@JvmField var iAccountID: Int = 0
	@JvmField var iViewModelIndex: Int = 0
	@JvmField var iWorldModelIndex: Int = 0
	@JvmField var iWorldDroppedModelIndex: Int = 0
	@JvmField var hViewModel: Int = 0
	@JvmField var nModelIndex: Int = 0
	@JvmField var fFlags: Int = 0
	@JvmField var iHealth: Int = 0
	@JvmField var nTickBase: Int = 0
	@JvmField var bIsScoped: Int = 0
	@JvmField var flNextPrimaryAttack: Int = 0
	@JvmField var iWeaponID: Int = 0
	@JvmField var vecViewOffset: Int = 0
	@JvmField var hActiveWeapon: Int = 0
	@JvmField var vecVelocity: Int = 0
	@JvmField var lifeState: Int = 0
	@JvmField var vecPunch: Int = 0
	@JvmField var flFlashMaxAlpha: Int = 0
	@JvmField var iShotsFired: Int = 0
	@JvmField var hMyWeapons: Int = 0
	@JvmField var iClip1: Int = 0
	@JvmField var iClip2: Int = 0
	@JvmField var bSpotted: Int = 0
	@JvmField var bSpottedByMask: Int = 0
	@JvmField var vecOrigin: Int = 0
	@JvmField var iTeamNum: Int = 0
	@JvmField var dwBoneMatrix: Int = 0
	@JvmField var iCrossHairID: Int = 0
	@JvmField var nSurvivalTeam: Int = 0
	@JvmField var dwModel: Int = 0
	@JvmField var dwIndex: Int = 0
	@JvmField var bMoveType: Int = 0
	@JvmField var flSurvivalStartTime: Int = 0
	@JvmField var m_SurvivalGameRuleDecisionTypes: Int = 0
	
	@JvmStatic
	fun load() {
		fFlags = byName("DT_BasePlayer", "m_fFlags")
		iHealth = byName("DT_BasePlayer", "m_iHealth")
		vecViewOffset = byName("DT_BasePlayer", "m_vecViewOffset[0]")
		hActiveWeapon = byName("DT_BasePlayer", "m_hActiveWeapon")
		nTickBase = byName("DT_BasePlayer", "m_nTickBase")
		vecVelocity = byName("DT_BasePlayer", "m_vecVelocity[0]")
		lifeState = byName("DT_BasePlayer", "m_lifeState")
		vecPunch = byName("DT_BasePlayer", "m_aimPunchAngle")
		
		flFlashMaxAlpha = byName("DT_CSPlayer", "m_flFlashMaxAlpha")
		iShotsFired = byName("DT_CSPlayer", "m_iShotsFired")
		bIsScoped = byName("DT_CSPlayer", "m_bIsScoped")
		hMyWeapons = byName("DT_CSPlayer", "m_hMyWeapons")
		hViewModel = byName("DT_CSPlayer", "m_hViewModel[0]")
		iCrossHairID = byName("DT_CSPlayer", "m_bHasDefuser") + 0x5C
		nSurvivalTeam = byName("DT_CSPlayer", "m_nSurvivalTeam")
		
		flNextPrimaryAttack = byName("DT_BaseCombatWeapon", "m_flNextPrimaryAttack")
		iClip1 = byName("DT_BaseCombatWeapon", "m_iClip1")
		iClip2 = byName("DT_BaseCombatWeapon", "m_iClip2")
		
		bSpotted = byName("DT_BaseEntity", "m_bSpotted")
		bSpottedByMask = byName("DT_BaseEntity", "m_bSpottedByMask")
		vecOrigin = byName("DT_BaseEntity", "m_vecOrigin")
		iTeamNum = byName("DT_BaseEntity", "m_iTeamNum")
		
		iWeaponID = byName("DT_WeaponCSBase", "m_fAccuracyPenalty") + 0x2C
		iAccountID = byName("DT_WeaponCSBase", "m_iAccountID")
		nFallbackPaintKit = byName("DT_WeaponCSBase", "m_nFallbackPaintKit")
		nFallbackSeed = byName("DT_WeaponCSBase", "m_nFallbackSeed")
		nFallbackStatTrak = byName("DT_WeaponCSBase", "m_nFallbackStatTrak")
		iEntityQuality = byName("DT_WeaponCSBase", "m_iEntityQuality")
		flFallbackWear = byName("DT_WeaponCSBase", "m_flFallbackWear")
		iItemDefinitionIndex = byName("DT_WeaponCSBase", "m_iItemDefinitionIndex")
		OriginalOwnerXuidLow = byName("DT_WeaponCSBase", "m_OriginalOwnerXuidLow")
		iItemIDHigh = byName("DT_WeaponCSBase", "m_iItemIDHigh")
		iViewModelIndex = byName("DT_WeaponCSBase", "m_iViewModelIndex")
		iWorldModelIndex = byName("DT_WeaponCSBase", "m_iWorldModelIndex")
		iWorldDroppedModelIndex = byName("DT_WeaponCSBase", "m_iWorldDroppedModelIndex")
		
		dwBoneMatrix = byName("DT_BaseAnimating", "m_nForceBone") + 0x1c
		
		nModelIndex = byName("DT_BaseViewModel", "m_nModelIndex")
		
		flSurvivalStartTime = byName("DT_CSGameRulesProxy", "m_flSurvivalStartTime")
		m_SurvivalGameRuleDecisionTypes = byName("DT_CSGameRulesProxy", "m_SurvivalGameRuleDecisionTypes")
		
		
		dwModel = 0x6C
		dwIndex = 0x64
		bMoveType = 0x258
	}
	
	@JvmStatic
	fun dump() {
		val text = NetVarOffsets::class.java.fields.map { it.name + " -> " + Strings.hex(getValue(it)) }
		try {
			Files.write(Paths.get("NetVarOffsets.txt"), text)
		} catch (e: IOException) {
			e.printStackTrace()
		}
		
	}
	
	private fun getValue(field: Field): Int {
		try {
			return field.get(NetVarOffsets::class.java) as? Int ?: -1
		} catch (t: Throwable) {
			t.printStackTrace()
		}
		
		return -1
	}
	
}
