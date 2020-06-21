package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class QuartusWorldNode extends QuartusNode {
    private World world;
    private BlockPos pos;
    private BlockState blockState;
    private QuartusWorldNodeInfoProvider infoProviderBlock;

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public QuartusWorldNode(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
        this.blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof QuartusWorldNodeInfoProvider)
            this.infoProviderBlock = (QuartusWorldNodeInfoProvider) blockState.getBlock();
        else throw new RuntimeException("Trying to create a node at a non-node block: "+ blockState.getBlock() + "@"+pos);
    }

    private Function<Direction, Direction> getRotationFunction(Direction facingDir) {
        if (facingDir == Direction.NORTH) return direction -> direction;
        else if (facingDir == Direction.EAST) return Direction::rotateYClockwise;
        else if (facingDir == Direction.SOUTH) return Direction::getOpposite;
        else if (facingDir == Direction.WEST) return Direction::rotateYCounterclockwise;
        else throw new IllegalArgumentException("Unknown direction: " + facingDir);
    }

    public final List<Direction> getAbsoluteInputDirections() {
        Direction facingDir = infoProviderBlock.getFacingDirection(blockState);
        Function<Direction, Direction> rotationFunction = getRotationFunction(facingDir);
        return getRelativeInputDirections().stream().map(rotationFunction).collect(Collectors.toList());
    }

    public final List<Direction> getAbsoluteOutputDirections() {
        Direction facingDir = infoProviderBlock.getFacingDirection(blockState);
        Function<Direction, Direction> rotationFunction = getRotationFunction(facingDir);
        return getRelativeOutputDirections().stream().map(rotationFunction).collect(Collectors.toList());
    }
}
