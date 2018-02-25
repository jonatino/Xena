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

package org.xena.cs


/**
 * Created by Jonathan on 7/24/2016.
 */
enum class Weapons(val id: Int, val skin: Int = -1, val customSkin: Boolean = (skin != -1)) {
	
	DESERT_EAGLE(1, CRIMSON_WEB),
	FIVE_SEVEN(3, MONKEY_BUSINESS),
	GLOCK(4, FADE),
	CZ75A(63, CRIMSON_WEB),
	DUAL_BERRETA(2, URBAN_SHOCK),
	P2000(32, IMPERIAL_DRAGON),
	P250(36, ASIIMOV_2),
	R8_REVOLVER(6, REBOOT),
	TEC9(30, AVALANCHE),
	USP_SILENCER(61, KILL_CONFIRMED),
	
	AK47(7, FRONTSIDE_MISTY),
	AUG(8, AKIHABARA_ACCEPT),
	AWP(9, DRAGON_LORE),
	FAMAS(10, AFTERIMAGE),
	M4A1_SILENCER(60, MECHA_INDUSTRIES),
	M4A4(16, HOWL),
	SSG08(40, DETOUR),
	
	
	PP_BIZON(26, JUDGEMENT_OF_ANUBIS),
	P90(19, DEATH_BY_KITTY),
	UMP45(24, PRIMAL_SABER),
	
	G3SG1(11),
	GALIL(13),
	M249(14),
	MAC10(17),
	XM1014(25),
	MAG7(27),
	NEGEV(28),
	SAWED_OFF(29),
	ZEUS_X27(31),
	MP7(33),
	MP9(34),
	NOVA(35),
	SCAR20(38),
	SG556(39),
	KNIFE(42),
	FLASH_GRENADE(43),
	EXPLOSIVE_GRENADE(44),
	SMOKE_GRENADE(45),
	MOLOTOV(46),
	DECOY_GRENADE(47),
	INCENDIARY_GRENADE(48),
	C4(49),
	KNIFE_T(59, MARBLE_FADE),
	KNIFE_CT(41, MARBLE_FADE),
	KNIFE_BAYONET(500),
	KNIFE_FLIP(505),
	KNIFE_GUT(506),
	KNIFE_KARAMBIT(507, MARBLE_FADE),
	KNIFE_M9_BAYONET(508, MARBLE_FADE),
	KNIFE_TACTICAL(509),
	KNIFE_TALCHION(512),
	KNIFE_BOWIE(514),
	KNIFE_BUTTERFLY(515),
	KNIFE_PUSH(516);
	
	companion object {
		
		private val cachedValues = values()
		
		@JvmStatic
		fun byID(id: Int) = cachedValues.firstOrNull { it.id == id }
		
	}

}