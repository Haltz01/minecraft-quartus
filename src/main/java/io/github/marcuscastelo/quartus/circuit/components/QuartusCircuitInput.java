package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.Quartus;
import net.minecraft.util.math.Direction;

import java.util.Collections;
import java.util.List;

public class QuartusCircuitInput extends QuartusCircuitComponent {
    public static final String TYPE = "QuartusInput";

    public QuartusCircuitInput(int ID) {
        super(TYPE, ID);
    }

    public QuartusCircuitInput() {
        super(TYPE);
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
        super.updateComponent();
    }
}
