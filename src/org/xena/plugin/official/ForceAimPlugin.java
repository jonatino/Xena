package org.xena.plugin.official;

import org.xena.Xena;
import org.xena.cs.ClientState;
import org.xena.cs.GameEntity;
import org.xena.cs.Me;
import org.xena.cs.Player;
import org.xena.keylistener.NativeKeyUtils;
import org.xena.logging.Logger;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;
import org.xena.plugin.utils.AngleUtils;

import java.util.Collection;
import java.util.Random;

@PluginManifest(name = "Aim Assist", description = "Helps you to stay on target.")
public final class ForceAimPlugin extends Plugin {

    private static final int[] targetBones = {6};

    private final AngleUtils aimHelper;

    public ForceAimPlugin(Logger logger, Xena xena) {
        super(logger, xena);
        aimHelper = new AngleUtils(this, targetBones, 40.5F, 1.7F, 2.5F, 1.7F, 2.5F);
    }

    private final float[] aim = new float[3];
    private final float[] enemyPosition = new float[3];
    private final float[] angle = new float[3];

    private int targetBone = -1;

    private final Random random = new Random();

    private int lastCrosshair = 0;

    @Override
    public void pulse(ClientState clientState, Me me, GameEntity[] entities, Collection<Player> players) {
        if (NativeKeyUtils.isKeyDown(164)) {
            int target = aimHelper.getCrosshairID(me);
            if (lastCrosshair > 0 && target == -1) {
                if (!aimHelper.isDead(lastCrosshair) && aimHelper.isSpotted(lastCrosshair)) {
                    target = lastCrosshair;
                } else {
                    lastCrosshair = -1;
                }
            }

            if (target < 0) {
                return;
            }

            if (target >= 0) {
                try {
                    if (targetBone == -1) {
                        targetBone = targetBones[random.nextInt(targetBones.length)];
                    }
                    if (aimHelper.canShoot(me, target) && aimHelper.getBones(target, enemyPosition, targetBone) != null) {
                        aimHelper.velocityComp(me, target, enemyPosition);
                        aimHelper.calculateAngle(me, me.getPosition(), enemyPosition, aim);
                        aimHelper.normalizeAngle(aim);
                        aimHelper.getAngle(angle);
                        aimHelper.normalizeAngle(angle);
                        aimHelper.setAngleSmooth(aim, angle);
                    } else {
                        lastCrosshair = -1;
                        return;
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            lastCrosshair = target;
        } else {
            lastCrosshair = -1;
        }
    }

}
