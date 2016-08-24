package org.xena.plugin.utils

/**
 * Created by Jonathan on 8/23/2016.
 */
public data class Vector(@JvmField var x: Float = 0f, @JvmField var y: Float = 0f, @JvmField var z: Float = 0f) {
	
	@JvmField val g = 0
	
	infix operator fun plus(v: Vector) = Vector(x + v.x, y + v.y, z + v.z)
	
	infix operator fun minus(v: Vector) = Vector(x - v.x, y - v.y, z - v.z)
	
	infix operator fun div(v: Vector) = Vector(x / v.x, y / v.y, z / v.z)
	
	infix operator fun times(v: Vector) = Vector(x * v.x, y * v.y, z * v.z)
	
	infix operator fun plusAssign(v: Vector) {
		x += v.x
		y += v.y
		z += v.z
	}
	
	infix operator fun minusAssign(v: Vector) {
		x -= v.x
		y -= v.y
		z -= v.z
	}
	
	infix operator fun divAssign(v: Int) {
		x /= v
		y /= v
		z /= v
	}
	
	infix operator fun divAssign(v: Vector) {
		x /= v.x
		y /= v.y
		z /= v.z
	}
	
	infix operator fun timesAssign(v: Vector) {
		x *= v.x
		y *= v.y
		z *= v.z
	}
	
	fun length() = Math.sqrt(x * x + y * y + z * z.toDouble()).toInt()
	
	fun copy() = Vector(x, y, z)
	
	fun reset() {
		x = 0f
		y = 0f
		z = 0f
	}
	
}