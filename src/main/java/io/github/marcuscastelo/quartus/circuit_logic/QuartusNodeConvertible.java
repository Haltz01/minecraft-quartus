package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public interface QuartusNodeConvertible {
    QuartusNode createQuartusNode(World world, BlockPos pos) throws QuartusNode.QuartusWrongNodeBlockException;
    List<Direction> getPossibleInputDirections(World world, BlockPos pos);
    List<Direction> getPossibleOutputDirections(World world, BlockPos pos);
}
