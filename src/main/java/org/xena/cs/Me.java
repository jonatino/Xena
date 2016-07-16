package org.xena.cs;

import lombok.Getter;

import static org.abendigo.OffsetManager.clientModule;
import static org.abendigo.OffsetManager.process;
import static org.abendigo.offsets.Offsets.*;

public class Me extends Player {

    @Getter
    private long crosshair;

    @Getter
    private Player target;

    @Getter
    private long shotsFired;

    @Override
    public void update() {
        super.update();

        crosshair = process().readUnsignedInt(address() + m_iCrossHairID) - 1;
        GameEntity entity = Game.current().get(clientModule().readUnsignedInt(m_dwEntityList + (crosshair * 0x10)));
        if (crosshair > 0 && entity != null && entity.isPlayer()) {
            target = entity.asPlayer();
        } else {
            target = null;
        }

        long weaponBase = process().readUnsignedInt(address() + m_hActiveWeapon);
        if (weaponBase > 0) {
            long entNum = weaponBase & 0xFFF;
            long weaponPointer = clientModule().readUnsignedInt(m_dwEntityList + (entNum - 1) * 16);
            if (weaponPointer > 0) {
                activeWeapon.setWeaponID(process().readUnsignedInt(weaponPointer + m_iWeaponID));
                activeWeapon.setCanReload(process().readBoolean(weaponPointer + m_bCanReload));
                activeWeapon.setClip1(process().readUnsignedInt(weaponPointer + m_iClip1));
                activeWeapon.setClip2(process().readUnsignedInt(weaponPointer + m_iClip2));
            }
        }

        shotsFired = process().readUnsignedInt(address() + m_iShotsFired);
    }

}