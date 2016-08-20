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
import org.xena.cs.*;
import org.xena.logging.Logger;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;

import static com.github.jonatino.offsets.Offsets.m_dwGlowObject;

@PluginManifest(name = "Glow ESP", description = "Make entities glow on your screen.")
public final class GlowESPPlugin extends Plugin {

	private static final float[] TEAM_CT = {114.0f, 155.0f, 221.0f, 153.0f};
	private static final float[] TEAM_T = {224.0f, 175.0f, 86.0f, 153.0f};
	private static final float[] BOMB_CARRY = {255f, 0.0f, 0.0f, 200.0f};
	private static final float[] BOMB_DROPPED = {133f, 142.0f, 30.0f, 200.0f};

	public GlowESPPlugin(Logger logger, Xena xena) {
		super(logger, xena);
	}

	@Override
	public void pulse(ClientState clientState, Me me, Indexer<GameEntity> entities) {
		long pointerGlow = client().readUnsignedInt(m_dwGlowObject);
		long glowObjectCount = client().readUnsignedInt(m_dwGlowObject + 4);

		for (int i = 0; i < glowObjectCount; i++) {
			long glowObjectPointer = pointerGlow + (i * 56);
			long entityAddress = process().readUnsignedInt(glowObjectPointer);

			if (entityAddress < 0x200) {
				continue;
			}

			GameEntity entity = Game.current().get(entityAddress);
			if (entity != null) {
				try {
					float[] c = getColor(entity);
					for (int x = 0; x < 4; x++) {
						process().writeFloat(glowObjectPointer + (x + 1) * 4, c[x] / 255.0f);
					}
					process().writeBoolean(glowObjectPointer + 0x24, true);
					process().writeBoolean(glowObjectPointer + 0x25, false);
				} catch (Throwable ignored) {
					ignored.printStackTrace();
				}
			}
		}
		sleep(64);
	}

	private float[] getColor(GameEntity entity) {
		if (entity.getTeam() == 3) {
			return TEAM_CT;
		}
		if (entity.isBombCarrier()) {
			return BOMB_CARRY;
		}
		if (entity.type() == EntityType.CC4) {
			return BOMB_DROPPED;
		}
		return TEAM_T;
	}

}
