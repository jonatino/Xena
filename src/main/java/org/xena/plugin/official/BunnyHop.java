
package org.xena.plugin.unofficial;

import org.xena.Indexer;
import org.xena.cs.ClientState;
import org.xena.cs.GameEntity;
import org.xena.cs.Me;
import org.xena.keylistener.NativeKeyUtils;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;

import java.awt.event.KeyEvent;

import static org.xena.offsets.OffsetManager.clientModule;
import static org.xena.offsets.OffsetManager.process;
import static org.xena.offsets.offsets.NetVarOffsets.fFlags;

@PluginManifest(name = "No Hand", description = "No hand.")
public class  BunnyHop extends Plugin {

    public static int FL_ONGROUND = (1 << 0); // At rest / on the ground

    @Override
    public void pulse(ClientState clientState, Me me, Indexer<GameEntity> entities)
    {
        long m_fFlags = process().readLong(me.address() + fFlags);
        int FL_ONGROUND = 257; // At rest / on the ground
        int jump = 0x4F1AAF4;


        if(NativeKeyUtils.isKeyDown(KeyEvent.VK_SPACE))
        {
            if (false && (m_fFlags & FL_ONGROUND) > 0)
                process().writeInt(jump, 6);
            else if ((m_fFlags & FL_ONGROUND) > 0) {
                try {
                    Thread.sleep(5 + (int) (Math.random() * 15));
                    clientModule().writeInt(jump, 5);
                    Thread.sleep(5 + (int) (Math.random() * 15 ));
                    clientModule().writeInt(jump, 4);


                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            }
        }

    }
}
