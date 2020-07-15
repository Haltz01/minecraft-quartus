package io.github.marcuscastelo.quartus.circuit.components;

public abstract class QuartusCircuitInput extends QuartusCircuitComponent {
    public QuartusCircuitInput() {
        super("QuartusInput");
    }

    public abstract void updateComponent();
}
