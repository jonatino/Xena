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

import com.github.jonatino.process.Module;
import com.github.jonatino.process.Process;
import com.github.jonatino.process.Processes;
import com.sun.jna.Platform;
import org.xena.offsets.netvars.NetVars;
import org.xena.offsets.offsets.Offsets;

/**
 * Created by Jonathan on 12/22/2015.
 */
public final class OffsetManager {
	
	private static Process process;
	private static Module clientModule, engineModule;
	
	static {
		StringBuilder procBaseName = new StringBuilder("csgo");
		StringBuilder clientBaseName = new StringBuilder("client");
		StringBuilder engineBaseName = new StringBuilder("engine");
		
		if (Platform.isWindows()) {
			procBaseName.append(".exe");
			clientBaseName.append(".dll");
			engineBaseName.append(".dll");
		} else if (Platform.isLinux()) {
			procBaseName.append("_linux");
			clientBaseName.append("_client.so");
			engineBaseName.append("_client.so");
		} else if (Platform.isMac()) {
			procBaseName.append("_osx");
			clientBaseName.append(".dylib");
			engineBaseName.append(".dylib");
		} else {
			throw new RuntimeException("Unsupported operating system type!");
		}
		
		String processName = procBaseName.toString();
		String clientName = clientBaseName.toString();
		String engineName = engineBaseName.toString();
		
		waitUntilFound("process", () -> (process = Processes.byName(processName)) != null);
		waitUntilFound("client module", () -> (clientModule = process.findModule(clientName)) != null);
		waitUntilFound("engine module", () -> (engineModule = process.findModule(engineName)) != null);
	}
	
	public static void initAll() {
		loadNetVars();
		loadOffsets();
	}
	
	public static void loadNetVars() {
		NetVars.load();
	}
	
	public static void loadOffsets() {
		Offsets.load();
	}
	
	public static Process process() {
		return process;
	}
	
	public static Module clientModule() {
		return clientModule;
	}
	
	public static Module engineModule() {
		return engineModule;
	}
	
	private static void waitUntilFound(String message, Clause clause) {
		System.out.print("Looking for " + message + ". Please wait.");
		while (!clause.get()) try {
			Thread.sleep(3000);
			System.out.print(".");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("\nFound " + message + "!");
	}
	
	@FunctionalInterface
	private interface Clause {
		boolean get();
	}
	
}
