package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import net.minecraft.util.math.Direction;

import java.util.List;

public class XorLogicGateBlock extends AbstractLogicGateBlock {
    @Override
    public QuartusCircuitComponent getCircuitComponent() {
        return new QuartusCircuitComponent("XorLogicGate") {
            @Override
            public void updateComponent() {

            }

            @Override
            public List<Direction> getPossibleInputDirections() {
                return XorLogicGateBlock.this.getPossibleInputDirections();
            }

            @Override
            public List<Direction> getPossibleOutputDirections() {
                return XorLogicGateBlock.this.getPossibleOutputDirections();
            }
        };
    }
}
