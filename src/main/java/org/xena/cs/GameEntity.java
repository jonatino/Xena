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
    @Setter
    protected long glowObjectPointer;

    @Getter
    protected long index;

    @Getter
    protected long model;

    @Getter
    protected long boneMatrix;

    @Getter
    protected long team;

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
        model = process().readUnsignedInt(address() + m_dwModel);
        boneMatrix = process().readUnsignedInt(address() + m_dwBoneMatrix);
        team = process().readUnsignedInt(address() + m_iTeamNum);
        running = process().readBoolean(address() + m_bMoveType);
        dormant = process().readBoolean(address() + m_bDormant);

        position[0] = process().readFloat(address() + m_vecOrigin);
        position[1] = process().readFloat(address() + m_vecOrigin + 4);
        position[2] = process().readFloat(address() + m_vecOrigin + 8);

        dead = process().readBoolean(address() + m_lifeState);
        spotted = process().readUnsignedInt(address() + m_bSpotted) > 0;


    }

    public Player asPlayer() {
        return (Player) this;
    }

    public boolean isPlayer() {
        return this instanceof Player;
    }

    public EntityType type() {
        return EntityType.byId(classId);
    }

}
