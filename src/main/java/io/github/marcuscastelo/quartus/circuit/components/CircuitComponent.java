package io.github.marcuscastelo.quartus.circuit.components;

import com.google.common.collect.ImmutableList;
import io.github.marcuscastelo.quartus.circuit.*;
import io.github.marcuscastelo.quartus.util.DirectionUtils;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;


public class CircuitComponent {
    private final ComponentDirectionInfo componentDirectionInfo;
    private final ComponentExecutionInfo executionInfo;
    private Map<Direction, List<ComponentConnection>> connections;

    //TODO: ver como fazer pros extensores
    //TODO: fazer map de lista

    private QuartusLogic logic;

    public static int LAST_ID = 1;

    private final int ID;
    private final String componentName;

    public CircuitComponent(String componentName, ComponentDirectionInfo componentDirectionInfo, int ID) {
        this.componentName = componentName;
        this.connections = new HashMap<>();
        this.logic = null;

        this.componentDirectionInfo = componentDirectionInfo;
        this.executionInfo = new ComponentExecutionInfo(componentDirectionInfo);

        this.ID = ID;

        for (Direction dir: DirectionUtils.HORIZONTAL_DIRECTIONS) {
            connections.put(dir, new ArrayList<>());
        }
    }

    public CircuitComponent(String componentName, ComponentDirectionInfo componentDirectionInfo) {
        this(componentName, componentDirectionInfo, LAST_ID++);
    }

    public CircuitComponent(String componentName, ComponentDirectionInfo componentDirectionInfo, QuartusLogic logic) {
        this(componentName, componentDirectionInfo);
        this.logic = logic;
    }

    public CircuitComponent(String componentName, ComponentDirectionInfo componentDirectionInfo, int ID, QuartusLogic logic) {
        this(componentName, componentDirectionInfo, ID);
        this.logic = logic;
    }

    public int getID() { return ID; }

    //TODO: tornar mais genérica: atualmente foca apenas em trazer a saída do outro (supondo ser única) para a entrada deste (supondo ser única)
    private void updateInputInfo(QuartusCircuit circuit) {
        forDirection:
        for (Map.Entry<Direction, List<ComponentConnection>> entry: connections.entrySet()) {
            Direction AtoBDirection = entry.getKey();

            //FIXME: suporta apenas uma entrada e uma saída
            List<ComponentConnection> possibleConnections = entry.getValue();
            if (entry.getValue().size() == 0) continue; //nenhuma conexão nessa direção
            ComponentConnection arbitrarilyChosenConnection = null;

            //Pega a primeira conexão de input encontrada (ou desiste da direção se nenhuma for encontrada)
            for (int i = 0; i < possibleConnections.size(); i++) {
                arbitrarilyChosenConnection = entry.getValue().get(i);
                if (arbitrarilyChosenConnection.getType() == ComponentConnection.ConnectionType.INPUT) break;
                if (i == possibleConnections.size()-1) continue forDirection;
            }

            int BID = CircuitUtils.getComponentStrInfo(arbitrarilyChosenConnection.connectToCompStr).getRight();
            CircuitComponent BComponent = circuit.getComponentByID(BID);

            Direction BtoADirection = arbitrarilyChosenConnection.BtoADirection;

            //Copia o output do B para o input do atual (A)
            ImmutableList<QuartusBus> BOutputs = BComponent.executionInfo.getOutput(BtoADirection);
            this.executionInfo.setInput(AtoBDirection, BOutputs);
        }
    }

    public void updateComponent(QuartusCircuit circuit) {
        updateInputInfo(circuit);
        if (logic != null) logic.updateLogic(executionInfo);
    }

    public ComponentExecutionInfo getExecutionInfo() { return executionInfo; }

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

    public Map<Direction, List<ComponentConnection>> getConnections() {
        return connections;
    }

    public final ImmutableList<Direction> getPossibleInputDirections() { return componentDirectionInfo.possibleInputDirections; }
    public final ImmutableList<Direction> getPossibleOutputDirections() { return componentDirectionInfo.possibleOutputDirections; }

    @Override
    public String toString() {
        return componentName+"_"+getID();
    }
}
