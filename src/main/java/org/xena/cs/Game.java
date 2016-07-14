package org.xena.cs;

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
    private final Map<Long, GameEntity> entities = new HashMap<>(256);
    private final ClientState clientState = new ClientState();

    public Me me() {
        return me;
    }

    public ClientState clientState() {
        return clientState;
    }

    public Map<Long, GameEntity> entities() {
        return entities;
    }

}
