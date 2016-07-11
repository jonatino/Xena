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
@file:JvmName("GameKT")

package org.xena.cs

import it.unimi.dsi.fastutil.longs.Long2IntArrayMap
import org.xena.Indexer

@JvmField
val me = Me()
@JvmField
val entityMap = Long2IntArrayMap(256)
@JvmField
val entities = Indexer<GameEntity>(128)
@JvmField
val clientState = ClientState()

operator fun Indexer<GameEntity>.get(address: Long): GameEntity? {
	var index = -1
	if (entityMap.containsKey(address)) index = entityMap.get(address)
	if (index == -1) {
		return null
	}
	return entities.get(index)
}

fun removePlayers() {
	entityMap.clear()
	entities.clear()
}
