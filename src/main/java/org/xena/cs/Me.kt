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

package org.xena.cs

import org.xena.Settings
import org.xena.offsets.OffsetManager.clientModule
import org.xena.offsets.OffsetManager.process
import org.xena.offsets.offsets.ClientOffsets.dwEntityList
import org.xena.offsets.offsets.ClientOffsets.dwLocalPlayer
import org.xena.offsets.offsets.ClientOffsets.dwMouseEnable
import org.xena.offsets.offsets.ClientOffsets.dwMouseEnablePtr
import org.xena.offsets.offsets.EngineOffsets.m_bCanReload
import org.xena.offsets.offsets.NetVarOffsets.fFlags
import org.xena.offsets.offsets.NetVarOffsets.hActiveWeapon
import org.xena.offsets.offsets.NetVarOffsets.hMyWeapons
import org.xena.offsets.offsets.NetVarOffsets.iClip1
import org.xena.offsets.offsets.NetVarOffsets.iClip2
import org.xena.offsets.offsets.NetVarOffsets.iCrossHairID
import org.xena.offsets.offsets.NetVarOffsets.iShotsFired
import org.xena.plugin.utils.AngleUtils
import org.xena.plugin.utils.Vector

class Me : Player() {
	
	var activeWeapon = Weapon()
		private set
	
	var target: Player? = null
		private set
	
	var shotsFired: Long = 0
		private set
	
	var cursorEnabled: Boolean = true
		private set
	
	var flags: Int = 0
		private set
	
	var onGround: Boolean = false
		private set
	
	override fun update() {
		setAddress(clientModule().readUnsignedInt(dwLocalPlayer.toLong()))
		super.update()
		
		val activeWeaponIndex = process().readUnsignedInt(address() + hActiveWeapon) and 0xFFF
		for (i in 0 until weaponIds.size) {
			val currentWeaponIndex = process().readUnsignedInt(address() + hMyWeapons.toLong() + ((i - 1) * 0x04).toLong()) and 0xFFF
			val weaponAddress = clientModule().readUnsignedInt(dwEntityList + (currentWeaponIndex - 1) * 0x10)
			
			if (weaponAddress > 0 && activeWeaponIndex == currentWeaponIndex) {
				processWeapon(weaponAddress, i, true)
			}
		}
		
		target = null
		val crosshair = process().readUnsignedInt(address() + iCrossHairID) - 1
		if (crosshair > -1 && crosshair <= 1024) {
			val entity = entities[clientModule().readUnsignedInt(dwEntityList + crosshair * 0x10)]
			if (entity != null) {
				target = entity as Player
			}
		}
		
		shotsFired = process().readUnsignedInt(address() + iShotsFired)
		
		val cursorEnablePtr = clientModule().address() + dwMouseEnablePtr
		cursorEnabled = clientModule().readInt(dwMouseEnable.toLong()) xor cursorEnablePtr.toInt() != 1
		
		flags = process().readInt(address() + fFlags)
		
		onGround = flags and 1 == 1
	}
	
	override fun processWeapon(weaponAddress: Long, index: Int, active: Boolean): Int {
		val weaponId = super.processWeapon(weaponAddress, index, active)
		if (active) {
			activeWeapon.setAddress(weaponAddress)
			activeWeapon.weaponID = weaponId.toLong()
			activeWeapon.canReload = process().readBoolean(weaponAddress + m_bCanReload)
			activeWeapon.clip1 = process().readUnsignedInt(weaponAddress + iClip1)
			activeWeapon.clip2 = process().readUnsignedInt(weaponAddress + iClip2)
		}
		return weaponId
	}
	
	private val closestTargetVector by lazy { Vector() }
	
	fun getClosestTarget(aimHelper: AngleUtils, fov: Int = Settings.FORCE_AIM_FOV): Player? {
		if (target != null) {
			return target
		}
		
		var closestDelta = Double.MAX_VALUE
		var closestPlayer: Player? = null
		
		val angle = clientState.angle()
		
		var lastIdx = 0
		while (lastIdx + 1 <= entities.size()) {
			val entity = entities.get(lastIdx++)
			
			try {
				if (/*aimHelper.delta(viewOrigin, entity.bones) > 3000 || */!aimHelper.canShoot(me, entity)) {
					continue
				}
				
				val eyePos = entity.bones
				val distance = distanceTo(viewOrigin, eyePos)
				
				aimHelper.calculateAngle(me, eyePos, closestTargetVector)
				
				val pitchDiff = Math.abs(angle.x - closestTargetVector.x).toDouble()
				val yawDiff = Math.abs(angle.y - closestTargetVector.y).toDouble()
				val delta = Math.abs(Math.sin(Math.toRadians(yawDiff)) * distance)
				val fovDelta = Math.abs((Math.sin(Math.toRadians(pitchDiff)) + Math.sin(Math.toRadians(yawDiff))) * distance)
				
				if (fovDelta <= fov && delta < closestDelta && closestDelta >= 0) {
					closestDelta = delta
					closestPlayer = entity as Player?
				}
			} catch (e: Throwable) {
				e.printStackTrace()
			}
		}
		
		if (closestDelta == Double.MAX_VALUE || closestDelta < 0) {
			return null
		}
		
		return closestPlayer
	}
	
}