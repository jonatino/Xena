package org.xena;

import org.xena.keylistener.NativeKeyUtils;

import java.awt.event.KeyEvent;

/**
 * Created by Jonathan on 2016-11-18.
 */
public final class Settings {
	
	/**
	 * Aim assist settings
	 */
	public static final float AIM_ASSIST_STRENGTH = 30f;
	
	
	/**
	 * Force aim settings
	 */
	public static final float FORCE_AIM_STRENGTH = 40f;
	public static final int FORCE_AIM_TOGGLE = NativeKeyUtils.LEFT_ALT;
	
	
	/**
	 * Spin Bot assist settings
	 */
	public static final float SPIN_BOT_STRENGTH = 40f;
	public static final int SPIN_BOT_TOGGLE = NativeKeyUtils.LEFT_CTRL;
	
	
	/**
	 * GUI settings
	 */
	public static final int HIDE_GUI_KEY = KeyEvent.VK_F9;
	
	
}
