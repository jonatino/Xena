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

import org.xena.offsets.offsets.EngineOffsets.m_dwViewAngles
import org.xena.plugin.utils.Vector
import org.xena.process

class ClientState : GameObject() {
	
	var localPlayerIndex: Long = 0
	
	var inGame: Long = 0
	
	var maxPlayer: Long = 0
	
	private val angleVector = Vector()
	
	fun angle(): Vector {
		angleVector.x = process.readFloat(address() + m_dwViewAngles)
		angleVector.y = process.readFloat(address() + m_dwViewAngles + 4)
		angleVector.z = process.readFloat(address() + m_dwViewAngles + 8)
		return angleVector
	}
	
}
