package io.github.marcuscastelo.quartus.util;

import com.google.common.collect.ImmutableList;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class DirectionUtils {
    public static final ImmutableList<Direction> HORIZONTAL_DIRECTIONS = ImmutableList.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    @Nullable
    public static Direction getHorizontalDirectionAtoB(BlockPos posA, BlockPos posB) {
        BlockPos difference = posB.subtract(posA);

        //Se as posições são iguais
        if (difference.getX() == 0 && difference.getZ() == 0) return null;

        //Se a diferença é diagonal
        if (difference.getX() * difference.getZ() != 0) return null;

        //Se não estiverem adjacentes no plano horizontal
        if (Math.abs(difference.getX() + difference.getZ()) > 1) return null;

        Direction i = Direction.EAST;
        Direction j = Direction.SOUTH;

        if (difference.getZ() == 0) return (difference.getX()>0)?i:i.getOpposite();
        if (difference.getX() == 0) return (difference.getZ()>0)?j:j.getOpposite();

        return null;
    }

    @Nullable
    public static Direction getVerticalDirectionAtoB(BlockPos posA, BlockPos posB) {
        BlockPos difference = posB.subtract(posA);

        //Se as posições tiverem a mesma altura
        if (difference.getY() == 0) return null;

        //Se as posições não forem verticalmente adjacentes
        if (Math.abs(difference.getY()) > 1) return null;

        Direction k = Direction.UP;
        return (difference.getY()>0)?k:k.getOpposite();
    }
}
