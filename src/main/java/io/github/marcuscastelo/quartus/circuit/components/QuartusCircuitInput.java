package io.github.marcuscastelo.quartus.circuit.components;

import com.google.common.collect.ImmutableList;
import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.registry.QuartusLogics;
import net.minecraft.util.math.Direction;

import java.util.Collections;
import java.util.List;

public class QuartusCircuitInput extends QuartusCircuitComponent {
    public static final String TYPE = "QuartusInput";
    public static final QuartusCircuitComponentDirectionInfo inputDirectionInfo = new QuartusCircuitComponentDirectionInfo(Direction.SOUTH, Direction.NORTH);

    public QuartusCircuitInput(int ID) {
        super(TYPE, inputDirectionInfo, ID, QuartusLogics.INPUT);
    }

    public QuartusCircuitInput() {
        super(TYPE, inputDirectionInfo, QuartusLogics.INPUT);
    }

    public void updateComponent(QuartusCircuit circuit) {
        super.updateComponent(circuit);
    }
}
