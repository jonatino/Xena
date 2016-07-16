package org.xena;

import com.beaudoin.jmm.natives.win32.Kernel32;
import com.beaudoin.jmm.process.Module;
import com.beaudoin.jmm.process.NativeProcess;
import com.beaudoin.jmm.process.impl.win32.Win32Process;
import com.sun.jna.ptr.IntByReference;
import lombok.Getter;
import org.xena.cs.*;
import org.xena.gui.Overlay;
import org.xena.keylistener.GlobalKeyboard;
import org.xena.keylistener.NativeKeyEvent;
import org.xena.keylistener.NativeKeyListener;
import org.xena.logging.Logger;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManager;
import org.xena.plugin.official.AimAssistPlugin;
import org.xena.plugin.official.ForceAimPlugin;
import org.xena.plugin.official.GlowESPPlugin;

import java.awt.event.KeyEvent;

import static java.lang.System.currentTimeMillis;
import static org.abendigo.offsets.Offsets.*;

public final class Xena implements NativeKeyListener {

    private final Game game = Game.current();

    public static final int CYCLE_TIME = 8;

    @Getter
    private final PluginManager pluginManager;

    @Getter
    private final NativeProcess process;

    @Getter
    private final Module clientModule;

    @Getter
    private final Module engineModule;

    @Getter
    private boolean paused;

    @Getter
    private long lastCycle;

    @Getter
    private Overlay overlay;

    @Getter
    private GlobalKeyboard keylistener;

    public Xena(NativeProcess process, Module clientModule, Module engineModule, PluginManager pluginManager) {
        this.pluginManager = pluginManager;
        this.process = process;
        this.clientModule = clientModule;
        this.engineModule = engineModule;
    }

    void run(Logger logger, int cycleMS) throws InterruptedException {
        keylistener = GlobalKeyboard.register(this);

        //pluginManager.enable(new RadarPlugin(logger, process, clientModule, engineModule, taskManager));
        pluginManager.add(new GlowESPPlugin(logger, this));
        //pluginManager.add(new RCS(logger, this, taskManager));
        pluginManager.add(new ForceAimPlugin(logger, this));
        //pluginManager.add(new SpinBotPlugin(logger, this));
        //pluginManager.add(new NoFlashPlugin(logger, this, taskManager));
        pluginManager.add(new AimAssistPlugin(logger, this));

        overlay = Overlay.open(this);

        logger.info("We're all set. Welcome to the new Xena platform!");
        logger.info("Use numpad or ALT+nums to toggle corresponding plugins.");

        IntByReference ref = new IntByReference();
        while (!Thread.interrupted()) {
            try {
                long stamp = currentTimeMillis();

                if (Kernel32.GetExitCodeProcess(((Win32Process) process).pointer(), ref)) {
                    if (ref.getValue() != 259) {
                        Thread.currentThread().interrupt();
                    }
                    ref.setValue(0);
                }

                checkGameStatus();

                updateClientState(game.clientState());
                updateEntityList();

                for (Plugin plugin : pluginManager) {
                    if (plugin.canPulse()) {
                        plugin.pulse(game.clientState(), game.me(), game.entities().values());
                    }
                }

                lastCycle = currentTimeMillis() - stamp;
                long sleep = cycleMS - lastCycle;
                if (sleep > 0) {
                    Thread.sleep(sleep);
                }
            } catch (Throwable t) {
                t.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void checkGameStatus() throws InterruptedException {
        while (true) {
            long myAddress = clientModule.readUnsignedInt(m_dwLocalPlayer);
            if (myAddress <= 0) {
                Thread.sleep(3000);
                continue;
            }

            long myTeam = process.readUnsignedInt(myAddress + m_iTeamNum);
            if (myTeam != 2 && myTeam != 3) {
                Thread.sleep(3000);
                continue;
            }

            long objectCount = clientModule.readUnsignedInt(m_dwGlowObject + 4);
            if (objectCount <= 0) {
                Thread.sleep(3000);
                continue;
            }

            long myIndex = process.readUnsignedInt(myAddress + m_dwIndex) - 1;
            if (myIndex < 0 || myIndex >= objectCount) {
                Thread.sleep(3000);
                continue;
            }

            long enginePointer = engineModule.readUnsignedInt(m_dwClientState);
            if (enginePointer <= 0) {
                Thread.sleep(3000);
                continue;
            }

            long inGame = process.readUnsignedInt(enginePointer + m_dwInGame);
            if (inGame != 6) {
                Thread.sleep(3000);
                continue;
            }

            if (myAddress <= 0 || myIndex < 0 || myIndex > 0x200 || myIndex > objectCount || objectCount <= 0) {
                Thread.sleep(3000);
                continue;
            }
            break;
        }
    }

    private void updateClientState(ClientState clientState) {
        long address = engineModule.readUnsignedInt(m_dwClientState);
        if (address < 0) {
            throw new IllegalStateException("Could not find client state");
        }
        clientState.setAddress(address);
        clientState.setInGame(process.readUnsignedInt(address + m_dwInGame));
        clientState.setMaxPlayer(process.readUnsignedInt(address + m_dwMaxPlayer));
        clientState.setLocalPlayerIndex(process.readUnsignedInt(address + m_dwLocalPlayerIndex));
    }

    private void updateEntityList() {
        long pointerGlow = clientModule.readUnsignedInt(m_dwGlowObject);
        long entityCount = clientModule.readUnsignedInt(m_dwGlowObject + 4);

        long myAddress = clientModule.readUnsignedInt(m_dwLocalPlayer);

        for (int i = 0; i < entityCount; i++) {
            long glowObjectPointer = pointerGlow + (i * 56);
            long entityAddress = process.readUnsignedInt(glowObjectPointer);
            if (entityAddress < 0x200) {
                continue;
            }
            EntityType type = getEntityType(entityAddress);
            if (type == null) {
                continue;
            }
            long team = process.readUnsignedInt(entityAddress + m_iTeamNum);

            GameEntity entity = game.entities().get(entityAddress);
            if (entity == null || (type == EntityType.CCSPlayer && entity.getTeam() != team)) {
                if (entity == null && myAddress == entityAddress) {
                    entity = Game.current().me();
                } else if (entity == null && type == EntityType.CCSPlayer) {
                    entity = new Player();
                } else if (entity == null) {
                    entity = new GameEntity();
                }
                game.entities().put(entityAddress, entity);
            }
            entity.setAddress(entityAddress);
            entity.setClassId(type.getId());
            entity.setGlowObjectPointer(glowObjectPointer);
            entity.update();
        }
    }

    public EntityType getEntityType(long address) {
        try {
            long vt = process.readUnsignedInt(address + 0x8);
            if (vt <= 0) {
                return null;
            }
            long fn = process.readUnsignedInt(vt + 0x8);
            if (fn <= 0) {
                return null;
            }
            long cls = process.readUnsignedInt(fn + 0x1);
            if (cls <= 0) {
                return null;
            }
            long classId = process.readUnsignedInt(cls + 20);
            return EntityType.byId(classId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean onKeyPressed(NativeKeyEvent event) {
        if (overlay != null && overlay.isVisible()) {
            if (event.keyCode() == KeyEvent.VK_F9 && !event.hasModifiers()) {
                overlay.minimize();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onKeyReleased(NativeKeyEvent event) {
        return false;
    }

}
