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

package org.xena.cs;

import org.xena.Indexer;

import java.util.HashMap;
import java.util.Map;

public final class Game {

    private static volatile Game instance;

    public static Game current() {
        synchronized (Game.class) {
            return instance == null ? (instance = new Game()) : instance;
        }
    }

    private final Me me = new Me();
    private final Map<Long, Integer> entityMap = new HashMap<>(256);
    private final Indexer<GameEntity> entities = new Indexer<>(128);
    private final ClientState clientState = new ClientState();

    public Me me() {
        return me;
    }

    public ClientState clientState() {
        return clientState;
    }

    public void register(GameEntity entity) {
        int index = entities.add(entity);
        entityMap.put(entity.address(), index);
    }

    public GameEntity get(long address) {
        int index = entityMap.getOrDefault(address, -1);
        if (index == -1) {
            return null;
        }
        return entities.get(index);
    }

    public Indexer<GameEntity> entities() {
        return entities;
    }

	public void removePlayers() {
		entityMap.clear();
		entities().clear();
	}

}
