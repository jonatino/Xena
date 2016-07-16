package org.xena.plugin.official;

import org.xena.Indexer;
import org.xena.Xena;
import org.xena.cs.*;
import org.xena.keylistener.NativeKeyUtils;
import org.xena.logging.Logger;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;
import org.xena.plugin.utils.AngleUtils;

import java.awt.*;
import java.awt.event.InputEvent;

@PluginManifest(name = "Spin Bot", description = "Helps you to stay on target.")
public final class SpinBotPlugin extends Plugin {

    private final AngleUtils aimHelper;

    public SpinBotPlugin(Logger logger, Xena xena) {
        super(logger, xena);
        aimHelper = new AngleUtils(this, 40.5F, 1.7F, 2.5F, 1.7F, 2.5F);
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private final float[] aim = new float[3];

    private Player lastTarget = null;
    private int lastIdx;

    private Robot robot;

    @Override
    public void pulse(ClientState clientState, Me me, Indexer<GameEntity> entities) {
        if (NativeKeyUtils.isLeftCtrlDown()) {
            if (lastTarget == null) {
                while (lastTarget == null) {
                    if (lastIdx + 1 >= entities.size()) {
                        lastIdx = 0;
                        break;
                    }
                    GameEntity entity = entities.get(lastIdx++);
                    try {
                        if (entity.type() == EntityType.CCSPlayer && entity.isPlayer()) {
                            // System.out.println(aimHelper.delta(me.getPosition(), entity.asPlayer().getBones()));
                            if (aimHelper.delta(me.getPosition(), entity.asPlayer().getBones()) > 3000) {
                                continue;
                            }

                            if (aimHelper.canShoot(me, entity.asPlayer())) {
                                lastTarget = entity.asPlayer();
                            } else {
                                System.out.println(me.getActiveWeapon().getClip1() + ", " + me.isDead() + ", " + lastTarget.getTeam() + ", " + me.getTeam());
                                lastTarget = null;
                            }
                        }
                    } catch (Throwable ignored) {
                        ignored.printStackTrace();
                    }
                }
            }

            if (lastTarget == null) {
                return;
            }

            if (aimHelper.canShoot(me, lastTarget)) {
                aimHelper.velocityComp(me, lastTarget, lastTarget.getBones());
                aimHelper.calculateAngle(me, me.getPosition(), lastTarget.getBones(), aim);
                aimHelper.setAngleSmooth(aim, lastTarget.getViewAngles());
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            } else {
                System.out.println(me.getActiveWeapon().getClip1() + ", " + me.isDead() + ", " + lastTarget.getTeam() + ", " + me.getTeam());
                lastTarget = null;
            }
        } else {
            lastTarget = null;
        }
        sleep(10);
    }

}
