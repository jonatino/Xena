package org.xena.plugin.utils;

import org.xena.cs.Me;
import org.xena.cs.Player;
import org.xena.plugin.Plugin;

import java.util.Random;

import static org.abendigo.offsets.Offsets.m_dwClientState;
import static org.abendigo.offsets.Offsets.m_dwViewAngles;

public final class AngleUtils {

    private final Plugin plugin;

    private final float smoothing;
    private final float lowestPitch;
    private final float highestPitch;
    private final float lowestYaw;
    private final float highestYaw;

    public AngleUtils(Plugin plugin, float smoothing, float lowestPitch, float highestPitch, float lowestYaw, float highestYaw) {
        this.plugin = plugin;
        this.smoothing = smoothing;
        this.lowestPitch = lowestPitch;
        this.highestPitch = highestPitch;
        this.lowestYaw = lowestYaw;
        this.highestYaw = highestYaw;
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

    private final float[] delta = new float[3];

    public float[] calculateAngle(Player player, float[] src, float[] dst, float[] angles) {
        float pitchreduction = randomFloat(lowestPitch, highestPitch);
        float yawreduction = randomFloat(lowestYaw, highestYaw);
        delta[0] = src[0] - dst[0];
        delta[1] = src[1] - dst[1];
        delta[2] = (src[2] + player.getViewOffsets()[2]) - dst[2];
        double hyp = Math.sqrt(delta[0] * delta[0] + delta[1] * delta[1]);
        angles[0] = (float) (Math.atan(delta[2] / hyp) * (180 / Math.PI) - player.getPunch()[0] * pitchreduction);
        angles[1] = (float) (Math.atan(delta[1] / delta[0]) * (180 / Math.PI) - player.getPunch()[1] * yawreduction);
        angles[2] = 0.0f;
        if (delta[0] >= 0.0) {
            angles[1] += 180.0f;
        }
        return angles;
    }

    private final Random random = new Random();

    public float randomFloat(float a, float b) {
        return a + (random.nextFloat() * (b - a));
    }

    public boolean canShoot(Me me, Player target) {
        long weaponID = me.getActiveWeapon().getWeaponID();
        if (weaponID == 42) {
            return false;
        }
	    return me.getActiveWeapon().getClip1() > 0 && !target.isDead() && !me.isDead()/* && target.getTeam() != me.getTeam()*/;
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
        long anglePointer = plugin.engine().readUnsignedInt(m_dwClientState);
        plugin.process().writeFloat(anglePointer + m_dwViewAngles, angles[0]);
        plugin.process().writeFloat(anglePointer + m_dwViewAngles + 4, angles[1]);
        return angles;
    }

    public float[] velocityComp(Me me, Player target, float[] enemyPos) {
        enemyPos[0] = enemyPos[0] + (target.getVelocity()[0] / 100.f) * (40.f / smoothing);
        enemyPos[1] = enemyPos[1] + (target.getVelocity()[1] / 100.f) * (40.f / smoothing);
        enemyPos[2] = enemyPos[2] + (target.getVelocity()[2] / 100.f) * (40.f / smoothing);
        enemyPos[0] = enemyPos[0] - (me.getVelocity()[0] / 100.f) * (40.f / smoothing);
        enemyPos[1] = enemyPos[1] - (me.getVelocity()[1] / 100.f) * (40.f / smoothing);
        enemyPos[2] = enemyPos[2] - (me.getVelocity()[2] / 100.f) * (40.f / smoothing);
        return enemyPos;
    }

    public float delta(float[] me, float[] them) {
        float delta = 0F;
        delta += Math.abs(((me[0] + Short.MAX_VALUE) - (them[0] + Short.MAX_VALUE)));
        delta += Math.abs((me[1] + Short.MAX_VALUE) - (them[1] + Short.MAX_VALUE));
        return delta;
    }

}
