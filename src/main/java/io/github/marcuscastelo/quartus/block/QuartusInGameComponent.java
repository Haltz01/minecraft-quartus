package io.github.marcuscastelo.quartus.block;

import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

import java.util.List;

public interface QuartusInGameComponent {
    Direction getFacingDirection(BlockState state);
    QuartusCircuitComponent getCircuitComponent();
    List<Direction> getPossibleInputDirections();
    List<Direction> getPossibleOutputDirections();
}