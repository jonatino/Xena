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

package org.xena.offsets.tools;

import org.xena.cs.EntityType;
import org.xena.offsets.OffsetManager;
import org.xena.offsets.misc.PatternScanner;
import org.xena.offsets.netvars.impl.ClientClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Jonathan on 1/1/2016.
 */
public final class ClassIdDumper {
	
	public static void main(String[] args) {
		System.setProperty("jna.nosys", "true");
		
		int firstclass = PatternScanner.byPattern(OffsetManager.clientModule(), 0, 0, 0, "DT_TEWorldDecal");
		firstclass = PatternScanner.byPattern(OffsetManager.clientModule(), 0x2B, 0, PatternScanner.READ, firstclass);
		
		List<ClientClassInfo> text = new ArrayList<>();
		ClientClass clientClass = new ClientClass();
		for (clientClass.setBase(firstclass); clientClass.readable(); clientClass.setBase(clientClass.next())) {
			text.add(new ClientClassInfo(clientClass.className(), clientClass.classId()));
		}
		text.sort(Comparator.comparingInt(o -> o.id));
		List<String> lines = new ArrayList<>();
		text.forEach(k -> lines.add(k.toString()));
		try {
			Files.write(Paths.get("ClassIds.txt"), lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static class ClientClassInfo {
		
		private String name;
		private int id;
		
		public ClientClassInfo(String name, int id) {
			this.name = name;
			this.id = id;
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(name).append("(").append(id);
			for (EntityType e : EntityType.values()) {
				if (e.getWeapon() && name.toLowerCase().startsWith(e.name().toLowerCase())) {
					b.append(", weapon = true");
					break;
				}
				if (e.getGrenade() && name.toLowerCase().startsWith(e.name().toLowerCase())) {
					b.append(", grenade = true");
					break;
				}
			}
			b.append("),");
			return b.toString();
		}
	}
}
