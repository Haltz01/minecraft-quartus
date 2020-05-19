package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusNodeConvertible;
import io.github.marcuscastelo.quartus.circuit_logic.native_programs.AndGateNode;
import io.github.marcuscastelo.quartus.circuit_logic.native_programs.OrGateNode;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class OrGateBlock extends AbstractGateBlock implements QuartusNodeConvertible {
    @Override
    public QuartusNode createQuartusNode(World world, BlockPos pos) {
        BlockState bs = world.getBlockState(pos);
        Direction facingDirection = bs.get(FACING);
        return new OrGateNode(world, pos) {
            @Override
            public List<Direction> getPossibleOutputDirections() {
                return Arrays.asList(facingDirection);
            }

            @Override
            public List<Direction> getPossibleInputDirections() {
                return Arrays.asList(facingDirection.rotateYClockwise(), facingDirection.rotateYCounterclockwise());
            }
        };
    }
}
