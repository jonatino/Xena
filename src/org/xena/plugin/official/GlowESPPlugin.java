package org.xena.plugin.official;

import org.xena.Xena;
import org.xena.cs.*;
import org.xena.logging.Logger;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;

import java.util.Collection;

import static org.abendigo.offsets.Offsets.m_dwGlowObject;

@PluginManifest(name = "Glow ESP", description = "Make entities glow on your screen.")
public final class GlowESPPlugin extends Plugin {

    public GlowESPPlugin(Logger logger, Xena xena) {
        super(logger, xena);
    }

    @Override
    public void pulse(ClientState clientState, Me me, Collection<GameEntity> players) {
        int pointerGlow = client().readInt(m_dwGlowObject);
        int glowObjectCount = client().readInt(m_dwGlowObject + 4);

        for (int i = 0; i < glowObjectCount; i++) {
            try {
                int glowObjectPointer = pointerGlow + (i * 56);
                int glowObjectEntityAddress = process().readInt(glowObjectPointer);
                if (glowObjectEntityAddress < 0x200) {
                    continue;
                }

                GameEntity entity = Game.current().entities().get(glowObjectEntityAddress);
                if (entity != null) {
                    float red = 0.8784314f, green = 0.6862745f, blue = 0.3372549f;
                    float opacity = 0.6F;
                    if (entity.getClassId() == 35) {
                        Player player = entity.asPlayer();
                        if (player.getTeam() == 3) {
                            red = 0.44705883f;
                            green = 0.60784316f;
                            blue = 0.8666667f;
                        }
                        if (player.getTeam() != me.getTeam() && me.getTarget() == player) {
                            green = .8f;
                        }
                        if (player.isHasBomb()) {
                            red = 1.0f;
                            green = 0.0f;
                            blue = 0.0f;
                        }
                    } else if (entity.getClassId() == 105 || entity.getClassId() == 29) {
                        red = 1.0f;
                        green = 0.0f;
                        blue = 0.0f;
                    } else if (entity.getClassId() == 31) {
                        red = 0.0f;
                        green = 1.0f;
                        blue = 0.0f;
                    } else {
                        continue;
                    }
                    process().writeFloat(glowObjectPointer + 0x4, red);
                    process().writeFloat(glowObjectPointer + 0x8, green);
                    process().writeFloat(glowObjectPointer + 0xC, blue);
                    process().writeFloat(glowObjectPointer + 0x10, opacity);
                    process().writeBoolean(glowObjectPointer + 0x24, true);
                    process().writeBoolean(glowObjectPointer + 0x25, false);
                }
            } catch (Throwable ignored) {
            }
        }
        sleep(64);
    }

}
