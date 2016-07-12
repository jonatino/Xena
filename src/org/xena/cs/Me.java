package org.xena.cs;

import lombok.Getter;

import static org.abendigo.OffsetManager.clientModule;
import static org.abendigo.OffsetManager.process;
import static org.abendigo.offsets.Offsets.*;

public class Me extends Player {

    @Getter
    private int crosshair;

    @Getter
    private Player target;

    @Getter
    private int shotsFired;

    @Override
    public void update() {
        int myAddress = clientModule().readInt(m_dwLocalPlayer);
        if (myAddress > 0) {
            setAddress(myAddress);
            super.update();
            index = process().readInt(myAddress + m_dwIndex) - 1;

            crosshair = process().readInt(myAddress + m_iCrossHairID) - 1;
            GameEntity entity = Game.current().entities().get(clientModule().readInt(m_dwEntityList + (crosshair * 0x10)));
            if (crosshair > 0 && entity != null && entity.isPlayer()) {
                target = entity.asPlayer();
            } else {
                target = null;
            }

            shotsFired = process().readInt(address() + m_iShotsFired);
        }
    }

}