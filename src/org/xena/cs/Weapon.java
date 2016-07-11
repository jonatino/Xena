package org.xena.cs;

import lombok.Getter;
import lombok.Setter;

public class Weapon extends GameObject {

    static final Weapon DEFAULT = new Weapon();

    @Getter
    @Setter
    private int clip1;

    @Getter
    @Setter
    private int clip2;

    @Getter
    @Setter
    private boolean canReload;

    @Getter
    @Setter
    private int ammoType;

    @Getter
    @Setter
    private int weaponID;

}
