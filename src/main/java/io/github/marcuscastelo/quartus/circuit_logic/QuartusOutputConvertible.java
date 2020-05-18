package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface QuartusOutputConvertible extends QuartusNodeConvertible {
    QuartusOutput createQuartusOutput(World world, BlockPos pos);

    @Override
    default QuartusNode createQuartusNode(World world, BlockPos pos) {
        return createQuartusOutput(world, pos);
    }
}
