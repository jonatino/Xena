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

package org.xena.offsets.misc;


import com.github.jonatino.misc.Cacheable;
import com.github.jonatino.process.Module;

import java.util.Arrays;

public final class PatternScanner {
	
	public static final int READ = 1, SUBTRACT = 2;
	
	public static int byPattern(Module module, int pattern_offset, int address_offset, int flags, String className) {
		return byPattern(module, pattern_offset, address_offset, flags, className.getBytes());
	}
	
	public static int byPattern(Module module, int pattern_offset, int address_offset, int flags, int value) {
		return byPattern(module, pattern_offset, address_offset, flags, toByteArray(value));
	}
	
	public static int byPattern(Module module, int pattern_offset, int address_offset, int flags, int... values) {
		return byPattern(module, pattern_offset, address_offset, flags, toByteArray(values));
	}
	
	public static int byPattern(Module module, int pattern_offset, int address_offset, int flags, byte... values) {
		long off = module.size() - values.length;
		for (int i = 0; i < off; i++) {
			if (checkMask(module, i, values)) {
				i += module.address() + pattern_offset;
				if ((flags & READ) == READ) {
					i = module.process().readInt(i);
				}
				if ((flags & SUBTRACT) == SUBTRACT) {
					i -= module.address();
				}
				return i + address_offset;
			}
		}
		throw new IllegalStateException("Can not find offset inside of " + module.name() + " with pattern " + Arrays.toString(values));
	}
	
	private static boolean checkMask(Module module, int offset, byte[] pMask) {
		for (int i = 0; i < pMask.length; i++) {
			if (pMask[i] != 0x0 && (pMask[i] != module.data().getByte(offset + i))) {
				return false;
			}
		}
		return true;
	}
	
	private static byte[] toByteArray(int value) {
		return new byte[]{(byte) value, (byte) (value >> 8), (byte) (value >> 16), (byte) (value >> 24)};
	}
	
	private static byte[] toByteArray(int... value) {
		byte[] byteVals = Cacheable.array(value.length);
		for (int i = 0; i < value.length; i++) {
			byteVals[i] = (byte) value[i];
		}
		return byteVals;
	}
	
}
