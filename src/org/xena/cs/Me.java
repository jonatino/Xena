package org.xena.cs;

import lombok.Getter;

import static org.abendigo.OffsetManager.clientModule;
import static org.abendigo.OffsetManager.process;
import static org.abendigo.offsets.Offsets.*;

public class Me extends Player {

    @Getter
    private int crosshair;

    @Getter
    private int target;

    @Getter
    private final float[] punch = new float[3];

    @Override
    public void update() {
        int myAddress = clientModule().readInt(m_dwLocalPlayer);
        if (myAddress > 0) {
            setAddress(myAddress);
            super.update();
            index = process().readInt(myAddress + m_dwIndex) - 1;
            crosshair = process().readInt(myAddress + m_iCrossHairID) - 1;
            target = clientModule().readInt(m_dwEntityList + (crosshair * 16));

            int weaponBase = process().readInt(myAddress + m_hActiveWeapon);
            if (weaponBase > 0) {
                int entNum = weaponBase & 0xFFF;
                int weaponID = clientModule().readInt(m_dwEntityList + (entNum - 1) * 16);
                if (weaponID > 0 && weaponID != activeWeapon.getWeaponID()) {
                    activeWeapon.setWeaponID(process().readInt(weaponID + m_iWeaponID));
                    activeWeapon.setCanReload(process().readBoolean(weaponID + m_bCanReload));
                    activeWeapon.setClip1(process().readInt(weaponID + m_iClip1));
                    activeWeapon.setClip2(process().readInt(weaponID + m_iClip2));
                }
            }
        }
    }

}