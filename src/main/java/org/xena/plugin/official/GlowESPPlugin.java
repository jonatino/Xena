package org.xena.plugin.official;

import org.xena.Xena;
import org.xena.cs.*;
import org.xena.logging.Logger;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;

import java.util.Collection;

@PluginManifest(name = "Glow ESP", description = "Make entities glow on your screen.")
public final class GlowESPPlugin extends Plugin {

    public GlowESPPlugin(Logger logger, Xena xena) {
        super(logger, xena);
    }

    @Override
    public void pulse(ClientState clientState, Me me, Collection<GameEntity> entities) {
        for (GameEntity entity : entities) {
            try {
                float red = 0.8784314f, green = 0.6862745f, blue = 0.3372549f;
                float opacity = 0.6F;
                if (entity.type() == EntityType.CCSPlayer && entity.isPlayer()) {
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
                } else if (entity.type() == EntityType.CPlantedC4 || entity.type() == EntityType.CC4) {
                    red = 1.0f;
                    green = 0.0f;
                    blue = 0.0f;
                } else if (entity.type() == EntityType.CChicken) {
                    red = 0.0f;
                    green = 1.0f;
                    blue = 0.0f;
                } else {
                    continue;
                }
                process().writeFloat(entity.getGlowObjectPointer() + 0x4, red);
                process().writeFloat(entity.getGlowObjectPointer() + 0x8, green);
                process().writeFloat(entity.getGlowObjectPointer() + 0xC, blue);
                process().writeFloat(entity.getGlowObjectPointer() + 0x10, opacity);
                process().writeBoolean(entity.getGlowObjectPointer() + 0x24, true);
                process().writeBoolean(entity.getGlowObjectPointer() + 0x25, false);
            } catch (Throwable ignored) {
                ignored.printStackTrace();
            }
        }
        sleep(128);
    }

}
