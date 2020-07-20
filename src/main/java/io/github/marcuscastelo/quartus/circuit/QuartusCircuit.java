package io.github.marcuscastelo.quartus.circuit;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitInput;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitOutput;
import io.github.marcuscastelo.quartus.network.QuartusSerializable;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

import java.util.*;

public class QuartusCircuit implements QuartusSerializable<QuartusCircuit, String> {

    private final Map<Integer, QuartusCircuitInput> circuitInputs;
    private final Map<Integer, QuartusCircuitOutput> circuitOutputs;
    private final Map<Integer, QuartusCircuitComponent> otherComponents;

    public QuartusCircuit(List<QuartusCircuitComponent> components) {
        this();
        for (QuartusCircuitComponent component: components) addComponent(component);
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

    public void setComponentAtID(int ID, QuartusCircuitComponent component) {
        if (component == null) throw new IllegalArgumentException("Component shall not be null");

        QuartusCircuitComponent match = otherComponents.getOrDefault(ID, null);

        if (match != null)  {
            otherComponents.put(ID, component);
            return;
        }
        else match = circuitInputs.getOrDefault(ID, null);

        if (match != null) {
            if (component instanceof QuartusCircuitInput)
                circuitInputs.put(ID, (QuartusCircuitInput) component);
            else Quartus.LOGGER.error("Illegal argument @QuartusCircuit::setComponentAt(): input is wrong type");
            return;
        }
        else match = circuitOutputs.getOrDefault(ID, null);

        if (match != null) {
            if (component instanceof QuartusCircuitOutput)
                circuitOutputs.put(ID, (QuartusCircuitOutput) component);
            else Quartus.LOGGER.error("Illegal argument @QuartusCircuit::setComponentAt(): output is wrong type");
            return;
        }

        Quartus.LOGGER.warn("QuartusCircuit::setComponentAt should not be used to add new components (comp = " + component.toString() + ")");
        otherComponents.put(ID, component);
    }

    public int getInputCount() { return circuitInputs.size(); }
    public int getOutputCount() { return circuitOutputs.size(); }

    public List<QuartusCircuitInput> getInputs() { return new ArrayList<>(this.circuitInputs.values()); }
    public List<QuartusCircuitOutput> getOutputs() { return new ArrayList<>(this.circuitOutputs.values()); }

    public void addLink(Direction AtoBDirection, Direction BtoADirection, QuartusCircuitComponent compA, QuartusCircuitComponent compB) {
        compA.addConnection(AtoBDirection, new ComponentConnection(ComponentConnection.ConnectionType.OUTPUT, compB.toString(), BtoADirection));
        compB.addConnection(BtoADirection, new ComponentConnection(ComponentConnection.ConnectionType.INPUT, compA.toString(), AtoBDirection));
    }

    @Nullable
    public QuartusCircuitComponent getComponentByID(int ID) {
        QuartusCircuitComponent component = otherComponents.getOrDefault(ID, null);
        if (component == null) component = circuitInputs.getOrDefault(ID, null);
        if (component == null) component = circuitOutputs.getOrDefault(ID, null);
        return component;
    }

    private void updateInputs() {
        for (QuartusCircuitInput input: circuitInputs.values()) {
            input.updateComponent(this);
        }
    }

    private void updateComponents() {
        for (QuartusCircuitComponent component: otherComponents.values()) {
            component.updateComponent(this);
        }
    }

    private void updateOutputs() {
        for (QuartusCircuitOutput output: circuitOutputs.values()) {
            output.updateComponent(this);
        }
    }

    public void updateCircuit() {
        updateInputs();
        updateComponents();
        updateOutputs();
    }

    @Override
    //TODO: change to match N->S format
    public String serialize() {
        StringBuilder str = new StringBuilder();
        Queue<QuartusCircuitComponent> componentsToPrint = new LinkedList<>(circuitInputs.values());

        List<QuartusCircuitComponent> alreadyPrintedComponents = new ArrayList<>();
        while (!componentsToPrint.isEmpty()) {
            QuartusCircuitComponent component = componentsToPrint.poll();
            if (alreadyPrintedComponents.contains(component)) continue;
            alreadyPrintedComponents.add(component);

            str.append(component.getOutputConnectionsString());
            for (ComponentConnection connection: component.getOutputConnections())
                componentsToPrint.add(getComponentByID(CircuitUtils.getComponentStrInfo(connection.connectToCompStr).getRight()));
        }

        return str.toString();
    }



    @Override
    //TODO: change to match N->S format
    public void unserialize(String serial) {
        Map<Integer, QuartusCircuitComponent> initializedGates = new HashMap<>();
        String[] lines = serial.split("\n");
        for (String line: lines) {
            if (!line.contains("/") || !line.contains("->")) continue;
            String[] sides = line.split("/");

            String fromGateStr = sides[0].trim();
            String toGateStr = sides[2].trim();

            String directionInfo = sides[1];
            String[] directions = directionInfo.split("->");
            String directionAtoBStr = directions[0].trim();
            String directionBtoAStr = directions[1].trim();

            Direction directionAtoB = Direction.byName(directionAtoBStr);
            Direction directionBtoA = Direction.byName(directionBtoAStr);

            Pair<String, Integer> fromGateInfo = CircuitUtils.getComponentStrInfo(fromGateStr);
            Pair<String, Integer> toGateInfo = CircuitUtils.getComponentStrInfo(toGateStr);

            String fromGateType = fromGateInfo.getLeft();
            int fromGateID = fromGateInfo.getRight();

            String toGateType = toGateInfo.getLeft();
            int toGateID = toGateInfo.getRight();

            QuartusCircuitComponent fromComp = initializedGates.getOrDefault(fromGateID, null);
            QuartusCircuitComponent toComp = initializedGates.getOrDefault(toGateID, null);

            if (fromComp == null) {
                fromComp = CircuitUtils.createPolimorphicComponent(fromGateType, fromGateID);
                initializedGates.putIfAbsent(fromGateID, fromComp);
                addComponent(fromComp);
            }

            if (toComp == null) {
                toComp = CircuitUtils.createPolimorphicComponent(toGateType, toGateID);
                initializedGates.putIfAbsent(toGateID, toComp);
                addComponent(toComp);
            }
            addLink(directionAtoB, directionBtoA, fromComp, toComp);
        }
    }

    @Override
    public String toString() {
        return serialize();
    }
}
