package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import io.github.marcuscastelo.quartus.circuit_logic.native_programs.NotGateNode;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class NotGateBlock extends AbstractGateBlock {
    @Override
    public List<Direction> getPossibleInputDirections(World world, BlockPos pos) {
        Direction facingDirection = world.getBlockState(pos).get(Properties.FACING);
        return Arrays.asList(facingDirection.getOpposite());
    }

    @Override
    public List<Direction> getPossibleOutputDirections(World world, BlockPos pos) {
        Direction facingDirection = world.getBlockState(pos).get(Properties.FACING);
        return Arrays.asList(facingDirection);
    }

    @Override
    public QuartusNode createQuartusNode(World world, BlockPos pos) throws QuartusNode.QuartusWrongNodeBlockException {
        return new NotGateNode(world, pos);
    }
}
