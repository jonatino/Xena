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
public final class ClientClass {
	
	private int base;
	
	public ClientClass setBase(int base) {
		this.base = base;
		return this;
	}
	
	public int classId() {
		return OffsetManager.process().readInt(base + 0x14);
	}
	
	public String className() {
		return OffsetManager.process().readString(OffsetManager.process().readInt(base + 0x8), 64);
	}
	
	public int next() {
		return OffsetManager.process().readInt(base + 0x10);
	}
	
	public int table() {
		return OffsetManager.process().readInt(base + 0xC);
	}
	
	public boolean readable() {
		return OffsetManager.process().canRead(base, 0x28);
	}
	
}
