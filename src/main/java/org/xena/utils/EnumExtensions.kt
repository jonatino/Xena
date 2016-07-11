/*
 * Charlatano: Free and open-source (FOSS) cheat for CS:GO/CS:CO
 * Copyright (C) 2017 - Thomas G. P. Nappo, Jonathan Beaudoin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.xena.utils

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap

open class EnumLookUpWithDefault<T>(map: Map<Int, T>,
                                    private val defaultValue: T) {
	//to get rid of type casting
	private val valueMap: Int2ObjectMap<T> = Int2ObjectArrayMap(map.size)
	
	init {
		map.forEach { k, v -> valueMap.put(k, v) }
	}
	
	operator fun get(id: Int) = valueMap[id] ?: defaultValue
}

open class EnumLookUp<T>(map: Map<Int, T>) {
	//to get rid of type casting
	private val valueMap: Int2ObjectMap<T> = Int2ObjectArrayMap(map.size)
	
	init {
		map.forEach { k, v -> valueMap.put(k, v) }
	}
	
	operator fun get(id: Int): T? = valueMap[id]
	fun getOrDefault(id: Int, default: T): T = valueMap.getOrDefault(id, default)
	operator fun get(id: Int, default: T) = getOrDefault(id, default)
}