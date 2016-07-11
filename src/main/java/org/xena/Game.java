package org.xena;

import org.xena.cs.GameEntity;
import org.xena.cs.GameKT;

/**
 * Created by Jonathan on 8/26/2016.
 */
public class Game {
	
	public static void register(GameEntity entity) {
		int index = GameKT.entities.add(entity);
		GameKT.entityMap.put(entity.address(), index);
	}
	
}
