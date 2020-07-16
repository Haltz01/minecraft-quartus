package io.github.marcuscastelo.quartus.circuit;

import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitInput;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitOutput;
import io.github.marcuscastelo.quartus.network.QuartusSerializable;
import io.github.marcuscastelo.quartus.registry.QuartusLogics;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.util.math.Direction;

import java.util.*;

public class QuartusCircuit implements QuartusSerializable<QuartusCircuit, String> {

    private final Map<Integer, QuartusCircuitInput> circuitInputs;
    private final Map<Integer, QuartusCircuitOutput> circuitOutputs;
    private final Map<Integer, QuartusCircuitComponent> otherComponents;

    public QuartusCircuit(List<QuartusCircuitComponent> components) {
        this();
        for (QuartusCircuitComponent component: components) {
            if (component instanceof QuartusCircuitInput) circuitInputs.putIfAbsent(component.getID(), (QuartusCircuitInput) component);
            else if (component instanceof QuartusCircuitOutput) circuitOutputs.putIfAbsent(component.getID(), (QuartusCircuitOutput) component);
            this.otherComponents.putIfAbsent(component.getID(),component);
        }
    }

    public QuartusCircuit() {
        circuitInputs = new HashMap<>();
        circuitOutputs = new HashMap<>();
        otherComponents = new HashMap<>();
    }

    public void addComponent(QuartusCircuitComponent component) {
        if (component instanceof QuartusCircuitInput)
            circuitInputs.putIfAbsent(component.getID(), (QuartusCircuitInput) component);
        else if (component instanceof QuartusCircuitOutput)
            circuitOutputs.putIfAbsent(component.getID(), (QuartusCircuitOutput) component);
        else
            otherComponents.putIfAbsent(component.getID(), component);
    }

    public int getInputCount() { return circuitInputs.size(); }
    public int getOutputCount() { return circuitOutputs.size(); }

    public void addLink(QuartusCircuitComponent compA, QuartusCircuitComponent compB) {
        //TODO: ver pq to passando 2 pro connection type
        compA.addConnection(new ComponentConnection<>(ComponentConnection.ConnectionType.OUTPUT, compA, compB));
        compB.addConnection(new ComponentConnection<>(ComponentConnection.ConnectionType.INPUT, compB, compA));
    }

    @Nullable
    public QuartusCircuitComponent getComponentByID(int ID) {
        QuartusCircuitComponent component = otherComponents.getOrDefault(ID, null);
        if (component == null) component = circuitInputs.getOrDefault(ID, null);
        if (component == null) component = circuitOutputs.getOrDefault(ID, null);
        return component;
    }

    private void updateInputs() {

    }

    private void updateComponents() {

    }

    private void updateOutputs() {

    }

    public void updateCircuit() {
        updateInputs();
        updateComponents();
        updateOutputs();
    }

    @Override
    public String serialize() {
        StringBuilder str = new StringBuilder();
        Queue<QuartusCircuitComponent> componentsToPrint = new LinkedList<>(circuitInputs.values());

        List<QuartusCircuitComponent> alreadyPrintedComponents = new ArrayList<>();
        while (!componentsToPrint.isEmpty()) {
            QuartusCircuitComponent component = componentsToPrint.poll();
            if (alreadyPrintedComponents.contains(component)) continue;
            alreadyPrintedComponents.add(component);

            str.append(component.getOutputConnectionsString());
            for (ComponentConnection<QuartusCircuitComponent> connection: component.getOutputConnections())
                componentsToPrint.add(connection.getB());
        }

        return str.toString();
    }

    @Override
    public void unserialize(String serial) {
        Map<Integer, QuartusCircuitComponent> initializedGates = new HashMap<>();
        String[] lines = serial.split("\n");
        for (String line: lines) {
            String[] sides = line.split("->");
            String[] fromParams = sides[0].split("_");
            String[] toParams = sides[1].split("_");
            String fromGateType = fromParams[0];
            int fromGateID = Integer.parseInt(fromParams[1]);
            String toGateType = toParams[0];
            int toGateID = Integer.parseInt(toParams[1]);

            QuartusCircuitComponent fromComp = initializedGates.getOrDefault(fromGateID, null);
            QuartusCircuitComponent toComp = initializedGates.getOrDefault(toGateID, null);

            if (fromComp == null) {
                fromComp = new QuartusCircuitComponent(fromGateType, QuartusLogics.getLogicByID(fromGateType)) {
                    @Override
                    public List<Direction> getPossibleInputDirections() {
                        return null;
                    }

                    @Override
                    public List<Direction> getPossibleOutputDirections() {
                        return null;
                    }

                    @Override
                    public int getID() {
                        return fromGateID;
                    }
                };
                initializedGates.putIfAbsent(fromGateID, fromComp);
                addComponent(fromComp);
            }

            if (toComp == null) {
                toComp = new QuartusCircuitComponent(toGateType, QuartusLogics.getLogicByID(toGateType)) {
                    @Override
                    public List<Direction> getPossibleInputDirections() {
                        return null;
                    }

                    @Override
                    public List<Direction> getPossibleOutputDirections() {
                        return null;
                    }

                    @Override
                    public int getID() {
                        return toGateID;
                    }
                };
                initializedGates.putIfAbsent(toGateID, toComp);
                addComponent(toComp);
            }

            addLink(fromComp, toComp);
        }
    }

    @Override
    public String toString() {
        return serialize();
    }
}
