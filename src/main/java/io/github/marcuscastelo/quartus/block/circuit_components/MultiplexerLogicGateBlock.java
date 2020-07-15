package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MultiplexerLogicGateBlock extends AbstractCircuitComponentBlock {
    public MultiplexerLogicGateBlock() {
        super(Settings.copy(Blocks.REPEATER));
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

    @Override
    public QuartusCircuitComponent getCircuitComponent() {
        return new QuartusCircuitComponent("MultiplexerGate") {
            @Override
            public void updateComponent() {

            }

            @Override
            public List<Direction> getPossibleInputDirections() {
                return MultiplexerLogicGateBlock.this.getPossibleInputDirections();
            }

            @Override
            public List<Direction> getPossibleOutputDirections() {
                return MultiplexerLogicGateBlock.this.getPossibleOutputDirections();
            }
        };
    }
}
