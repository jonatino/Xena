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
import org.xena.plugin.utils.AngleUtils;

import static com.github.jonatino.offsets.Offsets.m_dwGlowObject;

@PluginManifest(name = "Glow ESP", description = "Make entities glow on your screen.")
public final class GlowESPPlugin extends Plugin {
	
	private final AngleUtils angleUtils;
	
	private static final int[] TEAM_CT = {114, 155, 221, 153};
	private static final int[] TEAM_T = {224, 175, 86, 153};
	private static final int[] BOMB_CARRY = {255, 0, 0, 200};
	private static final int[] BOMB_DROPPED = {133, 142, 30, 200};
	
	public GlowESPPlugin(Logger logger, Xena xena) {
		super(logger, xena);
		angleUtils = new AngleUtils(this, 0f, 0f, 0f, 0f, 0f);
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
					int[] c = getColor(entity);
					for (int x = 0; x < 4; x++) {
						process().writeFloat(glowObjectPointer + (x + 1) * 4, c[x] / 255f);
						process().writeByte(entityAddress + 0x70 + x, c[x]);
					}
					
					process().writeBoolean(glowObjectPointer + 0x24, true);
					process().writeBoolean(glowObjectPointer + 0x25, false);
					process().writeBoolean(glowObjectPointer + 0x26, false);
				} catch (Throwable ignored) {
					ignored.printStackTrace();
				}
			}
		}
	}
	
	private int[] getColor(GameEntity entity) {
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
