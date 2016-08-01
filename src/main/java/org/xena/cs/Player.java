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

public class Player extends GameEntity {

	@Getter
	protected int lifeState;

	@Getter
	protected int health;

	@Getter
	public long[][] weaponIds = new long[8][2];

	@Getter
	protected Weapon activeWeapon = new Weapon();

	@Getter
	protected int glowIndex;

	@Getter
	protected int armor;

	@Getter
	protected boolean gunGameImmunity;

	@Getter
	protected boolean hasBomb;

	@Override
	public void update() {
		super.update();
		hasBomb = false;

		for (int i = 0; i < weaponIds.length; i++) {
			long currentWeaponIndex = process().readUnsignedInt(address() + m_hMyWeapons + ((i - 1) * 0x04)) & 0xFFF;
			long weaponAddress = clientModule().readUnsignedInt(m_dwEntityList + (currentWeaponIndex - 1) * 0x10);

			if (weaponAddress > 0) {
				processWeapon(weaponAddress, i, false);
			}
		}
	}

	public int processWeapon(long weaponAddress, int index, boolean active) {
		int weaponId = process().readInt(weaponAddress + m_iItemDefinitionIndex);
		if (weaponId == Weapons.C4.getId()) {
			hasBomb = true;

		}
		weaponIds[index][0] = weaponId;
		weaponIds[index][1] = weaponAddress;
		return weaponId;
	}

}
