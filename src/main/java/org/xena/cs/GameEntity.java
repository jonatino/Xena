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
import org.xena.plugin.utils.Vector;

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
	private final Vector viewOrigin = new Vector();
	
	@Getter
	protected final Vector velocity = new Vector();
	
	@Getter
	protected final Vector viewOffsets = new Vector();
	
	@Getter
	protected final Vector viewAngles = new Vector();
	
	@Getter
	protected final Vector bones = new Vector();
	
	@Getter
	protected final Vector punch = new Vector();
	
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
			
			viewOrigin.x = process().readFloat(address() + m_vecOrigin);
			viewOrigin.y = process().readFloat(address() + m_vecOrigin + 4);
			viewOrigin.z = process().readFloat(address() + m_vecOrigin + 8);
			
			velocity.x = process().readFloat(address() + m_vecVelocity);
			velocity.y = process().readFloat(address() + m_vecVelocity + 4);
			velocity.z = process().readFloat(address() + m_vecVelocity + 8);
			
			viewOffsets.x = process().readFloat(address() + m_vecViewOffset);
			viewOffsets.y = process().readFloat(address() + m_vecViewOffset + 4);
			viewOffsets.z = process().readFloat(address() + m_vecViewOffset + 8);
			
			long anglePointer = engineModule().readUnsignedInt(m_dwClientState);
			viewAngles.x = process().readFloat(anglePointer + m_dwViewAngles);
			viewAngles.y = process().readFloat(anglePointer + m_dwViewAngles + 4);
			viewAngles.z = process().readFloat(anglePointer + m_dwViewAngles + 8);
			
			long boneMatrix = process().readUnsignedInt(address() + m_dwBoneMatrix);
			if (boneMatrix > 0) {
				//Bones bone = Bones.roll();
				Bones bone = Bones.HEAD;
				try {
					bones.x = process().readFloat(boneMatrix + 0x30 * bone.getId() + 0x0C);
					bones.y = process().readFloat(boneMatrix + 0x30 * bone.getId() + 0x1C);
					bones.z = process().readFloat(boneMatrix + 0x30 * bone.getId() + 0x2C);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			punch.x = process().readFloat(address() + m_vecPunch);
			punch.y = process().readFloat(address() + m_vecPunch + 4);
			
			dead = process().readByte(address() + m_lifeState) != 0;
			spotted = process().readUnsignedInt(address() + m_bSpotted) != 0;
		}
	}
	
	public Vector getEyePos() {
		return viewOffsets.plus(viewOrigin);
	}
	
	public EntityType type() {
		return EntityType.byId(classId);
	}
	
	public boolean shouldUpdate() {
		return team == 2 || team == 3 && !dead;
	}
	
}
