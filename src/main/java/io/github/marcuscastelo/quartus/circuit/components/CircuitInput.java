package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.registry.QuartusLogics;
import net.minecraft.util.math.Direction;

public class CircuitInput extends CircuitComponent {
    public static final String COMP_NAME = "QuartusInput";
    public static final ComponentDirectionInfo inputDirectionInfo = new ComponentDirectionInfo(Direction.SOUTH, Direction.NORTH);

    public CircuitInput(int ID) {
        super(COMP_NAME, inputDirectionInfo, ID, QuartusLogics.INPUT);
    }

    public CircuitInput() {
        super(COMP_NAME, inputDirectionInfo, QuartusLogics.INPUT);
    }

    public void updateComponent(QuartusCircuit circuit) {
        super.updateComponent(circuit);
    }
}
