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
