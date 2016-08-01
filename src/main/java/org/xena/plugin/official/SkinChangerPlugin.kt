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

package org.xena.plugin.official

import com.github.jonatino.offsets.Offsets.*
import org.xena.Indexer
import org.xena.Xena
import org.xena.cs.*
import org.xena.keylistener.NativeKeyUtils
import org.xena.logging.Logger
import org.xena.plugin.Plugin
import org.xena.plugin.PluginManifest
import java.awt.event.KeyEvent

@PluginManifest(name = "Skin Changer", description = "Skin changer plugin.")
class SkinChangerPlugin(logger: Logger, xena: Xena) : Plugin(logger, xena) {

    private val DEFAULT_SKIN_SEED = 0
    private val DEFAULT_STATTRAK = -1 // -1 for no StatTrak, 0+ for StatTrak amount
    private val DEFAULT_WEAR = 0.0001f // lower = less wear, higher = more wear
    private val DEFAULT_QUALITY = 1

    private var weapon: Weapons? = null
    private var weaponAddress: Long = 0

    public fun skins() {
        Weapons.AK47(490)
        Weapons.AUG(455)
        Weapons.AWP(344)
        Weapons.CZ75A(350)
        Weapons.DESERT_EAGLE(527)
        Weapons.FAMAS(194)
        Weapons.FIVE_SEVEN(427)
        Weapons.G3SG1(511)
        Weapons.GALIL(398)
        Weapons.GLOCK(353)
        Weapons.M249(496)
        Weapons.M4A1_SILENCER(548)
        Weapons.M4A4(309)
        Weapons.MAC10(433)
        Weapons.MAG7(431)
        Weapons.MP7(536)
        Weapons.MP9(262)
        Weapons.NEGEV(317)
        Weapons.NOVA(286)
        Weapons.P2000(389)
        Weapons.P250(551)
        Weapons.P90(156)
        Weapons.PP_BIZON(542)
        Weapons.R8_REVOLVER(595)
        Weapons.SAWED_OFF(256)
        Weapons.SCAR20(391)
        Weapons.SSG08(222)
        Weapons.SG556(287)
        Weapons.TEC9(520)
        Weapons.UMP45(556)
        Weapons.USP_SILENCER(504)
        Weapons.XM1014(393)
    }

    private operator fun Weapons.invoke(skinID: Int, skinSeed: Int = DEFAULT_SKIN_SEED, statTrak: Int = DEFAULT_STATTRAK, wear: Float = DEFAULT_WEAR, quality: Int = DEFAULT_QUALITY) {
        if (this == weapon) skin(skinID, skinSeed, statTrak, wear, quality)
    }

    override fun pulse(clientState: ClientState, me: Me, entities: Indexer<GameEntity>) {
        for (weaponId in me.weaponIds) {
            val weapon = Weapons.byID(weaponId)
            if (weapon != null && weapon.customSkin) {
                for (i in 0..4) {
                    process().writeInt(weaponAddress + m_iItemIDHigh, 1)// patch to make the skins stay
                    process().writeInt(weaponAddress + m_nFallbackPaintKit, weapon.skin)
                    process().writeInt(weaponAddress + m_nFallbackSeed, DEFAULT_SKIN_SEED)
                    process().writeInt(weaponAddress + m_nFallbackStatTrak, DEFAULT_STATTRAK)
                    process().writeInt(weaponAddress + m_iEntityQuality, DEFAULT_QUALITY)
                    process().writeFloat(weaponAddress + m_flFallbackWear, DEFAULT_WEAR)
                }
            }
        }
        if (NativeKeyUtils.isKeyDown(KeyEvent.VK_F1))
            engine().writeInt(Game.current().clientState().address() + m_dwForceFullUpdate, -1)
    }

    private fun skin(skinID: Int, skinSeed: Int, statTrak: Int, wear: Float, quality: Int) {
        process().writeInt(weaponAddress + m_nFallbackPaintKit, skinID)
        process().writeInt(weaponAddress + m_nFallbackSeed, skinSeed)
        process().writeInt(weaponAddress + m_nFallbackStatTrak, statTrak)
        process().writeInt(weaponAddress + m_iEntityQuality, quality)
        process().writeFloat(weaponAddress + m_flFallbackWear, wear)
    }

}
