package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import io.github.marcuscastelo.quartus.circuit_logic.real_nodes.MultiplexerGateNode;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class MultiplexerGateBlock extends AbstractGateBlock{
    @Override
    public QuartusNode createQuartusNode(World world, BlockPos pos) throws QuartusNode.QuartusWrongNodeBlockException {
        return new MultiplexerGateNode(world, pos);
    }

    @Override
    public List<Direction> getPossibleInputDirections(World world, BlockPos pos) {
        BlockState bs = world.getBlockState(pos);
        Direction facingDirection = bs.get(FACING);
        return Arrays.asList(facingDirection.rotateYClockwise(), facingDirection.rotateYCounterclockwise(), facingDirection.getOpposite());
    }

    @Override
    public List<Direction> getPossibleOutputDirections(World world, BlockPos pos) {
        BlockState bs = world.getBlockState(pos);
        Direction facingDirection = bs.get(FACING);
        return Arrays.asList(facingDirection);
    }
}
