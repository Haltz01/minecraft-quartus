package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public interface QuartusInputConvertible extends QuartusNodeConvertible {
    QuartusInput createQuartusInput(World world, BlockPos pos);
    List<Direction> DIRECTIONS_NONE = new ArrayList<>();

    @Override
    default QuartusNode createQuartusNode(World world, BlockPos pos) {
        return createQuartusInput(world, pos);
    }

    @Override
    default List<Direction> getPossibleOutputDirections(World world, BlockPos pos) {
        return CircuitUtils.getHorizontalDirections();
    }

    @Override
    default List<Direction> getPossibleInputDirections(World world, BlockPos pos) {
        return DIRECTIONS_NONE;
    }
}
