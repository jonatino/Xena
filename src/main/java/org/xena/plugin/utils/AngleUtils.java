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

package org.xena.plugin.utils;

import org.xena.cs.GameEntity;
import org.xena.cs.Me;
import org.xena.cs.Player;
import org.xena.plugin.Plugin;

import java.util.Random;

import static com.github.jonatino.offsets.Offsets.m_dwClientState;
import static com.github.jonatino.offsets.Offsets.m_dwViewAngles;

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
	
	public Vector normalizeAngle(Vector vector) {
		if (vector.x > 89.0f && vector.x <= 180.0f) {
			vector.x = 89.0f;
		}
		if (vector.x > 180.f) {
			vector.x -= 360.f;
		}
		if (vector.x < -89.0f) {
			vector.x = -89.0f;
		}
		if (vector.y > 180.f) {
			vector.y -= 360.f;
		}
		if (vector.y < -180.f) {
			vector.y += 360.f;
		}
		if (vector.z != 0.0f) {
			vector.z = 0.0f;
		}
		return vector;
	}
	
	private final Vector delta = new Vector();
	
	public void calculateAngle(Player player, Vector src, Vector dst, Vector angles) {
		float pitchreduction = randomFloat(lowestPitch, highestPitch);
		float yawreduction = randomFloat(lowestYaw, highestYaw);
		delta.x = src.x - dst.x;
		delta.y = src.y - dst.y;
		delta.z = (src.z + player.getViewOffsets().z) - dst.z;
		double hyp = Math.sqrt(delta.x * delta.x + delta.y * delta.y);
		angles.x = (float) (Math.atan(delta.z / hyp) * (180 / Math.PI) - player.getPunch().x * pitchreduction);
		angles.y = (float) (Math.atan(delta.y / delta.x) * (180 / Math.PI) - player.getPunch().y * yawreduction);
		angles.z = 0.0f;
		if (delta.x >= 0.0) {
			angles.y += 180.0f;
		}
	}
	
	private final Random random = new Random();
	
	public float randomFloat(float a, float b) {
		return a + (random.nextFloat() * (b - a));
	}
	
	public boolean canShoot(Me me, GameEntity target) {
		long weaponID = me.getActiveWeapon().getWeaponID();
		if (weaponID == 42) {
			return false;
		}
		return me.getActiveWeapon().getClip1() > 0 && !target.isDead() && !me.isDead() && target.getTeam() != me.getTeam();
	}
	
	private static Vector smoothedAngles = new Vector();
	
	public void setAngleSmooth(Vector dest, Vector orig) {
		smoothedAngles.x = dest.x - orig.x;
		smoothedAngles.y = dest.y - orig.y;
		smoothedAngles.z = 0.0f;
		normalizeAngle(smoothedAngles);
		smoothedAngles.x = orig.x + smoothedAngles.x / 100.0f * smoothing;
		smoothedAngles.y = orig.y + smoothedAngles.y / 100.0f * smoothing;
		smoothedAngles.z = 0.0f;
		normalizeAngle(smoothedAngles);
		
		setAngles(smoothedAngles);
	}
	
	public void setAngles(Vector angles) {
		normalizeAngle(angles);
		if (Float.isNaN(angles.x) || Float.isNaN(angles.y) || Float.isNaN(angles.z)) {
			return;
		}
		long anglePointer = plugin.engine().readUnsignedInt(m_dwClientState);
		plugin.process().writeFloat(anglePointer + m_dwViewAngles, angles.x);
		plugin.process().writeFloat(anglePointer + m_dwViewAngles + 4, angles.y);
	}
	
	public void velocityComp(Me me, Player target, Vector enemyPos) {
		enemyPos.x = enemyPos.x + (target.getVelocity().x / 100.f) * (40.f / smoothing);
		enemyPos.y = enemyPos.y + (target.getVelocity().y / 100.f) * (40.f / smoothing);
		enemyPos.z = enemyPos.z + (target.getVelocity().z / 100.f) * (40.f / smoothing);
		enemyPos.x = enemyPos.x - (me.getVelocity().x / 100.f) * (40.f / smoothing);
		enemyPos.y = enemyPos.y - (me.getVelocity().y / 100.f) * (40.f / smoothing);
		enemyPos.z = enemyPos.z - (me.getVelocity().z / 100.f) * (40.f / smoothing);
	}
	
	public float delta(Vector me, Vector them) {
		float delta = 0F;
		delta += Math.abs(((me.x + Short.MAX_VALUE) - (them.x + Short.MAX_VALUE)));
		delta += Math.abs((me.y + Short.MAX_VALUE) - (them.y + Short.MAX_VALUE));
		return delta;
	}
	
}
