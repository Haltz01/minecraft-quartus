package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusWorldNode;
import io.github.marcuscastelo.quartus.circuit_logic.real_nodes.XorGateNode;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class XorGateBlock extends AbstractGateBlock {
    @Override
    public QuartusNode createQuartusNode() {
        return new XorGateNode();
    }

    @Override
    public List<Direction> getPossibleOutputDirections(World world, BlockPos pos) {
        BlockState bs = world.getBlockState(pos);
        Direction facingDirection = bs.get(FACING);
        return Arrays.asList(facingDirection);
    }

    @Override
    public List<Direction> getPossibleInputDirections(World world, BlockPos pos) {
        BlockState bs = world.getBlockState(pos);
        Direction facingDirection = bs.get(FACING);
        return Arrays.asList(facingDirection.rotateYClockwise(), facingDirection.rotateYCounterclockwise());
    }
}
