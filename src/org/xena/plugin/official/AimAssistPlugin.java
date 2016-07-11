package org.xena.plugin.official;

import org.xena.Xena;
import org.xena.cs.ClientState;
import org.xena.cs.GameEntity;
import org.xena.cs.Me;
import org.xena.cs.Player;
import org.xena.logging.Logger;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;
import org.xena.plugin.utils.AngleUtils;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@PluginManifest(name = "Aim Assist", description = "Helps you to stay on target.")
public final class AimAssistPlugin extends Plugin {

    private static final int[] targetBones = {5, 5, 5, 5, 5, 6, 6, 8, 8, 8};

    private final AngleUtils aimHelper;

    public AimAssistPlugin(Logger logger, Xena xena) {
        super(logger, xena);
        aimHelper = new AngleUtils(this, targetBones, 40.5F, 1.7F, 2.5F, 1.7F, 2.5F);
    }

    private final float[] aim = new float[3];
    private final float[] enemyPosition = new float[3];
    private final float[] angle = new float[3];

    private int targetBone = -1;

    private final Random random = new Random();

    private final ExecutorService service = Executors.newSingleThreadExecutor();

    private volatile int prevFired = 0;

    private int lastCrosshair = 0;

    @Override
    public void pulse(ClientState clientState, Me me, GameEntity[] entities, Collection<Player> players) {
        int shotsFired = aimHelper.getShotsFired(me);
        if (shotsFired < 1 || shotsFired < prevFired) {
            prevFired = 0;
            return;
        }

        int target = aimHelper.getCrosshairID(me);
        if (lastCrosshair > 0 && target == -1) {
            if (!aimHelper.isDead(lastCrosshair) && aimHelper.isSpotted(lastCrosshair)) {
                target = lastCrosshair;
            }
        }

        if (shotsFired > 1 && shotsFired >= prevFired && target >= 0) {
            try {
                int shots;
                if (targetBone == -1) {
                    targetBone = targetBones[random.nextInt(targetBones.length)];
                }
                if (aimHelper.canShoot(me, target) && (shots = aimHelper.getShotsFired(me)) > 1 && shots >= prevFired && aimHelper.getBones(target, enemyPosition, targetBone) != null) {
                    float delta = aimHelper.delta(me.getPosition(), enemyPosition);
                    if (delta < 190) {
                        return;
                    }
                    aimHelper.velocityComp(me, target, enemyPosition);
                    aimHelper.calculateAngle(me, me.getPosition(), enemyPosition, aim);
                    aimHelper.normalizeAngle(aim);
                    aimHelper.getAngle(angle);
                    aimHelper.normalizeAngle(angle);
                    aimHelper.setAngleSmooth(aim, angle);

                    prevFired = aimHelper.getShotsFired(me);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            prevFired = 0;
        }
        lastCrosshair = aimHelper.getCrosshairID(me);
    }

    private int random(int a, int b) {
        return ThreadLocalRandom.current().nextInt(a, b);
    }

}
