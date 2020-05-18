package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class QuartusNode {
    World world;
    BlockPos pos;
    public QuartusNode(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    public BlockState getBlockState() {
        return world.getBlockState(pos);
    }

    static List<Direction> HORIZONTAL_DIRECTIONS = Arrays.asList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    public boolean isDirectionInput(Direction direction) {
        return getInputDirections().contains(direction);
    }

    public abstract List<Direction> getOutputDirections();
    public abstract List<Direction> getInputDirections();

    @Override
    public String toString() {
        return this.getNodeType() + "{" +
                "pos=" + pos +
                '}';
    }

    public String getNodeType() {
        return "GenericNode";
    }
}
