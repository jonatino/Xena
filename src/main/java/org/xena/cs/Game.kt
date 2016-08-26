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

import org.xena.Indexer
import java.util.*

@JvmField val me = Me()
private val entityMap = HashMap<Long, Int>(256)
@JvmField val entities = Indexer<GameEntity>(128)
@JvmField val clientState = ClientState()

operator fun Indexer<GameEntity>.get(address: Long): GameEntity? {
	val index = (entityMap as Map<Long, Int>).getOrDefault(address, -1)
	if (index == -1) {
		return null
	}
	return entities.get(index)
}

fun register(entity: GameEntity) {
	val index = entities.add(entity)
	entityMap.put(entity.address(), index)
}

fun removePlayers() {
	entityMap.clear()
	entities.clear()
}
