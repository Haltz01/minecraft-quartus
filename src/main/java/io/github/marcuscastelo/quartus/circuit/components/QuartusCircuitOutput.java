package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.registry.QuartusLogics;
import net.minecraft.util.math.Direction;


public class QuartusCircuitOutput extends QuartusCircuitComponent {
    public static final String TYPE = "QuartusOutput";
    public static final QuartusCircuitComponentDirectionInfo outputDirectionInfo = new QuartusCircuitComponentDirectionInfo(Direction.SOUTH, Direction.NORTH);

    public QuartusCircuitOutput(int ID) {
        super(TYPE, outputDirectionInfo, ID, QuartusLogics.OUTPUT);
    }

    public QuartusCircuitOutput() {
        super(TYPE, outputDirectionInfo, QuartusLogics.OUTPUT);
    }

    public void updateComponent(QuartusCircuit circuit) {
        super.updateComponent(circuit);
    }

}
