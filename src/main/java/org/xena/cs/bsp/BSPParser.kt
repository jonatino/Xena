package org.xena.cs.bsp

import org.xena.cs.bsp.BspFile.*
import org.xena.plugin.utils.Vector
import org.xena.plugin.utils.ushort
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Files
import java.nio.file.Paths


class BSPParser(val fileName: String) {
	
	private val LUMP_LEAFS = 10
	private val LUMP_NODES = 5
	private val LUMP_PLANES = 1
	
	val stream by lazy { ByteBuffer.wrap(Files.readAllBytes(Paths.get(fileName))) }
	
	var header = dheader_t()
	
	var leafs = emptyArray<dleaf_t>()
	var nodes = emptyArray<dnode_t>()
	var planes = emptyArray<dplane_t>()
	
	fun getLeafForPoint(point: Vector): dleaf_t {
		var index = 0
		
		var node: dnode_t
		var plane: dplane_t
		
		while (index >= 0) {
			node = nodes[index]
			plane = planes[node.planenum]
			
			val d = (point.x * plane.normal.x + point.y * plane.normal.y + point.z * plane.normal.z) - plane.dist
			
			index = if (d > 0) node.children[0] else node.children[1]
		}
		if (-index - 1 >= 0 && -index - 1 < leafs.size) {
			return leafs[-index - 1]
		} else {
			val newLeaf = dleaf_t()
			
			newLeaf.area = -1
			newLeaf.contents = 0
			return newLeaf
		}
	}
	
	fun visible(vStart: Vector, vEnd: Vector): Boolean {
		val vDirection = vEnd - vStart
		
		var steps = vDirection.length()
		vDirection /= steps
		
		var leaf: dleaf_t
		val vPoint = vEnd.copy()
		while (steps >= 0) {
			vPoint -= vDirection
			
			leaf = getLeafForPoint(vPoint)
			
			if (leaf.area.toInt() != -1) {
				if (leaf.contents and 0x1 != 0) {
					return false
				}
			}
			steps--
		}
		return true
	}
	
	fun parse() {
		header.ident = stream.int
		
		if (header.ident == 'V'.toInt() + ('B'.toInt() shl 8) + ('S'.toInt() shl 16) + ('P'.toInt() shl 24)) {
			stream.order(ByteOrder.BIG_ENDIAN)
		} else {
			stream.order(ByteOrder.nativeOrder())
		}
		
		header.version = stream.int
		
		header.lumps = Array(64) {
			val lump = BspFile.lump_t()
			lump.fileofs = stream.int
			lump.filelen = stream.int
			lump.version = stream.int
			lump.fourCC = stream.int
			
			lump
		}
		
		header.mapRevision = stream.int
		
		stream.position(header.lumps[LUMP_LEAFS].fileofs)
		leafs = Array(header.lumps[LUMP_LEAFS].filelen / 56) {
			val leaf = dleaf_t()
			
			leaf.contents = stream.int
			leaf.cluster = stream.short
			
			val areaFlags = stream.int
			leaf.area = areaFlags and 0x1FF
			leaf.flags = areaFlags shr 9 and 0x3F
			
			leaf.flags = stream.short.toInt()
			
			for (i in 0..leaf.mins.lastIndex) leaf.mins[i] = stream.short
			for (i in 0..leaf.maxs.lastIndex) leaf.maxs[i] = stream.short
			
			leaf.firstleafface = stream.ushort
			leaf.numleaffaces = stream.ushort
			leaf.firstleafbrush = stream.ushort
			leaf.numleafbrushes = stream.ushort
			leaf.leafWaterDataID = stream.short
			
			leaf
		}
		
		stream.position(header.lumps[LUMP_PLANES].fileofs)
		planes = Array(header.lumps[LUMP_PLANES].filelen / 20) {
			val plane = dplane_t()
			
			plane.normal = Vector(stream.float, stream.float, stream.float)
			plane.dist = stream.float
			plane.type = stream.int
			
			plane
		}
		
		stream.position(header.lumps[LUMP_NODES].fileofs)
		nodes = Array(header.lumps[LUMP_NODES].filelen / 32) {
			val node = dnode_t()
			
			node.planenum = stream.int
			
			for (i in 0..node.children.lastIndex) node.children[i] = stream.int
			for (i in 0..node.mins.lastIndex) node.mins[i] = stream.short
			for (i in 0..node.maxs.lastIndex) node.maxs[i] = stream.short
			
			node.firstface = stream.ushort
			node.numfaces = stream.ushort
			node.area = stream.short
			node.paddding = stream.short
			
			node
		}
	}
	
	companion object {
		
		@JvmStatic fun main(args: Array<String>) {
			val tspawn = Vector(744.60254f, -604.03125f, 220.09381f)
			val tspawn2 = Vector(743.60254f, -604.03125f, 220.09381f)
			val upperTunnels = Vector(-1406.858f, 1254.9688f, 96.09381f)
			val p = BSPParser("E:\\Games\\Steam\\SteamApps\\common\\Counter-Strike Global Offensive\\csgo\\maps\\de_dust2.bsp")
			p.parse()
			
			println(p.visible(tspawn, tspawn2))
			println(p.visible(tspawn, upperTunnels))
			println(p.visible(upperTunnels, tspawn2))
		}
	}
	
}
