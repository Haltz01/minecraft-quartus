package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.registry.QuartusLogics;
import net.minecraft.util.math.Direction;


public class CircuitOutput extends CircuitComponent {
    //TODO: remover Quartus do TYPE
    public static final String COMP_NAME = "QuartusOutput";
    public static final ComponentDirectionInfo outputDirectionInfo = new ComponentDirectionInfo(Direction.SOUTH, Direction.NORTH);

    public CircuitOutput(int ID) {
        super(COMP_NAME, outputDirectionInfo, ID, QuartusLogics.OUTPUT);
    }

    public CircuitOutput() {
        super(COMP_NAME, outputDirectionInfo, QuartusLogics.OUTPUT);
    }

    public void updateComponent(QuartusCircuit circuit) {
        super.updateComponent(circuit);
    }

}
