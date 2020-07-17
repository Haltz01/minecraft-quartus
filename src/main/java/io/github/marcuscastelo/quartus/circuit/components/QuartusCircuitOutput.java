package io.github.marcuscastelo.quartus.circuit.components;

import net.minecraft.util.math.Direction;

import java.util.Collections;
import java.util.List;

public class QuartusCircuitOutput extends QuartusCircuitComponent {
    public QuartusCircuitOutput(int ID) {
        super("QuartusOutput", ID);
    }

    public QuartusCircuitOutput() {
        super("QuartusOutput");
    }

    public void updateComponent() {}

    @Override
    public List<Direction> getPossibleInputDirections() {
        return Collections.emptyList();
    }

    @Override
    public List<Direction> getPossibleOutputDirections() {
        return Collections.emptyList();
    }
}
