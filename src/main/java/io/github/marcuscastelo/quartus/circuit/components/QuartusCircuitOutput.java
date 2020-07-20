package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.circuit.QuartusBusInfo;
import net.minecraft.util.math.Direction;

import java.util.Collections;
import java.util.List;

public class QuartusCircuitOutput extends QuartusCircuitComponent {
    public static final String TYPE = "QuartusOutput";

    public QuartusCircuitOutput(int ID) {
        super(TYPE, ID);
    }

    public QuartusCircuitOutput() {
        super(TYPE);
    }

    public void updateComponent() {
        super.updateComponent();
    }

    @Override
    public List<Direction> getPossibleInputDirections() {
        return Collections.emptyList();
    }

    @Override
    public List<Direction> getPossibleOutputDirections() {
        return Collections.emptyList();
    }
}
