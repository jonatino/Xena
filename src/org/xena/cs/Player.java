package org.xena.cs;

import lombok.Getter;

import static org.abendigo.OffsetManager.process;
import static org.abendigo.offsets.Offsets.*;

public class Player extends GamePlayer {

    @Getter
    protected int glowIndex;

    @Getter
    protected int armor;

    @Getter
    protected boolean gunGameImmunity;

    @Getter
    protected int shotsFired;

    @Override
    public void update() {
        super.update();
        shotsFired = process().readInt(address() + m_iShotsFired);
        velocity[0] = process().readFloat(address() + m_vecVelocity);
        velocity[1] = process().readFloat(address() + m_vecVelocity + 4);
        velocity[2] = process().readFloat(address() + m_vecVelocity + 8);

        viewAngles[0] = process().readFloat(address() + m_vecViewOffset);
        viewAngles[1] = process().readFloat(address() + m_vecViewOffset + 4);
        viewAngles[2] = process().readFloat(address() + m_vecViewOffset + 8);
    }
}
