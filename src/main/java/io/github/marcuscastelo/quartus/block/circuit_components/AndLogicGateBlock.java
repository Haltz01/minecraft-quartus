package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import io.github.marcuscastelo.quartus.registry.QuartusLogics;
import net.minecraft.util.math.Direction;

import java.util.List;

public class AndLogicGateBlock extends LogicGateBlock {
    @Override
    public QuartusCircuitComponent getCircuitComponent() {
        return new QuartusCircuitComponent("AndGate", QuartusLogics.AND_GATE) {
            @Override
            public List<Direction> getPossibleInputDirections() {
                return AndLogicGateBlock.this.getPossibleInputDirections();
            }

            @Override
            public List<Direction> getPossibleOutputDirections() {
                return AndLogicGateBlock.this.getPossibleOutputDirections();
            }
        };
    }
}
