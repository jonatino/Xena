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

    private GameEntity[] entities;

    private final Me me = new Me();
    private final Map<Integer, Player> players = new HashMap<>(256);
    private final ClientState clientState = new ClientState();

    public GameEntity[] entities() {
        return entities;
    }

    public void setEntities(GameEntity[] entities) {
        this.entities = entities;
    }

    public Me me() {
        return me;
    }

    public ClientState clientState() {
        return clientState;
    }

    public Map<Integer, Player> players() {
        return players;
    }

}
