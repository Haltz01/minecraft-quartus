package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface QuartusInputConvertible extends QuartusNodeConvertible {
    QuartusInput createQuartusInput();
    List<Direction> DIRECTIONS_NONE = new ArrayList<>();

    @Override
    default QuartusNode createQuartusNode() {
        return createQuartusInput();
    }
    @Override
    default List<Direction> getPossibleOutputDirections(World world, BlockPos pos) {
        Direction facingDirection = world.getBlockState(pos).get(Properties.HORIZONTAL_FACING);
        return Arrays.asList(facingDirection);
    }

    @Override
    default List<Direction> getPossibleInputDirections(World world, BlockPos pos) {
        return DIRECTIONS_NONE;
    }
}
