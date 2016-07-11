package org.xena.cs;

import lombok.Getter;

class GamePlayer extends GameEntity {

    @Getter
    protected int lifeState;

    @Getter
    protected int health;

    @Getter
    protected Weapon activeWeapon = Weapon.DEFAULT;

    @Getter
    protected final float[] velocity = new float[3];

    @Getter
    protected final float[] viewAngles = new float[3];

}
