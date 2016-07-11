package org.xena.plugin.utils

import java.nio.ByteBuffer

/**
 * Created by Jonathan on 8/23/2016.
 */
val ByteBuffer.ushort: Int
	get() = short.toInt() and 0xffff
