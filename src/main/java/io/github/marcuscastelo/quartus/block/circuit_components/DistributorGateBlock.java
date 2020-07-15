package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DistributorGateBlock extends AbstractCircuitComponentBlock {
    public DistributorGateBlock() {
        super(Settings.copy(Blocks.REPEATER));
    }

    @Override
    public QuartusCircuitComponent getCircuitComponent() {
        return new QuartusCircuitComponent("DistributorGate") {
            @Override
            public void updateComponent() {
            }

            @Override
            public List<Direction> getPossibleInputDirections() {
                return DistributorGateBlock.this.getPossibleInputDirections();
            }

            @Override
            public List<Direction> getPossibleOutputDirections() {
                return DistributorGateBlock.this.getPossibleOutputDirections();
            }
        };
    }

    List<Direction> possibleInputDirections = Collections.singletonList(Direction.SOUTH);
    List<Direction> possibleOutputDirections = Arrays.asList(Direction.WEST, Direction.NORTH, Direction.EAST);
    @Override
    public List<Direction> getPossibleInputDirections() {
        return possibleInputDirections;
    }

    @Override
    public List<Direction> getPossibleOutputDirections() {
        return possibleOutputDirections;
    }
}

