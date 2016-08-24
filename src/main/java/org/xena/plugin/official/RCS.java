/*
 *    Copyright 2016 Jonathan Beaudoin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xena.plugin.official;

import org.xena.Indexer;
import org.xena.Xena;
import org.xena.cs.ClientState;
import org.xena.cs.GameEntity;
import org.xena.cs.Me;
import org.xena.logging.Logger;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;
import org.xena.plugin.utils.AngleUtils;
import org.xena.plugin.utils.Vector;

@PluginManifest(name = "RCS", description = "Recoil control system.")
public final class RCS extends Plugin {

    private final AngleUtils aimHelper;

    public RCS(Logger logger, Xena xena) {
        super(logger, xena);
        aimHelper = new AngleUtils(this, 40.5F, 1.7F, 2.5F, 1.7F, 2.5F);
    }

    private static final Vector oldAng = new Vector();
    private static final Vector punch = new Vector();
    private static final Vector angle = new Vector();
    private static final Vector viewAng = new Vector();

    @Override
    public void pulse(ClientState clientState, Me me, Indexer<GameEntity> entities) {
        long shotsFired = me.getShotsFired();
        if (shotsFired > 1) {
            //aimHelper.getAngle(viewAng);
            //aimHelper.getPunch(me, punch);


            viewAng.x = viewAng.x + oldAng.x;
            viewAng.y = viewAng.y + oldAng.y;


            angle.x = viewAng.x - punch.x * 2F;
            angle.y = viewAng.y - punch.y * 2F;

            aimHelper.setAngles(angle);

            oldAng.x = punch.x * 2F;
            oldAng.y = punch.y * 2F;
        } else {
            oldAng.x = 0;
            oldAng.y = 0;
        }
    }

}
