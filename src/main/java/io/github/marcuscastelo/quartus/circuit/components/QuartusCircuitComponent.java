package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.ComponentConnection;
import io.github.marcuscastelo.quartus.circuit.QuartusBusInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.math.Direction;

public abstract class QuartusCircuitComponent {
    List<ComponentConnection<QuartusCircuitComponent>> connections;

    Map<Direction, QuartusBusInfo> outputInfo;
    Map<Direction, QuartusBusInfo> inputInfo;

    public static int LAST_ID = 1;

    private final int ID;
    private final String componentName;
    public QuartusCircuitComponent(String componentName) {
        this.componentName = componentName;
        connections = new ArrayList<>();
        this.inputInfo = new HashMap<>();
        this.outputInfo = new HashMap<>();

        ID = LAST_ID++;
    }

    public int getID() { return ID; }

    public abstract void updateComponent();

    public Map<Direction, QuartusBusInfo> getOutputInfo() { return outputInfo; }
    public Map<Direction, QuartusBusInfo> getInputInfo() { return inputInfo; }

    public String getOutputConnectionsString() {
        StringBuilder str = new StringBuilder();

        for (ComponentConnection<QuartusCircuitComponent> componentConnection : connections) {
            if (componentConnection.getType() == ComponentConnection.ConnectionType.OUTPUT) //Exibe apenas as conexões de saída
                str.append(String.format("%s->%s\n", this.toString(), componentConnection.getB().toString()));
        }

        return str.toString();
    }

    @Override
    public String toString() {
        return componentName+getID();
    }
}
