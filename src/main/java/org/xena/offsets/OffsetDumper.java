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

package org.xena.offsets;


import org.xena.offsets.netvars.NetVars;
import org.xena.offsets.offsets.ClientOffsets;

/**
 * Created by Jonathan on 12/22/2015.
 */
public final class OffsetDumper {
	
	public static void main(String... args) {
		System.setProperty("jna.nosys", "true");
		OffsetManager.initAll();
		
		NetVars.dump();
		ClientOffsets.dump();
	}
	
}