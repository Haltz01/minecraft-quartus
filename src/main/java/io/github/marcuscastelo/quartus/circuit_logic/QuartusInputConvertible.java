package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface QuartusInputConvertible extends QuartusNodeConvertible {
    QuartusInput createQuartusInput(World world, BlockPos pos);

    @Override
    default QuartusNode createQuartusNode(World world, BlockPos pos) {
        return createQuartusInput(world, pos);
    }
}
