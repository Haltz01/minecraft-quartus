package io.github.marcuscastelo.quartus.util;

import com.google.common.collect.ImmutableList;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Map;

/**
 * Classe que possui metodos uteis para o tratamento de direções
 */
public class DirectionUtils {
	//Variável lista que contém as direções horizontais possíveis para um bloco
    public static final ImmutableList<Direction> HORIZONTAL_DIRECTIONS = ImmutableList.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

	/**
	 * Método que retorna a direção horizontal,
	 * a partir da posição de um bloco até a posição de outro
	 * @param posA		Posição do primeiro bloco
	 * @param posB		Posição do segundo bloco
	 * @return		Direção encontrada
	 */
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

	/**
	 * Método que retorna a direção vertical,
	 * a partir da posição de um bloco até a posição de outro
	 * @param posA		Posição do primeiro bloco
	 * @param posB		Posição do segundo bloco
	 * @return		Direção encontrada
	 */
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
