package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import net.minecraft.util.math.Direction;

import java.util.List;

public class XnorLogicGateBlock extends AbstractLogicGateBlock {
    @Override
    public QuartusCircuitComponent getCircuitComponent() {
        return new QuartusCircuitComponent("XnorGate") {
            @Override
            public void updateComponent() {

            }

            @Override
            public List<Direction> getPossibleInputDirections() {
                return XnorLogicGateBlock.this.getPossibleInputDirections();
            }

            @Override
            public List<Direction> getPossibleOutputDirections() {
                return XnorLogicGateBlock.this.getPossibleOutputDirections();
            }
        };
    }
}
