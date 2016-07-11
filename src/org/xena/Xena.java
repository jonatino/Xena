package org.xena;

import com.beaudoin.jmm.natives.win32.Kernel32;
import com.beaudoin.jmm.process.Module;
import com.beaudoin.jmm.process.NativeProcess;
import com.beaudoin.jmm.process.impl.win32.Win32Process;
import com.sun.jna.ptr.IntByReference;
import org.xena.cs.ClientState;
import org.xena.cs.Game;
import org.xena.cs.GameEntity;
import org.xena.cs.Player;
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

    private final PluginManager pluginManager;

    private final NativeProcess process;

    private final Module clientModule;

    private final Module engineModule;

    private boolean paused;

    private long lastCycle;

    private Overlay overlay;

    private GlobalKeyboard keylistener;

    public Xena(NativeProcess process, Module clientModule, Module engineModule, PluginManager pluginManager) {
        this.pluginManager = pluginManager;
        this.process = process;
        this.clientModule = clientModule;
        this.engineModule = engineModule;
    }

    void run(Logger logger, int cycleMS) throws InterruptedException {
        keylistener = GlobalKeyboard.register(this);

        // pluginManager.enable(new RadarPlugin(logger, process, clientModule, engineModule, taskManager));
        pluginManager.add(new GlowESPPlugin(logger, this));
        // pluginManager.add(new RCS(logger, this, taskManager));
        pluginManager.add(new ForceAimPlugin(logger, this));
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

                int myAddress = clientModule.readInt(m_dwLocalPlayer);
                if (myAddress <= 0) {
                    Thread.sleep(3000);
                    continue;
                }

                int myTeam = process.readInt(myAddress + m_iTeamNum);
                if (myTeam != 2 && myTeam != 3) {
                    Thread.sleep(3000);
                    continue;
                }

                int objectCount = clientModule.readInt(m_dwGlowObject + 4);
                if (objectCount <= 0) {
                    Thread.sleep(3000);
                    continue;
                }

                int myIndex = process.readInt(myAddress + m_dwIndex) - 1;
                if (myIndex < 0 || myIndex >= objectCount) {
                    Thread.sleep(3000);
                    continue;
                }

                int enginePointer = engineModule.readInt(m_dwClientState);
                if (enginePointer <= 0) {
                    Thread.sleep(3000);
                    continue;
                }

                int inGame = process.readInt(enginePointer + m_dwInGame);
                if (inGame != 6) {
                    Thread.sleep(3000);
                    continue;
                }

                if (myAddress <= 0 || myIndex < 0 || myIndex > 0x200 || myIndex > objectCount || objectCount <= 0) {
                    Thread.sleep(3000);
                    continue;
                }

                updateClientState(game.clientState());
                game.me().update();
                updateEntityList(game.entities());

                for (Plugin plugin : pluginManager) {
                    if (plugin.canPulse()) {
                        plugin.pulse(game.clientState(), game.me(), game.entities(), game.players().values());
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

    private void updateClientState(ClientState clientState) {
        int address = engineModule.readInt(m_dwClientState);
        if (address < 0) {
            throw new IllegalStateException("Could not find client state");
        }
        clientState.setAddress(address);
        clientState.setInGame(process.readInt(address + m_dwInGame));
        clientState.setMaxPlayer(process.readInt(address + m_dwMaxPlayer));
        clientState.setLocalPlayerIndex(process.readInt(address + m_dwLocalPlayerIndex));
    }

    private void updateEntityList(GameEntity[] entities) {
        int entityCount = clientModule.readInt(m_dwGlowObject + 4);

 /*       if (entities == null || entityCount != entities.length) {
            game.setEntities(new GameEntity[entityCount]);
        }*/

        for (int i = 0; i < entityCount; i++) {
            int entityAddress = clientModule.readInt(m_dwEntityList + (i * 16));
            if (entityAddress >= 0x200) {
                int playerTeam = process.readInt(entityAddress + m_iTeamNum);
                if (playerTeam == 2 || playerTeam == 3) {
                    int vt = process.readInt(entityAddress + 0x8);
                    if (vt <= 0) {
                        continue;
                    }
                    int fn = process.readInt(vt + 0x8);
                    if (fn <= 0) {
                        continue;
                    }
                    int cls = process.readInt(fn + 0x1);
                    if (cls <= 0) {
                        continue;
                    }
                    int classId = process.readInt(cls + 20);
                    if (classId == 35) {
                        Player player = game.players().get(entityAddress);
                        if (player == null || player.address() != entityAddress || player.getTeam() != playerTeam) {
                            game.players().put(entityAddress, player = new Player());
                        }
                        player.setAddress(entityAddress);
                        player.update();
                    }
                }
            }
        }
    }

    @Override
    public boolean onKeyPressed(NativeKeyEvent event) {
        if (overlay != null && overlay.isVisible()) {
            if (event.keyCode() == KeyEvent.VK_F9) {
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

    public Game getGame() {
        return this.game;
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public NativeProcess getProcess() {
        return this.process;
    }

    public Module getClientModule() {
        return this.clientModule;
    }

    public Module getEngineModule() {
        return this.engineModule;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public long getLastCycle() {
        return this.lastCycle;
    }

    public Overlay getOverlay() {
        return this.overlay;
    }

    public GlobalKeyboard getKeylistener() {
        return this.keylistener;
    }
}
