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

package org.xena.plugin;

import org.xena.Indexer;

import java.util.Iterator;

public final class PluginManager implements Iterable<Plugin> {
	
	private final Indexer<Plugin> indexer = new Indexer<>(10);
	
	public void add(Plugin plugin) {
		indexer.add(plugin);
	}
	
	public int size() {
		return indexer.size();
	}
	
	public Plugin get(int i) {
		return indexer.get(i);
	}
	
	@Override
	public Iterator<Plugin> iterator() {
		return indexer.iterator();
	}
	
}
