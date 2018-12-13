package org.xena.utils;

import org.xena.Settings;

public class Logging {
	
	public static void debug(Object o) {
		if (Settings.DEBUG)
			System.out.println(o);
	}
}
