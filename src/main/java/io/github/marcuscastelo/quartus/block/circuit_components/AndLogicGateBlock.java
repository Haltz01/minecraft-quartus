package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import net.minecraft.util.math.Direction;

import java.util.List;

public class AndLogicGateBlock extends AbstractLogicGateBlock {
    @Override
    public QuartusCircuitComponent getCircuitComponent() {
        return new QuartusCircuitComponent("AndGate") {
            @Override
            public void updateComponent() {
                //Logica and foda
            }

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
