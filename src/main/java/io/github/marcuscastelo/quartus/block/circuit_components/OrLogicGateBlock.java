package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import net.minecraft.util.math.Direction;

import java.util.List;

public class OrLogicGateBlock extends AbstractLogicGateBlock {
    @Override
    public QuartusCircuitComponent getCircuitComponent() {
        return new QuartusCircuitComponent("OrGate") {
            @Override
            public void updateComponent() {

            }

            @Override
            public List<Direction> getPossibleInputDirections() {
                return OrLogicGateBlock.this.getPossibleInputDirections();
            }

            @Override
            public List<Direction> getPossibleOutputDirections() {
                return OrLogicGateBlock.this.getPossibleOutputDirections();
            }
        };
    }
}
