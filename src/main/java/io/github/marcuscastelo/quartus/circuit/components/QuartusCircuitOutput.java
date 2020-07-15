package io.github.marcuscastelo.quartus.circuit.components;

public abstract class QuartusCircuitOutput extends QuartusCircuitComponent {
    public QuartusCircuitOutput() {
        super("QuartusOutput");
    }

    public abstract void updateComponent();
}
