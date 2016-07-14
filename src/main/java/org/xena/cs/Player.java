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
    protected long[] weaponIds = new long[8];

    @Getter
    protected Weapon activeWeapon = new Weapon();

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
    protected boolean hasBomb;

    @Override
    public void update() {
        super.update();
        velocity[0] = process().readFloat(address() + m_vecVelocity);
        velocity[1] = process().readFloat(address() + m_vecVelocity + 4);
        velocity[2] = process().readFloat(address() + m_vecVelocity + 8);

        viewOffsets[0] = process().readFloat(address() + m_vecViewOffset);
        viewOffsets[1] = process().readFloat(address() + m_vecViewOffset + 4);
        viewOffsets[2] = process().readFloat(address() + m_vecViewOffset + 8);

        long anglePointer = engineModule().readUnsignedInt(m_dwClientState);
        viewAngles[0] = process().readFloat(anglePointer + m_dwViewAngles);
        viewAngles[1] = process().readFloat(anglePointer + m_dwViewAngles + 4);
        viewAngles[2] = process().readFloat(anglePointer + m_dwViewAngles + 8);

        long boneMatrix = process().readUnsignedInt(address() + m_dwBoneMatrix);
        try {
            bones[0] = process().readFloat(boneMatrix + 0x30 * 6 + 0x0C);
        } catch (Exception e) {
            System.out.println(Game.current().me().address());
            System.out.println(address() + ", " + team);
            System.exit(-1);
        }
        bones[1] = process().readFloat(boneMatrix + 0x30 * 6 + 0x1C);
        bones[2] = process().readFloat(boneMatrix + 0x30 * 6 + 0x2C);

        punch[0] = process().readFloat(address() + m_vecPunch);
        punch[1] = process().readFloat(address() + m_vecPunch + 4);

        hasBomb = false;

        for (int i = 0; i < weaponIds.length; i++) {
            long weaponBase = process().readUnsignedInt(address() + m_hMyWeapons + ((i - 1) * 0x04));

            long entNum = weaponBase & 0xFFF;
            long id = clientModule().readUnsignedInt(m_dwEntityList + (entNum - 1) * 0x10);

            weaponIds[i] = id;
            if (id > 0) {
                activeWeapon.setWeaponID(process().readUnsignedInt(id + m_iWeaponID));
                activeWeapon.setCanReload(process().readBoolean(id + m_bCanReload));
                activeWeapon.setClip1(process().readUnsignedInt(id + m_iClip1));
                activeWeapon.setClip2(process().readUnsignedInt(id + m_iClip2));
                if (activeWeapon.getWeaponID() == 50) {
                    hasBomb = true;
                }
            }
        }

    }
}
