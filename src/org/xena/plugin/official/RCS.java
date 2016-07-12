package org.xena.plugin.official;

import org.xena.Xena;
import org.xena.cs.ClientState;
import org.xena.cs.GameEntity;
import org.xena.cs.Me;
import org.xena.logging.Logger;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;
import org.xena.plugin.utils.AngleUtils;

import java.util.Collection;

@PluginManifest(name = "RCS", description = "Recoil control system.")
public final class RCS extends Plugin {

    private final AngleUtils aimHelper;

    public RCS(Logger logger, Xena xena) {
        super(logger, xena);
        aimHelper = new AngleUtils(this, 40.5F, 1.7F, 2.5F, 1.7F, 2.5F);
    }

    private static final float[] oldAng = new float[3];
    private static final float[] punch = new float[3];
    private static final float[] angle = new float[3];
    private static final float[] viewAng = new float[3];

    @Override
    public void pulse(ClientState clientState, Me me, Collection<GameEntity> entities) {
        int shotsFired = me.getShotsFired();
        if (shotsFired > 1) {
            //aimHelper.getAngle(viewAng);
            //aimHelper.getPunch(me, punch);


            viewAng[0] = viewAng[0] + oldAng[0];
            viewAng[1] = viewAng[1] + oldAng[1];


            angle[0] = viewAng[0] - punch[0] * 2F;
            angle[1] = viewAng[1] - punch[1] * 2F;

            aimHelper.setAngles(angle);

            oldAng[0] = punch[0] * 2F;
            oldAng[1] = punch[1] * 2F;
        } else {
            oldAng[0] = 0;
            oldAng[1] = 0;
        }
    }

}
