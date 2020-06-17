package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface QuartusOutputConvertible extends QuartusNodeConvertible {
    QuartusInput createQuartusInput(World world, BlockPos pos) throws QuartusNode.QuartusWrongNodeBlockException;
    List<Direction> DIRECTIONS_NONE = new ArrayList<>();

    @Override
    default QuartusNode createQuartusNode(World world, BlockPos pos) throws QuartusNode.QuartusWrongNodeBlockException {
        return createQuartusInput(world, pos);
    }

    @Override
    default List<Direction> getPossibleOutputDirections(World world, BlockPos pos) {
        return DIRECTIONS_NONE;
    }

    @Override
    default List<Direction> getPossibleInputDirections(World world, BlockPos pos) {
        final Direction facingDirection = world.getBlockState(pos).get(Properties.FACING);
        return Arrays.asList(facingDirection.getOpposite());
    }
}
