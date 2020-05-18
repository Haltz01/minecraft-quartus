package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface QuartusTransportInfoProvider {
    Direction nextDirection(World world, BlockPos pos, Direction facingBefore);
}
