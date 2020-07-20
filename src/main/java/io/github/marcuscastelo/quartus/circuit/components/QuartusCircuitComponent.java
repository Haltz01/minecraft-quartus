package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.ComponentConnection;
import io.github.marcuscastelo.quartus.circuit.QuartusBusInfo;
import io.github.marcuscastelo.quartus.circuit.QuartusLogic;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

import java.util.*;

public class QuartusCircuitComponent {
    Map<Direction, List<ComponentConnection>> connections;

    //TODO: ver como fazer pros extensores
    //TODO: fazer map de lista
    Map<Direction, QuartusBusInfo> outputInfo;
    Map<Direction, QuartusBusInfo> inputInfo;

    QuartusLogic logic;

    public static int LAST_ID = 1;

    private final int ID;
    private final String componentName;

    public QuartusCircuitComponent(String componentName, int ID) {
        this.componentName = componentName;
        this.connections = new HashMap<>();
        this.inputInfo = new HashMap<>();
        this.outputInfo = new HashMap<>();
        this.logic = null;

        this.ID = ID;

        Direction[] HORIZONTAL_DIRECTIONS = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
        for (Direction dir: HORIZONTAL_DIRECTIONS) {
            connections.put(dir, new ArrayList<>());
            outputInfo.put(dir, new QuartusBusInfo());
            inputInfo.put(dir, new QuartusBusInfo());
        }
    }

    public QuartusCircuitComponent(String componentName) {
        this(componentName, LAST_ID++);
    }

    public QuartusCircuitComponent(String componentName, QuartusLogic logic) {
        this(componentName);
        this.logic = logic;
    }

    public QuartusCircuitComponent(String componentName, int ID, QuartusLogic logic) {
        this(componentName, ID);
        this.logic = logic;
    }

    public int getID() { return ID; }

    private void updateInputInfo() {

    }

    public void updateComponent() {
        updateInputInfo();
        if (logic != null) logic.updateLogic(inputInfo, outputInfo);
        else {
            //Por padrão a lógica é simplesmente replicar os inputs no output
            QuartusBusInfo outputBus = getOutputInfo().get(Direction.NORTH);
            QuartusBusInfo inputBus = getInputInfo().get(Direction.SOUTH);
            outputBus.setValue(inputBus);
        }
    }

    public Map<Direction, QuartusBusInfo> getOutputInfo() { return outputInfo; }
    public Map<Direction, QuartusBusInfo> getInputInfo() { return inputInfo; }
    public boolean hasOutputConnections() {
        for (List<ComponentConnection> connectionsPerDir: connections.values()) {
            for (ComponentConnection connection: connectionsPerDir) {
                if (connection.getType().equals(ComponentConnection.ConnectionType.OUTPUT)) return true;
            }
        }
        return false;
    }

    public void addConnection(Direction direction, ComponentConnection connection) {
        connections.get(direction).add(connection);
    }

    public String getOutputConnectionsString() {
        StringBuilder str = new StringBuilder();

        for (Map.Entry<Direction, List<ComponentConnection>> connectionsEntries : connections.entrySet()) {
            Direction AtoBDirection = connectionsEntries.getKey();
            List<ComponentConnection> connectionsForThatDirection = connectionsEntries.getValue();
            for (ComponentConnection connection: connectionsForThatDirection) {
                if (connection.getType() == ComponentConnection.ConnectionType.OUTPUT) //Exibe apenas as conexões de saída
                    str.append(String.format("%s / %s -> %s / %s\n", this.toString(), AtoBDirection.getName(), connection.BtoADirection, connection.connectToCompStr));
            }
        }

        return str.toString();
    }

    public List<ComponentConnection> getOutputConnections() {
        List<ComponentConnection> outputConnections = new ArrayList<>();
        for (List<ComponentConnection> connectionsPerDir: connections.values())
            for (ComponentConnection connection: connectionsPerDir)
                if (connection.getType() == ComponentConnection.ConnectionType.OUTPUT)
                    outputConnections.add(connection);
        return outputConnections;
    }

    public List<ComponentConnection> getInputConnections() {
        List<ComponentConnection> inputConnections = new ArrayList<>();
        for (List<ComponentConnection> connectionsPerDir: connections.values())
            for (ComponentConnection connection: connectionsPerDir)
                if (connection.getType() != ComponentConnection.ConnectionType.OUTPUT)
                    inputConnections.add(connection);
        return inputConnections;
    }

    public List<Direction> getPossibleInputDirections() { return Collections.emptyList(); }
    public List<Direction> getPossibleOutputDirections() { return Collections.emptyList(); }

    @Override
    public String toString() {
        return componentName+"_"+getID();
    }
}
