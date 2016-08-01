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

import static com.github.jonatino.offsets.Offsets.m_flFlashMaxAlpha;

@PluginManifest(name = "No Flash", description = "Disables the flash bang effect on your screen.")
public final class NoFlashPlugin extends Plugin {

    public NoFlashPlugin(Logger logger, Xena xena) {
        super(logger, xena);
    }

    @Override
    public void pulse(ClientState clientState, Me me, Indexer<GameEntity> entities) {
        if (process().readFloat(me.address() + m_flFlashMaxAlpha) > 0.0f) {
            process().writeFloat(me.address() + m_flFlashMaxAlpha, 85.0f);
        } else if (process().readFloat(me.address() + m_flFlashMaxAlpha) == 0.0f) {
            process().writeFloat(me.address() + m_flFlashMaxAlpha, 255.0f);
        }
    }

}
