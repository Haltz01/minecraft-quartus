package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface QuartusNodeConvertible {
    QuartusNode createQuartusNode(World world, BlockPos pos);
}
