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

package org.xena.cs

/**
 * Created by Jonathan on 7/22/2016.
 */
enum class Bones(val id: Int, val percentage: Double) {
	
	HEAD(8, 0.75),
	NECK(7, 0.45),
	UPPER_CHEST(4, 0.30),
	LOWER_CHEST(3, -0.00),
	LEGS(2, -1.0),
	FEET(1, -1.0);
	
	
	companion object {
		
		@JvmStatic private val cachedValues = values().sortedByDescending { it.percentage }
		
		@JvmStatic
		fun roll(): Bones {
			for (bone in cachedValues) {
				if (bone.percentage < Math.random()) {
					return bone
				}
			}
			return HEAD
		}
		
	}
	
}