package org.xena.plugin.official;

import org.xena.Indexer;
import org.xena.Settings;
import org.xena.cs.ClientState;
import org.xena.cs.GameEntity;
import org.xena.cs.Me;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;

import static org.xena.offsets.OffsetManager.process;
import static org.xena.offsets.offsets.NetVarOffsets.nModelIndex;

@PluginManifest(name = "No Hand", description = "No hand.")
public final class NoHandAndFOV extends Plugin {

    @Override
    public void pulse(ClientState clientState, Me me, Indexer<GameEntity> entities) {
        process().writeInt(me.address() + 0x31D4, (int) 130);
        process().writeInt(me.address() + nModelIndex, 20);
    }
}
