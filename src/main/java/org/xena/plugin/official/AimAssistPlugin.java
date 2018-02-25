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
import org.xena.Settings;
import org.xena.cs.ClientState;
import org.xena.cs.GameEntity;
import org.xena.cs.Me;
import org.xena.cs.Player;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;
import org.xena.plugin.utils.AngleUtils;
import org.xena.plugin.utils.Vector;

@PluginManifest(name = "Aim Assist", description = "Helps you to stay on target.")
public final class AimAssistPlugin extends Plugin {
	
	private final AngleUtils aimHelper;
	private final Vector aim = new Vector();
	private long prevFired = 0;
	private Player lastTarget = null;
	
	public AimAssistPlugin() {
		aimHelper = new AngleUtils(this, Settings.AIM_ASSIST_STRENGTH, 1.7F, 2.5F, 1.7F, 2.5F);
	}
	
	@Override
	public void pulse(ClientState clientState, Me me, Indexer<GameEntity> entities) {
		long shotsFired = me.getShotsFired();
		if (shotsFired < 1 || shotsFired < prevFired) {
			prevFired = 0;
			return;
		}
		
		Player target = me.getTarget();
		if (lastTarget != null && target == null) {
			if (!lastTarget.isDead() && lastTarget.isSpotted()) {
				target = lastTarget;
			} else {
				lastTarget = null;
			}
		}
		
		if (target == null) {
			return;
		}
		
		if (shotsFired > 1 && shotsFired >= prevFired) {
			try {
				long shots;
				if (aimHelper.canShoot(me, target) && (shots = me.getShotsFired()) > 1 && shots >= prevFired) {
					float delta = aimHelper.delta(me.getViewOrigin(), target.getBones());
					if (delta < 190) {
						return;
					}
					aimHelper.velocityComp(me, target, target.getBones());
					aimHelper.calculateAngle(me, me.getViewOrigin(), target.getBones(), aim);
					aimHelper.setAngleSmooth(aim, target.getViewAngles());
					
					prevFired = me.getShotsFired();
					lastTarget = target;
				} else {
					lastTarget = null;
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			prevFired = 0;
			lastTarget = null;
		}
	}
	
}
