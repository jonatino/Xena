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
	@JvmField var dwClientState_State: Int = 0
	@JvmField var dwInGame: Int = 0
	@JvmField var dwMaxPlayer: Int = 0
	@JvmField var dwMapDirectory: Int = 0
	@JvmField var dwMapname: Int = 0
	@JvmField var dwPlayerInfo: Int = 0
	@JvmField var dwViewAngles: Int = 0
	@JvmField var dwEnginePosition: Int = 0
	@JvmField var m_bCanReload: Int = 0
	@JvmField var bSendPacket: Int = 0
	//@JvmField val dwForceFullUpdate;
	@JvmField var dwLocalPlayerIndex: Int = 0
	@JvmField var dwGlobalVars: Int = 0
	
	@JvmStatic
	fun load() {
		dwGlobalVars = byPattern(engineModule(), 0x1, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0x68, 0x0, 0x0, 0x0, 0x0, 0x68, 0x0, 0x0, 0x0, 0x0, 0xFF, 0x50, 0x08, 0x85, 0xC0)
		dwClientState_State = byPattern(engineModule(), 0x1, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0xA1, 0x00, 0x00, 0x00, 0x00, 0x33, 0xD2, 0x6A, 0x0, 0x6A, 0x0, 0x33, 0xC9, 0x89, 0xB0)
		dwInGame = byPattern(engineModule(), 0x2, 0x0, PatternScanner.READ, 0x83, 0xB8, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0F, 0x94, 0xC0, 0xC3)
		dwMaxPlayer = byPattern(engineModule(), 0x7, 0x0, PatternScanner.READ, 0xA1, 0x00, 0x00, 0x00, 0x00, 0x8B, 0x80, 0x00, 0x00, 0x00, 0x00, 0xC3, 0xCC, 0xCC, 0xCC, 0xCC, 0x55, 0x8B, 0xEC, 0x8A, 0x45, 0x08)
		dwMapDirectory = byPattern(engineModule(), 0x1, 0x0, PatternScanner.READ, 0x05, 0x00, 0x00, 0x00, 0x00, 0xC3, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0x80, 0x3D)
		dwMapname = byPattern(engineModule(), 0x1, 0x0, PatternScanner.READ, 0x05, 0x00, 0x00, 0x00, 0x00, 0xC3, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0xCC, 0xA1, 0x00, 0x00, 0x00, 0x00)
		dwPlayerInfo = byPattern(engineModule(), 0x2, 0x0, PatternScanner.READ, 0x8B, 0x89, 0x00, 0x00, 0x00, 0x00, 0x85, 0xC9, 0x0F, 0x84, 0x00, 0x00, 0x00, 0x00, 0x8B, 0x01)
		dwViewAngles = byPattern(engineModule(), 0x4, 0x0, PatternScanner.READ, 0xF3, 0x0F, 0x11, 0x80, 0x00, 0x00, 0x00, 0x00, 0xD9, 0x46, 0x04, 0xD9, 0x05, 0x00, 0x00, 0x00, 0x00)
		dwEnginePosition = byPattern(engineModule(), 0x4, 0x0, PatternScanner.READ or PatternScanner.SUBTRACT, 0xF3, 0x0F, 0x11, 0x15, 0x00, 0x00, 0x00, 0x00, 0xF3, 0x0F, 0x11, 0x0D, 0x00, 0x00, 0x00, 0x00, 0xF3, 0x0F, 0x11, 0x05, 0x00, 0x00, 0x00, 0x00, 0xF3, 0x0F, 0x11, 0x3D, 0x00, 0x00, 0x00, 0x00)
		dwLocalPlayerIndex = byPattern(engineModule(), 0x2, 0x0, PatternScanner.READ, 0x8B, 0x80, 0x00, 0x00, 0x00, 0x00, 0x40, 0xC3)
		bSendPacket = byPattern(engineModule(), 0, 0, PatternScanner.SUBTRACT, 0x01, 0x8B, 0x01, 0x8B, 0x40, 0x10)
		//dwForceFullUpdate = PatternScanner.getAddressForPattern(OffsetManager.engineModule(), 0x3, 0, PatternScanner.READ | PatternScanner.SUBTRACT, 0xB0, 0xFF, 0xB7, 0x00, 0x00, 0x00, 0x00, 0xE8);
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
			return field.get(EngineOffsets::class.java) as? Int ?: -1
		} catch (t: Throwable) {
			t.printStackTrace()
		}
		
		return -1
	}
	
}
