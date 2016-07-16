package org.xena.plugin.official;

import org.xena.Indexer;
import org.xena.Xena;
import org.xena.cs.*;
import org.xena.logging.Logger;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;

import static org.abendigo.offsets.Offsets.m_dwGlowObject;

@PluginManifest(name = "Glow ESP", description = "Make entities glow on your screen.")
public final class GlowESPPlugin extends Plugin {

    private static final float[] TEAM_CT = {114.0f, 155.0f, 221.0f, 153.0f};
    private static final float[] TEAM_T = {224.0f, 175.0f, 86.0f, 153.0f};
    private static final float[] BOMB_CARRY = {255f, 0.0f, 0.0f, 200.0f};

    public GlowESPPlugin(Logger logger, Xena xena) {
        super(logger, xena);
    }

    @Override
    public void pulse(ClientState clientState, Me me, Indexer<GameEntity> entities) {
        long pointerGlow = client().readUnsignedInt(m_dwGlowObject);
        long glowObjectCount = client().readUnsignedInt(m_dwGlowObject + 4);

        for (int i = 0; i < glowObjectCount; i++) {
            long glowObjectPointer = pointerGlow + (i * 56);
            long glowEntityAddress = process().readUnsignedInt(glowObjectPointer);
            GameEntity entity = Game.current().get(glowEntityAddress);

            if (entity != null) {
                try {
                    Player player = entity.asPlayer();
                    float[] c = getColor(player);
                    for (int x = 0; x < 4; x++) {
                        process().writeFloat(glowObjectPointer + (x + 1) * 4, c[x] / 255.0f);
                    }
                    process().writeBoolean(glowObjectPointer + 0x24, true);
                    process().writeBoolean(glowObjectPointer + 0x25, false);
                } catch (Throwable ignored) {
                    ignored.printStackTrace();
                }
            }
        }
        sleep(128);
    }

    private float[] getColor(Player player) {
        if (player.getTeam() == 3) {
            return TEAM_CT;
        }
        if (player.isHasBomb()) {
            return BOMB_CARRY;
        }
        return TEAM_T;
    }

}
