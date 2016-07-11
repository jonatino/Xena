package org.xena.plugin.utils;

import org.xena.cs.Me;
import org.xena.cs.Player;
import org.xena.plugin.Plugin;

import java.util.Random;

import static org.abendigo.offsets.Offsets.*;

public final class AngleUtils {

    private final Plugin plugin;

    private final int[] targetBones;
    private final float smoothing;
    private final float lowestPitch;
    private final float highestPitch;
    private final float lowestYaw;
    private final float highestYaw;

    public AngleUtils(Plugin plugin, int[] targetBones, float smoothing, float lowestPitch, float highestPitch, float lowestYaw, float highestYaw) {
        this.plugin = plugin;
        this.targetBones = targetBones;
        this.smoothing = smoothing;
        this.lowestPitch = lowestPitch;
        this.highestPitch = highestPitch;
        this.lowestYaw = lowestYaw;
        this.highestYaw = highestYaw;
    }

    public int getBaseEntity(int playerNumber) {
        return plugin.client().readInt(m_dwEntityList + (16 * playerNumber));
    }

    public float[] getBones(int playerNumber, float[] bones, int targetBone) {
        if (validAddress(playerNumber)) {
            int boneMatrix = plugin.process().readInt(getBaseEntity(playerNumber) + m_dwBoneMatrix);
            System.out.println(boneMatrix);
            bones[0] = plugin.process().readFloat(boneMatrix + 0x30 * targetBone + 0x0C);
            bones[1] = plugin.process().readFloat(boneMatrix + 0x30 * targetBone + 0x1C);
            bones[2] = plugin.process().readFloat(boneMatrix + 0x30 * targetBone + 0x2C);
            return bones;
        } else {
            return null;
        }
    }

    public boolean isSpotted(int playerNumber) {
        if (validAddress(playerNumber)) {
            int baseAddress = getBaseEntity(playerNumber);
            return plugin.process().readInt(baseAddress + m_bSpotted) > 0;
        } else {
            return false;
        }
    }

    public int getShotsFired(Player player) {
        return plugin.process().readInt(player.address() + m_iShotsFired);
    }

    public boolean isDead(Player player) {
        return plugin.process().readBoolean(player.address() + m_lifeState);
    }

    public boolean isDead(int playerNumber) {
        if (validAddress(playerNumber)) {
            int baseAddress = getBaseEntity(playerNumber);
            return baseAddress < 0x200 || isPlayer(baseAddress) && playerNumber >= 0 && plugin.process().readBoolean(baseAddress + m_lifeState);
        } else {
            return true;
        }
    }

    public boolean isPlayer(int address) {
        int team = plugin.process().readInt(address + m_iTeamNum);
        return team == 2 || team == 3;
    }

    public boolean validAddress(int playerNumber) {
        return getBaseEntity(playerNumber) >= 0x200;
    }

    public float[] normalizeAngle(float[] angle) {
        if (angle[0] > 89.0f && angle[0] <= 180.0f) {
            angle[0] = 89.0f;
        }
        if (angle[0] > 180.f) {
            angle[0] -= 360.f;
        }
        if (angle[0] < -89.0f) {
            angle[0] = -89.0f;
        }
        if (angle[1] > 180.f) {
            angle[1] -= 360.f;
        }
        if (angle[1] < -180.f) {
            angle[1] += 360.f;
        }
        if (angle[2] != 0.0f) {
            angle[2] = 0.0f;
        }
        return angle;
    }

    private final float[] punch = new float[3];
    private final float[] delta = new float[3];

    public float[] calculateAngle(Player player, float[] src, float[] dst, float[] angles) {
        getPunch(player, punch);
        float pitchreduction = randomFloat(lowestPitch, highestPitch);
        float yawreduction = randomFloat(lowestYaw, highestYaw);
        delta[0] = src[0] - dst[0];
        delta[1] = src[1] - dst[1];
        delta[2] = (src[2] + player.getViewAngles()[2]) - dst[2];
        double hyp = Math.sqrt(delta[0] * delta[0] + delta[1] * delta[1]);
        angles[0] = (float) (Math.atan(delta[2] / hyp) * (180 / Math.PI) - punch[0] * pitchreduction);
        angles[1] = (float) (Math.atan(delta[1] / delta[0]) * (180 / Math.PI) - punch[1] * yawreduction);
        angles[2] = 0.0f;
        if (delta[0] >= 0.0) {
            angles[1] += 180.0f;
        }
        return angles;
    }

