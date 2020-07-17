package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import io.github.marcuscastelo.quartus.registry.QuartusLogics;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.List;

public class NotLogicGateBlock extends LogicGateBlock {
    @Override
    public QuartusCircuitComponent getCircuitComponent() {
        return new QuartusCircuitComponent("NotGate", QuartusLogics.NOT_GATE) {
            @Override
            public void updateComponent() {
            }

            @Override
            public List<Direction> getPossibleInputDirections() {
                return NotLogicGateBlock.this.getPossibleInputDirections();
            }

            @Override
            public List<Direction> getPossibleOutputDirections() {
                return NotLogicGateBlock.this.getPossibleOutputDirections();
            }
        };
    }

    @Override
    public List<Direction> getPossibleInputDirections() {
        return Arrays.asList(Direction.SOUTH);
    }
}
