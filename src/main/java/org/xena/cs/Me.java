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

import static com.github.jonatino.OffsetManager.clientModule;
import static com.github.jonatino.OffsetManager.process;
import static com.github.jonatino.offsets.Offsets.*;

public class Me extends Player {

	@Getter
	private long crosshair;

	@Getter
	private Player target;

	@Getter
	private long shotsFired;

	@Override
	public void update() {
		super.update();
		long activeWeaponIndex = process().readUnsignedInt(address() + m_hActiveWeapon) & 0xFFF;
		for (int i = 0; i < weaponIds.length; i++) {
			long currentWeaponIndex = process().readUnsignedInt(address() + m_hMyWeapons + ((i - 1) * 0x04)) & 0xFFF;
			long weaponAddress = clientModule().readUnsignedInt(m_dwEntityList + (currentWeaponIndex - 1) * 0x10);

			if (weaponAddress > 0) {
				processWeapon(weaponAddress, 0, activeWeaponIndex == currentWeaponIndex);
			}
		}
/*		if (activeWeapon.getWeaponID() == 42 || activeWeapon.getWeaponID() == 516) {
			int modelAddress = process().readInt(address() + m_hViewModel) & 0xFFF;
			long ds = clientModule().readUnsignedInt(m_dwEntityList + (modelAddress - 1) * 0x10);
			process().writeInt(ds + m_nModelIndex, 403);
			process().writeInt(weaponAddress + iViewModelIndex, 403);
			process().writeInt(weaponAddress + iWorldModelIndex, 404);
			process().writeInt(weaponAddress + m_iWorldDroppedModelIndex, 405);
			process().writeInt(weaponAddress + m_iItemDefinitionIndex, 515);
			process().writeInt(weaponAddress + m_iWeaponID, 516);
		}*/

		crosshair = process().readUnsignedInt(address() + m_iCrossHairID) - 1;
		GameEntity entity = Game.current().get(clientModule().readUnsignedInt(m_dwEntityList + (crosshair * 0x10)));
		if (crosshair > -1 && entity != null && entity.isPlayer()) {
			target = entity.asPlayer();
		} else {
			target = null;
		}

		shotsFired = process().readUnsignedInt(address() + m_iShotsFired);
	}

	@Override
	public int processWeapon(long weaponAddress, int index, boolean active) {
		int weaponId = super.processWeapon(weaponAddress, index, active);
		if (active) {
			activeWeapon.setWeaponID(weaponId);
			activeWeapon.setCanReload(process().readBoolean(weaponAddress + m_bCanReload));
			activeWeapon.setClip1(process().readUnsignedInt(weaponAddress + m_iClip1));
			activeWeapon.setClip2(process().readUnsignedInt(weaponAddress + m_iClip2));
		}
		return weaponId;
	}
}