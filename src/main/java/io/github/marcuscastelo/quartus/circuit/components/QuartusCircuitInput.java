package io.github.marcuscastelo.quartus.circuit.components;

import net.minecraft.util.math.Direction;

import java.util.Collections;
import java.util.List;

public class QuartusCircuitInput extends QuartusCircuitComponent {
    public QuartusCircuitInput(int ID) {
        super("QuartusInput", ID);
    }

    public QuartusCircuitInput() {
        super("QuartusInput");
    }

    @Override
    public List<Direction> getPossibleInputDirections() {
        return Collections.emptyList();
    }

    @Override
    public List<Direction> getPossibleOutputDirections() {
        return Collections.emptyList();
    }

    public void updateComponent() {
        System.out.println("Trying to update input " + getID());
    }
}
