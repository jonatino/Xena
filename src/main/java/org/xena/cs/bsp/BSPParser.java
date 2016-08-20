/*
package org.xena.cs.bsp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

*/
/**
 * Created by Jonathan on 8/20/2016.
 *//*

public class BSPParser {
	
	private final int LUMP_LEAFS = 10;
	private final int LUMP_NODES = 5;
	private final int LUMP_PLANES = 1;
	
	private ByteBuffer stream;
	public BspFile.dheader_t BspHeader;
	
	
	public BspFile.dleaf_t[] Leafs;
	public BspFile.lump_t Lumps = new BspFile.lump_t();
	public BspFile.dnode_t[] Nodes;
	public BspFile.dplane_t[] Planes;
	private byte[] fileBytes;
	
	/// <summary>
	///     It loads the Bsp File into the Buffer.
	/// </summary>
	/// <param name="name">Map Name</param>
	public void LoadFile(String name) {
		try {
			fileBytes = Files.readAllBytes(Paths.get(name));
			stream = ByteBuffer.wrap(fileBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/// <summary>
	///     It returns the Leaf which is at the specified Point
	/// </summary>
	/// <param name="point">The Point you wan't to get the Leaf for</param>
	/// <returns>Leaf which is at the Point</returns>
	
	public BspFile.dleaf_t GetLeafForPoint(BspFile.Vector point) {
		int node = 0;
		
		BspFile.dnode_t pNode;
		BspFile.dplane_t pPlane;
		
		float d = 0.0f;
		
		while (node >= 0) {
			pNode = Nodes[node];
			pPlane = Planes[pNode.planenum];
			
			d = point.x * pPlane.normal.x + point.y * pPlane.normal.y + point.z * pPlane.normal.z - pPlane.dist;
			
			node = d > 0 ? pNode.children[0] : pNode.children[1];
		}
		
		if ((-node - 1) >= 0 && -node - 1 < Leafs.length) {
			return Leafs[-node - 1];
		} else {
			BspFile.dleaf_t newLeaf = new BspFile.dleaf_t();
			
			newLeaf.area = -1;
			newLeaf.contents = 0;
			return newLeaf;
		}
		
	}
	
	private BspFile.dleaf_t GetLeafForPoint2(BspFile.Vector point) {
		int node = 0;
		
		BspFile.dnode_t pNode;
		BspFile.dplane_t pPlane;
		
		float d = 0.0f;
		
		while (node >= 0) {
			pNode = Nodes[node];
			pPlane = Planes[pNode.planenum];
			
			d = point.x * pPlane.normal.x + point.y * pPlane.normal.y + point.z * pPlane.normal.z - pPlane.dist;
			
			node = d > 0 ? pNode.children[0] : pNode.children[1];
		}
		return (
				(-node - 1) >= 0 && -node - 1 < Leafs.length ?
						Leafs[-node - 1] :
						new BspFile.dleaf_t() {
							area =-1,contents =0
						}
		);
		//return Leafs[-node - 1];
	}
	
	/// <summary>
	///     Checks if something is Visible
	/// </summary>
	/// <param name="vStart">Start Point</param>
	/// <param name="vEnd">End Point</param>
	/// <returns>true if visible false if invisible</returns>
	public boolean IsVisible(BspFile.Vector vStart, BspFile.Vector vEnd) {
		BspFile.Vector start = new BspFile.Vector(vEnd.x, vEnd.y, vEnd.z);
		BspFile.Vector end = new BspFile.Vector(vStart.x, vStart.y, vStart.z);
		
		BspFile.Vector vDirection = end - start;
		BspFile.Vector vPoint = end;
		
		int iStepCount = (int) vDirection.Length();
		
		vDirection /= iStepCount;
		
		BspFile.dleaf_t pLeaf = new BspFile.dleaf_t() {
			area =-1
		};
		
		while (iStepCount > 0) {
			vPoint += vDirection;
			
			pLeaf = GetLeafForPoint(new BspFile.Vector(vPoint.x, vPoint.y, vPoint.z));
			
			if (pLeaf.area != -1) {
				if (
						(pLeaf.contents & 0x1) == 0x1 ||
								(pLeaf.contents & 0x8000000) == 0x8000000) {
					break;
				}
			}
			
			iStepCount--;
		}
		return (pLeaf.contents & 0x1) != 0x1;
	}
	
	public bool Visible(_vec3 vStart, _vec3 vEnd) {
		var vDirection = new _vec3(vEnd.x - vStart.x, vEnd.y - vStart.y, vEnd.z - vStart.z);
		
		
		var iStepCount = (int) Math.Sqrt(vDirection.x * vDirection.x + vDirection.y *
				vDirection.y + vDirection.z * vDirection.z);
		
		vDirection.x /= iStepCount;
		vDirection.y /= iStepCount;
		vDirection.z /= iStepCount;
		
		BspFile.dleaf_t pLeaf;
		
		var i = 0;
		var vPoint = vEnd;
		
		while (i <= iStepCount) {
			vPoint.x -= vDirection.x;
			vPoint.y -= vDirection.y;
			vPoint.z -= vDirection.z;
			
			pLeaf = GetLeafForPoint(vPoint);
			
			if (pLeaf.cluster == 0 && pLeaf.area == 0 && pLeaf.numleafbrushes == 0) {
				if ((pLeaf.contents & 0x1) != 0) {
					return false;
				}
			}
			i++;
		}
		
		return true;
	}
	
	/// <summary>
	///     It Loads BspHeader, Lumps, Leafs, Nodes and Planes. Execute it after every Map Change with LoadFile
	///     Also execute it before using Visible Check
	///     Just needs to be executed if you ran Load File
	/// </summary>
	public void ParseData() {
		byte[] bytes2 = fileBytes;
		using(var br = new BinaryReader(new MemoryStream(bytes2)))
		{
			BspHeader.ident = UtilityReader.ReadInt(stream);
			
			if (BspHeader.ident == (int) ('V' + ('B' << 8) + ('S' << 16) + ('P' << 24)))
				UtilityReader.BigEndian = false;
			else
				UtilityReader.BigEndian = true;
			
			BspHeader.version = UtilityReader.ReadInt(stream);
			
			BspHeader.lumps = new BspFile.lump_t[64];
			for (var i = 0; i < BspHeader.lumps.Length; i++) {
				BspHeader.lumps[i] = new BspFile.lump_t();
				
				BspHeader.lumps[i].fileofs = UtilityReader.ReadInt(stream);
				
				BspHeader.lumps[i].filelen = UtilityReader.ReadInt(stream);
				
				BspHeader.lumps[i].version = UtilityReader.ReadInt(stream);
				
				BspHeader.lumps[i].fourCC = UtilityReader.ReadInt(stream);
			}
			BspHeader.mapRevision = UtilityReader.ReadInt(stream);
			
			Planes = new BspFile.dplane_t[BspHeader.lumps[LUMP_PLANES].filelen / 20];
			Leafs = new BspFile.dleaf_t[BspHeader.lumps[LUMP_LEAFS].filelen / 56];
			Nodes = new BspFile.dnode_t[BspHeader.lumps[LUMP_NODES].filelen / 32];
		}
		
		
		stream.Position = BspHeader.lumps[LUMP_LEAFS].fileofs;
		
		//fileStream.Read(Leafsa, BspHeader.lumps[LUMP_LEAFS].fileofs, BspHeader.lumps[LUMP_LEAFS].filelen);
		
		for (var i = 0; i < BspHeader.lumps[LUMP_LEAFS].filelen / 56; i++) {
			Leafs[i] = new BspFile.dleaf_t();
			
			Leafs[i].contents = UtilityReader.ReadInt(stream);
			
			Leafs[i].cluster = UtilityReader.ReadShort(stream);
			
			Leafs[i].area = UtilityReader.ReadShort(stream);
			
			Leafs[i].flags = UtilityReader.ReadShort(stream);
			
			Leafs[i].mins = new short[3];
			Leafs[i].mins[0] = UtilityReader.ReadShort(stream);
			Leafs[i].mins[1] = UtilityReader.ReadShort(stream);
			Leafs[i].mins[2] = UtilityReader.ReadShort(stream);
			
			
			Leafs[i].maxs = new short[3];
			Leafs[i].maxs[0] = UtilityReader.ReadShort(stream);
			Leafs[i].maxs[1] = UtilityReader.ReadShort(stream);
			Leafs[i].maxs[2] = UtilityReader.ReadShort(stream);
			
			
			Leafs[i].firstleafface = UtilityReader.ReadUShort(stream);
			
			Leafs[i].numleaffaces = UtilityReader.ReadUShort(stream);
			
			Leafs[i].firstleafbrush = UtilityReader.ReadUShort(stream);
			
			Leafs[i].numleafbrushes = UtilityReader.ReadUShort(stream);
			
			Leafs[i].leafWaterDataID = UtilityReader.ReadShort(stream);
		}
		
		
		//var Planesa = fileBytes.Skip(BspHeader.lumps[LUMP_PLANES].fileofs).Take(BspHeader.lumps[LUMP_PLANES].filelen).ToArray();
		//fileStream.Read(Planesa, BspHeader.lumps[LUMP_PLANES].fileofs, BspHeader.lumps[LUMP_PLANES].filelen);
		
		stream.Position = BspHeader.lumps[LUMP_PLANES].fileofs;
		for (var i = 0; i < BspHeader.lumps[LUMP_PLANES].filelen / 20; i++) {
			Planes[i].normal = new BspFile.Vector();
			
			Planes[i].normal.x = UtilityReader.ReadFloat(stream);
			Planes[i].normal.y = UtilityReader.ReadFloat(stream);
			Planes[i].normal.z = UtilityReader.ReadFloat(stream);
			
			Planes[i].dist = UtilityReader.ReadFloat(stream);
			
			Planes[i].type = UtilityReader.ReadInt(stream);
		}
		
		
		//var Nodesa = fileBytes.Skip(BspHeader.lumps[LUMP_NODES].fileofs).Take(BspHeader.lumps[LUMP_NODES].filelen).ToArray();
		//fileStream.Read(Nodesa, BspHeader.lumps[LUMP_NODES].fileofs, BspHeader.lumps[LUMP_NODES].filelen);
		stream.Position = BspHeader.lumps[LUMP_NODES].fileofs;
		
		for (var index = 0; index < BspHeader.lumps[LUMP_NODES].filelen / 32; index++) {
			Nodes[index] = new BspFile.dnode_t();
			
			Nodes[index].planenum = UtilityReader.ReadInt(stream);
			
			Nodes[index].children = new int[2];
			Nodes[index].children[0] = UtilityReader.ReadInt(stream);
			Nodes[index].children[1] = UtilityReader.ReadInt(stream);
			
			
			Nodes[index].mins = new short[3];
			Nodes[index].mins[0] = UtilityReader.ReadShort(stream);
			Nodes[index].mins[1] = UtilityReader.ReadShort(stream);
			Nodes[index].mins[2] = UtilityReader.ReadShort(stream);
			
			
			Nodes[index].maxs = new short[3];
			Nodes[index].maxs[0] = UtilityReader.ReadShort(stream);
			Nodes[index].maxs[1] = UtilityReader.ReadShort(stream);
			Nodes[index].maxs[2] = UtilityReader.ReadShort(stream);
			
			Nodes[index].firstface = UtilityReader.ReadUShort(stream);
			
			Nodes[index].numfaces = UtilityReader.ReadUShort(stream);
			
			Nodes[index].area = UtilityReader.ReadShort(stream);
			
			Nodes[index].paddding = UtilityReader.ReadShort(stream);
		}
	}
	
}
*/
