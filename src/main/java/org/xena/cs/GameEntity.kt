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

import org.xena.offsets.OffsetManager.engineModule
import org.xena.offsets.OffsetManager.process
import org.xena.offsets.offsets.ClientOffsets.bDormant
import org.xena.offsets.offsets.EngineOffsets.dwClientState_State
import org.xena.offsets.offsets.EngineOffsets.dwViewAngles
import org.xena.offsets.offsets.NetVarOffsets.bMoveType
import org.xena.offsets.offsets.NetVarOffsets.bSpotted
import org.xena.offsets.offsets.NetVarOffsets.dwBoneMatrix
import org.xena.offsets.offsets.NetVarOffsets.dwModel
import org.xena.offsets.offsets.NetVarOffsets.lifeState
import org.xena.offsets.offsets.NetVarOffsets.vecOrigin
import org.xena.offsets.offsets.NetVarOffsets.vecPunch
import org.xena.offsets.offsets.NetVarOffsets.vecVelocity
import org.xena.offsets.offsets.NetVarOffsets.vecViewOffset
import org.xena.plugin.utils.Vector
import java.lang.Math.abs

open class GameEntity : GameObject() {
	
	var model: Long = 0
		protected set
	
	var boneMatrix: Long = 0
		protected set
	
	var team: Int = 0
	
	var isRunning: Boolean = false
		protected set
	
	var isDormant: Boolean = false
		protected set
	
	val viewOrigin = Vector()
	
	val velocity = Vector()
	
	val viewOffsets = Vector()
	
	val viewAngles = Vector()
	
	val bones = Vector()
	
	val punch = Vector()
	
	var isDead: Boolean = false
		protected set
	
	var isSpotted: Boolean = false
		protected set
	
	var isBombCarrier: Boolean = false
		protected set
	
	open fun update() {
		model = process().readUnsignedInt(address() + dwModel)
		boneMatrix = process().readUnsignedInt(address() + dwBoneMatrix)
		isRunning = process().readBoolean(address() + bMoveType)
		isDormant = process().readBoolean(address() + bDormant)
		
		viewOrigin.x = process().readFloat(address() + vecOrigin)
		viewOrigin.y = process().readFloat(address() + vecOrigin + 4)
		viewOrigin.z = process().readFloat(address() + vecOrigin + 8)
		
		velocity.x = process().readFloat(address() + vecVelocity)
		velocity.y = process().readFloat(address() + vecVelocity + 4)
		velocity.z = process().readFloat(address() + vecVelocity + 8)
		
		viewOffsets.x = process().readFloat(address() + vecViewOffset)
		viewOffsets.y = process().readFloat(address() + vecViewOffset + 4)
		viewOffsets.z = process().readFloat(address() + vecViewOffset + 8)
		
		val anglePointer = engineModule().readUnsignedInt(dwClientState_State.toLong())
		viewAngles.x = process().readFloat(anglePointer + dwViewAngles)
		viewAngles.y = process().readFloat(anglePointer + dwViewAngles + 4)
		viewAngles.z = process().readFloat(anglePointer + dwViewAngles + 8)
		
		val boneMatrix = process().readUnsignedInt(address() + dwBoneMatrix)
		if (boneMatrix > 0) {
			//Bones bone = Bones.roll();
			val bone = Bones.HEAD
			try {
				bones.x = process().readFloat(boneMatrix + (0x30 * bone.id) + 0x0C)
				bones.y = process().readFloat(boneMatrix + (0x30 * bone.id) + 0x1C)
				bones.z = process().readFloat(boneMatrix + (0x30 * bone.id) + 0x2C)
			} catch (e: Exception) {
				e.printStackTrace()
			}
			
		}
		
		punch.x = process().readFloat(address() + vecPunch)
		punch.y = process().readFloat(address() + vecPunch + 4)
		
		isDead = process().readByte(address() + lifeState) != 0
		isSpotted = process().readUnsignedInt(address() + bSpotted).toInt() != 0
	}
	
	fun distanceTo(vector: Vector, target: Vector) = abs(vector.x - target.x) + abs(vector.y - target.y) + abs(vector.z - target.z)
	
	val eyePos by lazy { viewOffsets.plus(viewOrigin) }
	
	val type by lazy { EntityType.byAddress(address()) }
	
}
