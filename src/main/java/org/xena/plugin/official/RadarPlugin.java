package org.xena.plugin.official;

import org.xena.Xena;
import org.xena.cs.ClientState;
import org.xena.cs.GameEntity;
import org.xena.cs.Me;
import org.xena.logging.Logger;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;

import java.util.Collection;

import static org.abendigo.offsets.Offsets.m_bSpotted;

@PluginManifest(name = "Radar", description = "Pinpoints enemies on the minimap.")
public final class RadarPlugin extends Plugin {

    public RadarPlugin(Logger logger, Xena xena) {
        super(logger, xena);
    }

    @Override
    public void pulse(ClientState clientState, Me me, Collection<GameEntity> entities) {
        for (GameEntity entity : entities) {
            if (entity != null) {
                process().writeBoolean(entity.address() + m_bSpotted, true);
            }
        }
    }

}
