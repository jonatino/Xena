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

package org.xena.utils;

import com.github.jonatino.misc.MemoryBuffer;

import java.awt.*;

import static org.xena.offsets.OffsetManager.clientModule;
import static org.xena.offsets.offsets.ClientOffsets.m_dwViewMatrix;

public final class Utils {
	
	public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	
	private static boolean screenTransform(float[] from, float[] to) {
		float[][] flMatrix = new float[4][4];
		
		MemoryBuffer buffer = clientModule().read(m_dwViewMatrix, 64);
		for (int row = 0; row < 4; row++) {
			for (int c = 0; c < 4; c++) {
				flMatrix[row][c] = buffer.getFloat();
			}
		}
		
		to[0] = flMatrix[0][0] * from[0] + flMatrix[0][1] * from[1] + flMatrix[0][2] * from[2] + flMatrix[0][3];
		to[1] = flMatrix[1][0] * from[0] + flMatrix[1][1] * from[1] + flMatrix[1][2] * from[2] + flMatrix[1][3];
		float w = flMatrix[3][0] * from[0] + flMatrix[3][1] * from[1] + flMatrix[3][2] * from[2] + flMatrix[3][3];
		
		if (w < 0.001f) {
			to[0] *= 100000;
			to[1] *= 100000;
			return true;
		}
		
		float invw = 1.0f / w;
		to[0] *= invw;
		to[1] *= invw;
		
		return false;
	}
	
	public static float[] worldToScreen(float[] from, float[] to) {
		if (!screenTransform(from, to)) {
			int iScreenWidth = SCREEN_SIZE.width;
			int iScreenHeight = SCREEN_SIZE.height;
			
			to[0] = (iScreenWidth / 2.0f) + (to[0] * iScreenWidth) / 2;
			to[1] = (iScreenHeight / 2.0f) - (to[1] * iScreenHeight) / 2;
			
			return to;
		}
		return to;
	}
	
	public static int[] from(int min, int max) {
		int[] keys = new int[(max - min) + 1];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = min++;
		}
		return keys;
	}
	
}