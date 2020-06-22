package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.QuartusBusInfo;

public class QuartusCircuitInput extends QuartusCircuitComponent {
    public QuartusCircuitInput(String componentName) {
        super(componentName);
    }

    @Override
    public QuartusBusInfo getOutput() {
        return null;
    }
}
