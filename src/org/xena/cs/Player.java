package org.xena.cs;

import lombok.Getter;

import static org.abendigo.OffsetManager.*;
import static org.abendigo.offsets.Offsets.*;

public class Player extends GameEntity {

    @Getter
    protected int lifeState;

    @Getter
    protected int health;

    @Getter
    protected int[] weaponIds = new int[48];

    @Getter
    protected Weapon activeWeapon = Weapon.DEFAULT;

    @Getter
    protected final float[] velocity = new float[3];

    @Getter
    protected final float[] viewOffsets = new float[3];

    @Getter
    protected final float[] viewAngles = new float[3];

    @Getter
    protected final float[] bones = new float[3];

    @Getter
    protected final float[] punch = new float[2];

    @Getter
    protected int glowIndex;

    @Getter
    protected int armor;

    @Getter
    protected boolean gunGameImmunity;

    @Getter
    protected int shotsFired;

    @Getter
    protected boolean hasBomb;

    @Override
    public void update() {
        super.update();
        shotsFired = process().readInt(address() + m_iShotsFired);
        velocity[0] = process().readFloat(address() + m_vecVelocity);
        velocity[1] = process().readFloat(address() + m_vecVelocity + 4);
        velocity[2] = process().readFloat(address() + m_vecVelocity + 8);

        viewOffsets[0] = process().readFloat(address() + m_vecViewOffset);
        viewOffsets[1] = process().readFloat(address() + m_vecViewOffset + 4);
        viewOffsets[2] = process().readFloat(address() + m_vecViewOffset + 8);

        int anglePointer = engineModule().readInt(m_dwClientState);
        viewAngles[0] = process().readFloat(anglePointer + m_dwViewAngles);
        viewAngles[1] = process().readFloat(anglePointer + m_dwViewAngles + 4);
        viewAngles[2] = process().readFloat(anglePointer + m_dwViewAngles + 8);

        int boneMatrix = process().readInt(address() + m_dwBoneMatrix);
        bones[0] = process().readFloat(boneMatrix + 0x30 * 6 + 0x0C);
        bones[1] = process().readFloat(boneMatrix + 0x30 * 6 + 0x1C);
        bones[2] = process().readFloat(boneMatrix + 0x30 * 6 + 0x2C);

        punch[0] = process().readFloat(address() + m_vecPunch);
        punch[1] = process().readFloat(address() + m_vecPunch + 4);

        hasBomb = false;

        for (int i = 0; i < weaponIds.length; i++) {
            int weaponBase = process().readInt(address() + m_hMyWeapons + ((i - 1) * 0x04));

            int entNum = weaponBase & 0xFFF;
            int id = clientModule().readInt(m_dwEntityList + (entNum - 1) * 0x10);

            weaponIds[i] = id;
            if (id > 0) {
                int weaponId = process().readInt(id + m_iWeaponID);
                if (weaponId != activeWeapon.getWeaponID()) {
                    activeWeapon.setWeaponID(weaponId);
                    activeWeapon.setCanReload(process().readBoolean(id + m_bCanReload));
                    activeWeapon.setClip1(process().readInt(id + m_iClip1));
                    activeWeapon.setClip2(process().readInt(id + m_iClip2));
                }
                if (weaponId == 50) {
                    hasBomb = true;
                }
            }
        }
    }
}
