package org.xena.plugin;

import com.beaudoin.jmm.process.Module;
import com.beaudoin.jmm.process.NativeProcess;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.xena.Xena;
import org.xena.cs.ClientState;
import org.xena.cs.GameEntity;
import org.xena.cs.Me;
import org.xena.keylistener.GlobalKeyboard;
import org.xena.keylistener.NativeKeyCombination;
import org.xena.logging.Logger;

import java.awt.event.KeyEvent;
import java.util.Collection;

@AllArgsConstructor
public abstract class Plugin {

    private static int pluginUid;

    private final int uid;
    public final Xena xena;
    private final Logger logger;
    private final NativeProcess process;
    private final Module client;
    private final Module engine;

    private long sleep;

    @Getter
    private boolean enabled = true;

    public Plugin(Logger logger, Xena xena) {
        this.uid = pluginUid++;
        this.logger = logger;
        this.xena = xena;
        this.process = xena.getProcess();
        this.client = xena.getClientModule();
        this.engine = xena.getEngineModule();

        xena.getKeylistener().registerHotkey(new NativeKeyCombination((e) -> {
            toggle();
            xena.getOverlay().repaint();
            e.consume();
        }, GlobalKeyboard.ALT, KeyEvent.VK_NUMPAD0 + uid, KeyEvent.VK_0 + uid));
    }

    public void toggle() {
        enabled = !enabled;
    }

    public boolean canPulse() {
        return enabled && System.currentTimeMillis() >= sleep;
    }

    protected void sleep(long ms) {
        sleep = System.currentTimeMillis() + ms;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public abstract void pulse(ClientState clientState, Me me, Collection<GameEntity> entities);

    protected final Logger logger() {
        return logger;
    }

    public final NativeProcess process() {
        return process;
    }

    public final Module client() {
        return client;
    }

    public final Module engine() {
        return engine;
    }

    public int uid() {
        return uid;
    }

    @Override
    public String toString() {
        return uid + ": " + getClass().getSimpleName();
    }

}
