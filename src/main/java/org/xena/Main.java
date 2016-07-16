package org.xena;

import org.abendigo.OffsetManager;
import org.xena.logging.Logger;
import org.xena.logging.Logging;
import org.xena.plugin.PluginManager;

import java.lang.management.ManagementFactory;

import static org.abendigo.OffsetManager.*;

public final class Main {

    private static final String logo = "__  __                \n" + "\\ \\/ /___ _ __   __ _ \n" + " \\  // _ \\ '_ \\ / _` |\n" + " /  \\  __/ | | | (_| |\n" + "/_/\\_\\___|_| |_|\\__,_|\n" + "A free, open-source CS:GO cheating platform\n";

    private static final Logger logger = Logging.logger(Logging.standardOutputter());

    public static void main(String... args) throws InterruptedException {
        System.out.println(ManagementFactory.getRuntimeMXBean().getName());
        System.out.println(logo);

        OffsetManager.initAll();

        while (!Thread.interrupted()) {
            Xena xena = new Xena(process(), clientModule(), engineModule(), new PluginManager());
            xena.run(logger, Xena.CYCLE_TIME);
        }
    }

    private Main() {
    }

}
