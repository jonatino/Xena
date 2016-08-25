/*
 *    Copyright 2016 Jonathan Beaudoin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xena.plugin.official;

import org.xena.Indexer;
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
import org.xena.plugin.utils.Vector;

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
	
	private final Vector aim = new Vector();
	
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
						if (aimHelper.delta(me.getViewOrigin(), entity.getBones()) > 3000) {
							continue;
						}
						
						if (aimHelper.canShoot(me, entity)) {
							lastTarget = (Player) entity;
						} else {
							lastTarget = null;
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
				aimHelper.calculateAngle(me, me.getViewOrigin(), lastTarget.getBones(), aim);
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
