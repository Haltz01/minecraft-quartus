package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.QuartusBusInfo;

public class QuartusCircuitOutput extends QuartusCircuitComponent{
    public QuartusCircuitOutput(String componentName) {
        super(componentName);
    }

    @Override
    public QuartusBusInfo getOutput() {
        return null;
    }
}
