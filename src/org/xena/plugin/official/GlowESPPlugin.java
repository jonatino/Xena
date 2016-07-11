package org.xena.plugin.official;

import org.xena.Xena;
import org.xena.cs.ClientState;
import org.xena.cs.GameEntity;
import org.xena.cs.Me;
import org.xena.cs.Player;
import org.xena.logging.Logger;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;

import java.util.Collection;

import static org.abendigo.offsets.Offsets.m_dwGlowObject;

@PluginManifest(name = "Glow ESP", description = "Make players glow on your screen.")
public final class GlowESPPlugin extends Plugin {

    public GlowESPPlugin(Logger logger, Xena xena) {
        super(logger, xena);
    }

    @Override
    public void pulse(ClientState clientState, Me me, GameEntity[] entities, Collection<Player> players) {
        int pointerGlow = client().readInt(m_dwGlowObject);
        int glowObjectCount = client().readInt(m_dwGlowObject + 4);

        for (int i = 0; i < glowObjectCount; i++) {
            int glowObjectPointer = pointerGlow + (i * 56);
            int glowObjectEntityAddress = process().readInt(glowObjectPointer);
            if (glowObjectEntityAddress == 0) {
                continue;
            }
            for (Player p : players) {
                if (me.equals(p)) {
                    continue;
                }
                if (glowObjectEntityAddress == p.address()) {
                    float red = 0.8784314f, green = 0.6862745f, blue = 0.3372549f;
                    float opacity = 0.6F;
                    if (p.getTeam() == 3) {
                        red = 0.44705883f;
                        green = 0.60784316f;
                        blue = 0.8666667f;
                    }
                    if (p.getTeam() != me.getTeam() && me.getTarget() == p.address()) {
                        green = 215;
                    }
                    process().writeFloat(glowObjectPointer + 0x4, red);
                    process().writeFloat(glowObjectPointer + 0x8, green);
                    process().writeFloat(glowObjectPointer + 0xC, blue);
                    process().writeFloat(glowObjectPointer + 0x10, opacity);
                    process().writeBoolean(glowObjectPointer + 0x24, true);
                    process().writeBoolean(glowObjectPointer + 0x25, false);
                }
            }
        }
        sleep(64);
    }

}
