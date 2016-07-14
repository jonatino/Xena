package org.xena.cs;

import lombok.Getter;
import lombok.Setter;

public class Weapon extends GameObject {

    @Getter
    @Setter
    private long clip1;

    @Getter
    @Setter
    private long clip2;

    @Getter
    @Setter
    private boolean canReload;

    @Getter
    @Setter
    private long ammoType;

    @Getter
    @Setter
    private long weaponID;

}
