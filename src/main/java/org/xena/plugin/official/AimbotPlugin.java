/*
package org.abendigo.plugin.official;

import org.abendigo.cs.*;
import Logger;
import org.abendigo.plugin.AbstractPlugin;
import PluginManifest;
import org.abendigo.process.Data;
import org.abendigo.process.GameProcess;
import org.abendigo.process.Module;
import org.abendigo.task.TaskManager;

import java.util.Set;

@PluginManifest(name = "Aim bot", description = "Aims for you.")
public final class AimbotPlugin extends AbstractPlugin {

	public AimbotPlugin(Logger logger, GameProcess process, Module client, Module engine, TaskManager taskManager) {
		super(logger, process, client, engine, taskManager);
	}

	@Override
	public void pulse(ClientState clientState, Player me, BaseEntity[] entities, Set<Player> players) {
		int index = closestEntity(me, entities);

		if (index > 0) {
			System.out.println("Dont chu dare: " + index);
			//Calculate Angle To Closest Entity
			CalcAngle(me.getPosition(), entities[index].getPosition(), entities[index].getAngles());

			System.out.println("Finished calcangle");
			//Write To AngRotation The Current Angle Of The Entity
			int anglePointer = engine().read(Offsets.m_dwClientState, Data.Type.INT).getInt();

			System.out.println("Dat angle pointer: " + anglePointer);
			write(anglePointer, Offsets.m_dwViewAngles, Data.ofFloat(entities[index].getAngles()[0]));
			System.out.println("Wrote the first toss");
			write(anglePointer, Offsets.m_dwViewAngles + 4, Data.ofFloat(entities[index].getAngles()[0]));

			System.out.println("Written the stuff for index: " + index);
		}
		//mem.Write<float>(dwAngPtr + 0x4C88, EntList[Index].Angle[0]);
		//mem.Write<float>(dwAngPtr + 0x4C8C, EntList[Index].Angle[1]);

	}

	public float Get3D(float X, float Y, float Z, float eX, float eY, float eZ) {
		return (float) (Math.sqrt((eX - X) * (eX - X) + (eY - Y) * (eY - Y) + (eZ - Z) * (eZ - Z)));
	}

	public int closestEntity(Player me, BaseEntity[] entities) {
		//Variables
		float fLowest = 100000, TMP;
		int iIndex = -1;

		int count = 0;
		for (int i = 0; i < entities.length; i++) {
			//if (entities[i] != null && entities[i].isPlayer()) {
			BaseEntity other = entities[i];
			if (other == null) {
				continue;
			}
			//Store Distances In Array
//			if (other.getTeam() != me.getTeam() && me.getPosition() != null && other.getPosition() != null) {
			TMP = Get3D(me.getPosition()[0], me.getPosition()[1], me.getPosition()[2], other.getPosition()[0], other.getPosition()[1], other.getPosition()[2]);

			//If Enemy Has Lower Distance The Player 1, Replace (var)Lowest With Current Enemy Distance
			if (TMP < fLowest*/
/* && other.getHealth() != 0*//*
) {
				fLowest = TMP;
				iIndex = i;
			}
//			}
			//}
		}
		return iIndex;
	}

	public void CalcAngle(float[] src, float[] dst, float[] angles) {
		double[] delta = new double[]{
				src[0] - dst[0], src[1] - dst[1], src[2] - dst[2]
		};

		double hyp = Math.sqrt(delta[0] * delta[0] + delta[1] * delta[1]);
		angles[0] = (float) (Math.atan(delta[2] / hyp) * (180 / Math.PI));
		angles[1] = (float) (Math.atan(delta[1] / delta[0]) * (180 / Math.PI));
		angles[2] = 0.0f;

		if (delta[0] >= 0.0) {
			angles[1] += 180.0f;
		}

*/
/*		if (fFlags == 775) {
            angles[0] = angles[0] + 5;
		}*//*

	}

}
*/
