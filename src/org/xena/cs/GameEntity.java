package org.xena.cs;

import lombok.Getter;
import lombok.Setter;

import static org.abendigo.OffsetManager.process;
import static org.abendigo.offsets.Offsets.*;

public class GameEntity extends GameObject {

    @Getter
    @Setter
    protected int classId;

    @Getter
    protected int model;

    @Getter
    protected int index;

    @Getter
    protected int boneMatrix;

    @Getter
    protected int team;

    @Getter
    protected boolean running;

    @Getter
    protected boolean dormant;

    @Getter
    private final float[] position = new float[3];

    @Getter
    protected boolean dead;

    @Getter
    protected boolean spotted;

    public void update() {
        model = process().readInt(address() + m_dwModel);
        boneMatrix = process().readInt(address() + m_dwBoneMatrix);
        team = process().readInt(address() + m_iTeamNum);
        running = process().readBoolean(address() + m_bMoveType);
        dormant = process().readBoolean(address() + m_bDormant);

        position[0] = process().readFloat(address() + m_vecOrigin);
        position[1] = process().readFloat(address() + m_vecOrigin + 4);
        position[2] = process().readFloat(address() + m_vecOrigin + 8);

        dead = process().readBoolean(address() + m_lifeState);
        spotted = process().readInt(address() + m_bSpotted) > 0;
    }

    public Player asPlayer() {
        return (Player) this;
    }

    public boolean isPlayer() {
        return classId == 35;
    }

}
