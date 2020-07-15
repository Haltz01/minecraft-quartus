package io.github.marcuscastelo.quartus.block.circuit_components;


import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExtensorGateBlock extends AbstractCircuitComponentBlock {
    public ExtensorGateBlock() {
        super(Settings.copy(Blocks.REPEATER));
    }

    @Override
    public QuartusCircuitComponent getCircuitComponent() {
        return new QuartusCircuitComponent("Extensor") {
            @Override
            public void updateComponent() {

            }

            @Override
            public List<Direction> getPossibleInputDirections() {
                return ExtensorGateBlock.this.getPossibleInputDirections();
            }

            @Override
            public List<Direction> getPossibleOutputDirections() {
                return ExtensorGateBlock.this.getPossibleOutputDirections();
            }
        };
    }

    List<Direction> possibleInputDirections = Arrays.asList(Direction.WEST, Direction.SOUTH, Direction.EAST);
    List<Direction> possibleOutputDirections = Collections.singletonList(Direction.NORTH);
    @Override
    public List<Direction> getPossibleInputDirections() {
        return possibleInputDirections;
    }

    @Override
    public List<Direction> getPossibleOutputDirections() {
        return possibleOutputDirections;
    }
}
