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

@PluginManifest(name = "Aim Assist", description = "Helps you to stay on target.")
public final class ForceAimPlugin extends Plugin {

	private final AngleUtils aimHelper;

	public ForceAimPlugin(Logger logger, Xena xena) {
		super(logger, xena);
		aimHelper = new AngleUtils(this, 40.5F, 1.7F, 2.5F, 1.7F, 2.5F);
	}

	private float[] aim = new float[3];
	private final float[] lastaim = new float[3];

	private Player lastTarget = null;

	@Override
	public void pulse(ClientState clientState, Me me, Indexer<GameEntity> players) {
		if (NativeKeyUtils.isLeftAltDown()) {

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

			if (target == null) {
				lastaim[0] = 0;
				lastaim[1] = 0;
				return;
			}

			//System.out.println(target + ", " + target.address());

			if (aimHelper.canShoot(me, target)) {
				aimHelper.velocityComp(me, target, target.getBones());
				aimHelper.calculateAngle(me, me.getPosition(), target.getBones(), aim);
				aimHelper.setAngleSmooth(aim, target.getViewAngles());
				//aimang = localViewAngles - aimAng;

				float deltax = aim[0] - lastaim[0];
				float deltay = lastaim[1] - aim[1];

				float x = deltax / (0.022f * 2.0f * 1.0f); // your formula. I have 2 sens in-game, using 6/11 windows sensitivity
				float y = deltay / (0.022f * 2.0f * 1.0f);

				//System.out.println(deltax+", "+deltay+", "+ x +", "+ y);
				//MouseMove(x, y);
				lastTarget = target;
			} else {
				lastTarget = null;
			}
		} else {
			lastTarget = null;
		}
	}

	public static final int MOUSEEVENTF_MOVE = 0x0001;
	public static final int MOUSEEVENTF_ABSOLUTE = 0x8000;

	public static void MouseMove(float delta_x, float delta_y) {
		int mouse_move_x = (int) delta_x;
		int mouse_move_y = (int) delta_y;

		org.xena.natives.User32.mouse_event(MOUSEEVENTF_MOVE, mouse_move_x, mouse_move_y, 0, null);
	}

}