    private final Random random = new Random();

    public int getCrosshairID(Player player) {
        return plugin.process().readInt(player.address() + m_iCrossHairID) - 1;
    }

    public float randomFloat(float a, float b) {
        return a + (random.nextFloat() * (b - a));
    }

    public void getPunch(Player player, float[] punch) {
        punch[0] = plugin.process().readFloat(player.address() + m_vecPunch);
        punch[1] = plugin.process().readFloat(player.address() + m_vecPunch + 4);
    }

    public int getTeam(int PlayerNumber) {
        return plugin.process().readInt(getBaseEntity(PlayerNumber) + m_iTeamNum);
    }

    public boolean canShoot(Me me, int target) {
        int weaponID = me.getActiveWeapon().getWeaponID();
        if (weaponID == 42) {
            return false;
        }
        return me.getActiveWeapon().getClip1() > 0 && !isDead(me) && target < 64 && target >= 0 && getTeam(target) != me.getTeam() && !isDead(target) && isSpotted(target);
    }

    private static float[] smoothedAngles = new float[3];

    public void setAngleSmooth(float[] dest, float[] orig) {
        smoothedAngles[0] = dest[0] - orig[0];
        smoothedAngles[1] = dest[1] - orig[1];
        smoothedAngles[2] = 0.0f;
        normalizeAngle(smoothedAngles);
        smoothedAngles[0] = orig[0] + smoothedAngles[0] / 100.0f * smoothing;
        smoothedAngles[1] = orig[1] + smoothedAngles[1] / 100.0f * smoothing;
        smoothedAngles[2] = 0.0f;
        normalizeAngle(smoothedAngles);

        setAngles(smoothedAngles);
    }

    public float[] setAngles(float[] angles) {
        normalizeAngle(angles);
        if (Float.isNaN(angles[0]) || Float.isNaN(angles[1]) || Float.isNaN(angles[2])) {
            return angles;
        }
        int anglePointer = plugin.engine().readInt(m_dwClientState);
        plugin.process().writeFloat(anglePointer + m_dwViewAngles, angles[0]);
        plugin.process().writeFloat(anglePointer + m_dwViewAngles + 4, angles[1]);
        return angles;
    }

    public float[] getVelocity(int playerNumber, float[] buffer) {
        buffer[0] = plugin.process().readFloat(getBaseEntity(playerNumber) + m_vecVelocity);
        buffer[1] = plugin.process().readFloat(getBaseEntity(playerNumber) + m_vecVelocity + 4);
        buffer[2] = plugin.process().readFloat(getBaseEntity(playerNumber) + m_vecVelocity + 8);
        return buffer;
    }

    public float[] getAngle(float[] angles) {
        int anglePointer = plugin.engine().readInt(m_dwClientState);
        angles[0] = plugin.process().readFloat(anglePointer + m_dwViewAngles);
        angles[1] = plugin.process().readFloat(anglePointer + m_dwViewAngles + 4);
        angles[2] = plugin.process().readFloat(anglePointer + m_dwViewAngles + 8);
        return angles;
    }

    private final float[] enemyVelocity = new float[3];

    public float[] velocityComp(Player player, int enemyNumber, float[] enemyPos) {
        float[] myVelocity = player.getVelocity();
        getVelocity(enemyNumber, enemyVelocity);
        enemyPos[0] = enemyPos[0] + (enemyVelocity[0] / 100.f) * (40.f / smoothing);
        enemyPos[1] = enemyPos[1] + (enemyVelocity[1] / 100.f) * (40.f / smoothing);
        enemyPos[2] = enemyPos[2] + (enemyVelocity[2] / 100.f) * (40.f / smoothing);
        enemyPos[0] = enemyPos[0] - (myVelocity[0] / 100.f) * (40.f / smoothing);
        enemyPos[1] = enemyPos[1] - (myVelocity[1] / 100.f) * (40.f / smoothing);
        enemyPos[2] = enemyPos[2] - (myVelocity[2] / 100.f) * (40.f / smoothing);
        return enemyPos;
    }

    public float delta(float[] me, float[] them) {
        float delta = 0F;
        delta += Math.abs(((me[0] + Short.MAX_VALUE) - (them[0] + Short.MAX_VALUE)));
        delta += Math.abs((me[1] + Short.MAX_VALUE) - (them[1] + Short.MAX_VALUE));
        return delta;
    }

}
