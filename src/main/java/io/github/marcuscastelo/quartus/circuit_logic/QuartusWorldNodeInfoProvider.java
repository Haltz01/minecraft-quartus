package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public interface QuartusWorldNodeInfoProvider {
    Direction getFacingDirection(BlockState state);
}
