package io.github.marcuscastelo.quartus.circuit.components;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.github.marcuscastelo.quartus.circuit.ComponentConnection;
import io.github.marcuscastelo.quartus.circuit.QuartusBusInfo;
import io.github.marcuscastelo.quartus.circuit.QuartusLogic;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.stream.Collectors;

public class QuartusCircuitComponent {
    List<ComponentConnection<QuartusCircuitComponent>> connections;

    //TODO: ver como fazer pros extensores
    Map<Direction, QuartusBusInfo> outputInfo;
    Map<Direction, QuartusBusInfo> inputInfo;

    QuartusLogic logic;

    public static int LAST_ID = 1;

    private final int ID;
    private final String componentName;

    public QuartusCircuitComponent(String componentName, int ID) {
        this.componentName = componentName;
        connections = new ArrayList<>();
        this.inputInfo = new HashMap<>();
        this.outputInfo = new HashMap<>();
        this.logic = null;

        this.ID = ID;
    }

    public QuartusCircuitComponent(String componentName) {
        this(componentName, LAST_ID++);
    }

    public QuartusCircuitComponent(String componentName, QuartusLogic logic) {
        this(componentName);
        this.logic = logic;
    }

    public int getID() { return ID; }

    public void updateComponent() {
        if (logic != null) logic.updateLogic(inputInfo, outputInfo);
    }

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

    public List<Direction> getPossibleInputDirections() { return Collections.emptyList(); }
    public List<Direction> getPossibleOutputDirections() { return Collections.emptyList(); }

    @Override
    public String toString() {
        return componentName+"_"+getID();
    }
}
