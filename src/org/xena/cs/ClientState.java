package org.xena.cs;

import lombok.Getter;
import lombok.Setter;

public class ClientState extends GameObject {

    @Setter
    @Getter
    private int localPlayerIndex;

    @Setter
    @Getter
    private int inGame;

    @Setter
    @Getter
    private int maxPlayer;

}
