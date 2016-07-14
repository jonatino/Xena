package org.xena.cs;

import lombok.Getter;
import lombok.Setter;

public class ClientState extends GameObject {

    @Setter
    @Getter
    private long localPlayerIndex;

    @Setter
    @Getter
    private long inGame;

    @Setter
    @Getter
    private long maxPlayer;

}
