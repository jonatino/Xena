package org.xena.cs.bsp;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jonathan on 8/20/2016.
 */
public class BspFile {
	
	public static class dplane_t extends Structure {
		
		public Vector normal;  // normal vector
		public float dist; // distance from origin
		public int type;   // plane axis identifier
		
		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("normal", "dist", "type");
		}
	}
	
	public static class dnode_t extends Structure {
		
		public int planenum;   // index into plane array
		public int[] children;    // negative numbers are -(leafs + 1), not nodes
		public short[] mins;  // for frustum culling
		public short[] maxs;
		public /*ushort*/ int firstface;   // index into face array
		public /*ushort*/ int numfaces;    // counting both sides
		public short area;     // If all leaves below this node are in the same area, then
		// this is the area index. If not, this is -1.
		public short paddding; // pad to 32 bytes length
		
		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("planenum", "children", "mins", "maxs", "firstface", "numfaces", "area", "paddding");
		}
	}
	
	public static class dleaf_t extends Structure {
		
		public int contents;       // OR of all brushes (not needed?)
		public short cluster;      // cluster this leaf is in
		public short area;           // area this leaf is in
		public short flags;      // flags
		public short[] mins;      // for frustum culling
		public short[] maxs;
		public /*ushort*/ int firstleafbrush;      // index into leafbrushes
		public /*ushort*/ int numleaffaces;
		public /*ushort*/ int numleafbrushes;
		public short leafWaterDataID;  // -1 for not in water
		
		//!!! NOTE: for maps of version 19 or lower uncomment this block
		    /*
            CompressedLightCube	ambientLighting;	// Precaculated light info for entities.
            short			padding;		// padding to 4-byte boundary
            */
		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("contents", "cluster", "area", "flags", "mins", "maxs", "firstleafbrush", "numleaffaces", "numleafbrushes", "leafWaterDataID");
		}
	}
	
	public static class Vector extends Structure {
		
		public float x;
		public float y;
		public float z;
		
		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("x", "y", "z");
		}
	}
	
	
	public enum BSP_LUMP_TYPE {
		//ENTITIES = 0,
		ENTITIES,
		PLANES,
		TEXTURES,
		VERTICES,
		VISIBILITY,
		NODES,
		TEXINFO,
		FACES,
		LIGHTING,
		CLIPNODES,
		LEAVES,
		MARKSURFACES,
		EDGES,
		SURFEDGES,
		MODELS,
		NUMLUMPS;
		
	}
	
	public static class dheader_t extends Structure {
		
		public int ident;                // BSP file identifier
		public int version;              // BSP file version
		public lump_t[] lumps;  // lump directory array
		public int mapRevision;          // the map's revision (iteration, version) number
		
		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("ident", "version", "lumps", "mapRevision");
		}
	}
	
	public static class lump_t extends Structure {
		
		public int fileofs;    // offset into file (bytes)
		public int filelen;    // length of lump (bytes)
		public int version;    // lump format version
		public int fourCC; // lump ident code
		
		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("fileofs", "filelen", "version", "fourCC");
		}
	}
	
}
