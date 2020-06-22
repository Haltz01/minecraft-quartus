package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.QuartusBusInfo;

public class QuartusLogicGate extends QuartusCircuitComponent{
    public QuartusLogicGate() {
        super("LogicGate");
    }

    public QuartusLogicGate(String componentName) {
        super(componentName);
    }

    @Override
    public QuartusBusInfo getOutput() {
        return null;
    }
}
