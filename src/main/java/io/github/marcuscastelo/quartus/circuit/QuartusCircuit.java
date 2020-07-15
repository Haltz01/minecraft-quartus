package io.github.marcuscastelo.quartus.circuit;

import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitInput;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitOutput;
import io.github.marcuscastelo.quartus.network.QuartusSerializable;
import jdk.internal.jline.internal.Nullable;

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

    public void addInput(QuartusCircuitInput input) { circuitInputs.putIfAbsent(input.getID(), input); }
    public void addOutput(QuartusCircuitOutput output) { circuitOutputs.putIfAbsent(output.getID(), output); }
    public void addComponent(QuartusCircuitComponent component) { otherComponents.putIfAbsent(component.getID(), component); }

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

    public void tickCircuit() {
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

    }
}
