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

package org.xena.offsets.netvars.impl;

import org.xena.offsets.OffsetManager;

/**
 * Created by Jonathan on 11/16/2015.
 */
public final class RecvTable {
	
	private int base;
	
	public RecvTable setBase(int base) {
		this.base = base;
		return this;
	}
	
	public int propForId(int id) {
		return OffsetManager.process().readInt(base) + (id * 0x3C);
	}
	
	public String tableName() {
		return OffsetManager.process().readString(OffsetManager.process().readInt(base + 0xC), 32);
	}
	
	public int propCount() {
		return OffsetManager.process().readInt(base + 0x4);
	}
	
	public boolean readable() {
		return OffsetManager.process().canRead(base, 0x10);
	}
	
	
}
