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

}
