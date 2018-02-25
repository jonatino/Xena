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

package org.xena.plugin.utils

import com.github.jonatino.offsets.Offsets.m_dwClientState
import com.github.jonatino.offsets.Offsets.m_dwViewAngles
import org.xena.cs.GameEntity
import org.xena.cs.Me
import org.xena.cs.Player
import org.xena.cs.Weapons
import org.xena.engineModule
import org.xena.plugin.Plugin
import org.xena.process
import java.lang.Float.isNaN
import java.util.*

class AngleUtils(private val plugin: Plugin, private val smoothing: Float, private val lowestPitch: Float, private val highestPitch: Float, private val lowestYaw: Float, private val highestYaw: Float) {
	
	fun normalizeAngle(vector: Vector): Vector {
		if (vector.x > 89.0f && vector.x <= 180.0f) {
			vector.x = 89.0f
		}
		while (vector.x > 180f) {
			vector.x -= 360f
		}
		if (vector.x < -89.0f) {
			vector.x = -89.0f
		}
		while (vector.y > 180f) {
			vector.y -= 360f
		}
		while (vector.y < -180f) {
			vector.y += 360f
		}
		vector.z = 0.0f
		return vector
	}
	
	private val delta by lazy { Vector() }
	
	fun calculateAngle(player: Player, src: Vector, dst: Vector, angles: Vector) {
		val pitchreduction = randomFloat(lowestPitch, highestPitch)
		val yawreduction = randomFloat(lowestYaw, highestYaw)
		delta.x = src.x - dst.x
		delta.y = src.y - dst.y
		delta.z = src.z + player.viewOffsets.z - dst.z
		
		val hyp = Math.sqrt((delta.x * delta.x + delta.y * delta.y).toDouble())
		angles.x = (Math.atan(delta.z / hyp) * (180 / Math.PI) - player.punch.x * pitchreduction).toFloat()
		angles.y = (Math.atan((delta.y / delta.x).toDouble()) * (180 / Math.PI) - player.punch.y * yawreduction).toFloat()
		angles.z = 0.0f
		if (delta.x >= 0.0) {
			angles.y += 180.0f
		}
	}
	
	fun canShoot(me: Me, target: GameEntity): Boolean {
		val weaponID = me.activeWeapon.weaponID.toInt()
		return weaponID != Weapons.KNIFE_T.id && weaponID != Weapons.KNIFE_CT.id && me.activeWeapon.clip1 > 0 && !target.isDead && !me.isDead && target.team != me.team
	}
	
	private val smoothedAngles = Vector()
	
	fun setAngleSmooth(dest: Vector, orig: Vector) {
		smoothedAngles.x = dest.x - orig.x
		smoothedAngles.y = dest.y - orig.y
		smoothedAngles.z = 0.0f
		normalizeAngle(smoothedAngles)
		
		smoothedAngles.x = orig.x + smoothedAngles.x / 100.0f * smoothing
		smoothedAngles.y = orig.y + smoothedAngles.y / 100.0f * smoothing
		smoothedAngles.z = 0.0f
		normalizeAngle(smoothedAngles)
		
		setAngles(smoothedAngles)
	}
	
	fun setAngles(angles: Vector) {
		if (isNaN(angles.x) || isNaN(angles.y) || isNaN(angles.z)) {
			return
		}
		val anglePointer = engineModule.readUnsignedInt(m_dwClientState.toLong())
		process.writeFloat(anglePointer + m_dwViewAngles, angles.x)
		process.writeFloat(anglePointer + m_dwViewAngles.toLong() + 4, angles.y)
	}
	
	fun velocityComp(me: Me, target: Player, enemyPos: Vector) {
		enemyPos.x += target.velocity.x / 100f * (40f / smoothing)
		enemyPos.y += target.velocity.y / 100f * (40f / smoothing)
		enemyPos.z += target.velocity.z / 100f * (40f / smoothing)
		
		enemyPos.x -= me.velocity.x / 100f * (40f / smoothing)
		enemyPos.y -= me.velocity.y / 100f * (40f / smoothing)
		enemyPos.z -= me.velocity.z / 100f * (40f / smoothing)
	}
	
	fun delta(me: Vector, them: Vector): Float {
		var delta = 0f
		delta += Math.abs(me.x + java.lang.Short.MAX_VALUE - (them.x + java.lang.Short.MAX_VALUE))
		delta += Math.abs(me.y + java.lang.Short.MAX_VALUE - (them.y + java.lang.Short.MAX_VALUE))
		return delta
	}
	
	private val random = Random()
	
	private fun randomFloat(a: Float, b: Float): Float {
		return a + random.nextFloat() * (b - a)
	}
	
}
