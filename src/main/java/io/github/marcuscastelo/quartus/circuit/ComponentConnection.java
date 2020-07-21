package io.github.marcuscastelo.quartus.circuit;

import net.minecraft.util.math.Direction;

public class ComponentConnection {
    public enum ConnectionType {
        INPUT, OUTPUT
    }

    public final ConnectionType type;
    public final String connectToCompStr;
    public final Direction BtoADirection;
    public ComponentConnection(ConnectionType type, String connectToCompStr, Direction BtoADirection) {
        this.type = type;
        this.connectToCompStr = connectToCompStr;
        this.BtoADirection = BtoADirection;
    }

    public ConnectionType getType() {
        return type;
    }
}
