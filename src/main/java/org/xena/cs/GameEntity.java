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

package org.xena.cs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.github.jonatino.OffsetManager.engineModule;
import static com.github.jonatino.OffsetManager.process;
import static com.github.jonatino.offsets.Offsets.*;

@ToString
public class GameEntity extends GameObject {
	
	@Getter
	@Setter
	protected int classId;
	
	@Getter
	protected long index;
	
	@Getter
	protected long model;
	
	@Getter
	protected long boneMatrix;
	
	@Setter
	@Getter
	protected long team;
	
	@Getter
	protected boolean running;
	
	@Getter
	protected boolean dormant;
	
	@Getter
	private final float[] position = new float[3];
	
	@Getter
	protected final float[] velocity = new float[3];
	
	@Getter
	protected final float[] viewOffsets = new float[3];
	
	@Getter
	protected final float[] viewAngles = new float[3];
	
	@Getter
	protected final float[] bones = new float[3];
	
	@Getter
	protected final float[] punch = new float[2];
	
	@Getter
	protected boolean dead;
	
	@Getter
	protected boolean spotted;
	
	@Getter
	protected boolean bombCarrier;
	
	public void update() {
		if (shouldUpdate()) {
			model = process().readUnsignedInt(address() + m_dwModel);
			boneMatrix = process().readUnsignedInt(address() + m_dwBoneMatrix);
			running = process().readBoolean(address() + m_bMoveType);
			dormant = process().readBoolean(address() + m_bDormant);
			
			position[0] = process().readFloat(address() + m_vecOrigin);
			position[1] = process().readFloat(address() + m_vecOrigin + 4);
			position[2] = process().readFloat(address() + m_vecOrigin + 8);
			
			velocity[0] = process().readFloat(address() + m_vecVelocity);
			velocity[1] = process().readFloat(address() + m_vecVelocity + 4);
			velocity[2] = process().readFloat(address() + m_vecVelocity + 8);
			
			viewOffsets[0] = process().readFloat(address() + m_vecViewOffset);
			viewOffsets[1] = process().readFloat(address() + m_vecViewOffset + 4);
			viewOffsets[2] = process().readFloat(address() + m_vecViewOffset + 8);
			
			long anglePointer = engineModule().readUnsignedInt(m_dwClientState);
			viewAngles[0] = process().readFloat(anglePointer + m_dwViewAngles);
			viewAngles[1] = process().readFloat(anglePointer + m_dwViewAngles + 4);
			viewAngles[2] = process().readFloat(anglePointer + m_dwViewAngles + 8);
			
			long boneMatrix = process().readUnsignedInt(address() + m_dwBoneMatrix);
			if (boneMatrix > 0) {
				//Bones bone = Bones.roll();
				Bones bone = Bones.HEAD;
				try {
					bones[0] = process().readFloat(boneMatrix + 0x30 * bone.getId() + 0x0C);
					bones[1] = process().readFloat(boneMatrix + 0x30 * bone.getId() + 0x1C);
					bones[2] = process().readFloat(boneMatrix + 0x30 * bone.getId() + 0x2C);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			punch[0] = process().readFloat(address() + m_vecPunch);
			punch[1] = process().readFloat(address() + m_vecPunch + 4);
			
			dead = process().readByte(address() + m_lifeState) != 0;
			spotted = process().readUnsignedInt(address() + m_bSpotted) != 0;
		}
	}
	
	public EntityType type() {
		return EntityType.byId(classId);
	}
	
	public boolean shouldUpdate() {
		return team == 2 || team == 3 && !dead;
	}
	
}
