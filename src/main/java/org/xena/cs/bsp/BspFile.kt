package org.xena.cs.bsp

import org.xena.plugin.utils.Vector


/**
 * Created by Jonathan on 8/20/2016.
 */
class BspFile {
	
	class dplane_t {
		
		var normal = Vector()   // normal vector
		var dist: Float = 0.toFloat() // distance from origin
		var type: Int = 0   // plane axis identifier
		
	}
	
	class dnode_t {
		
		var planenum: Int = 0   // index into plane array
		var children = IntArray(2)    // negative numbers are -(leafs + 1), not nodes
		var mins = ShortArray(3)   // for frustum culling
		var maxs = ShortArray(3)
		/*ushort*/ var firstface: Int = 0   // index into face array
		/*ushort*/ var numfaces: Int = 0    // counting both sides
		var area: Short = 0     // If all leaves below this node are in the same area, then
		// this is the area index. If not, this is -1.
		var paddding: Short = 0 // pad to 32 bytes length
		
	}
	
	class dleaf_t {
		
		var contents: Int = 0       // OR of all brushes (not needed?)
		var cluster: Short = 0      // cluster this leaf is in
		var area: Int = 0           // area this leaf is in
		var flags: Int = 0      // flags
		var mins = ShortArray(3)      // for frustum culling
		var maxs = ShortArray(3)
		/*ushort*/ var firstleafface: Int = 0       // index into leaffaces
		/*ushort*/ var numleaffaces: Int = 0
		/*ushort*/ var firstleafbrush: Int = 0      // index into leafbrushes
		/*ushort*/ var numleafbrushes: Int = 0
		var leafWaterDataID: Short = 0  // -1 for not in water
		
	}
	
	class dheader_t {
		
		var ident: Int = 0                // BSP file identifier
		var version: Int = 0              // BSP file version
		var lumps: Array<lump_t> = emptyArray()  // lump directory array
		var mapRevision: Int = 0          // the map's revision (iteration, version) number
		
	}
	
	class lump_t {
		
		var fileofs: Int = 0    // offset into file (bytes)
		var filelen: Int = 0    // length of lump (bytes)
		var version: Int = 0    // lump format version
		var fourCC: Int = 0 // lump ident code
		
	}
	
}
