package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.ComponentConnection;
import io.github.marcuscastelo.quartus.circuit.QuartusBusInfo;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public boolean hasOutputConnections() {
        for (ComponentConnection<QuartusCircuitComponent> connection: connections) {
            if (connection.getType().equals(ComponentConnection.ConnectionType.OUTPUT)) return true;
        }
        return false;
    }

    public void addConnection(ComponentConnection<QuartusCircuitComponent> connection) {
        connections.add(connection);
    }

    public String getOutputConnectionsString() {
        StringBuilder str = new StringBuilder();

        for (ComponentConnection<QuartusCircuitComponent> componentConnection : connections) {
            if (componentConnection.getType() == ComponentConnection.ConnectionType.OUTPUT) //Exibe apenas as conexões de saída
                str.append(String.format("%s->%s\n", this.toString(), componentConnection.getB().toString()));
        }

        return str.toString();
    }

    public List<ComponentConnection<QuartusCircuitComponent>> getOutputConnections() {
        return connections.stream().filter(connection -> connection.getType() == ComponentConnection.ConnectionType.OUTPUT).collect(Collectors.toList());
    }

    public List<ComponentConnection<QuartusCircuitComponent>> getInputConnections() {
        return connections.stream().filter(connection -> connection.getType() == ComponentConnection.ConnectionType.INPUT).collect(Collectors.toList());
    }

    public abstract List<Direction> getPossibleInputDirections();
    public abstract List<Direction> getPossibleOutputDirections();

    @Override
    public String toString() {
        return componentName+getID();
    }
}
