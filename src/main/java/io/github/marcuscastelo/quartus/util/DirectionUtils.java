package io.github.marcuscastelo.quartus.util;

import com.google.common.collect.ImmutableList;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.function.Function;

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

    /**
     * Método auxiliar que retorna uma função com a direção a seguida,
     * traduzindo para o jogo entender qual a direção desejada
     * @param facingDir		Direção de referência
     * @return		Função que diz qual direção seguir
     */
    private static Function<Direction, Direction> getRotationFunction(Direction facingDir) {
        if (facingDir == Direction.NORTH) return direction -> direction;
        else if (facingDir == Direction.EAST) return Direction::rotateYClockwise;
        else if (facingDir == Direction.SOUTH) return Direction::getOpposite;
        else if (facingDir == Direction.WEST) return Direction::rotateYCounterclockwise;
        else throw new IllegalArgumentException("Unknown direction: " + facingDir);
    }

    /**
     * Método chamado para receber a direção que um bloco olha no jogo
     * @param facingDir		Direção que o bloco 'mira'
     * @param absoluteDireciton		Direção absoluta (em relação ao norte do mundo)
     * @return		Direção relativa (em relação ao norte do bloco)
     */
    public static Direction getRelativeDirection(Direction facingDir, Direction absoluteDireciton) {
        Function<Direction, Direction> rotationFunction = getRotationFunction(facingDir);

        //Para reverter uma conversão Relativa -> Absoluta, basta executar 3 vezes a transformação novamente
        Direction relativeDirection = absoluteDireciton;
        for (int i = 0; i < 3; i++)
            relativeDirection = rotationFunction.apply(relativeDirection);
        return relativeDirection;
    }

    /**
     * Método chamado para receber a direção para o qual um bloco está olhando
     * em relação ao jogo
     * @param facingDir		Direção que o bloco 'mira'
     * @param relativeDirection		Direção relativa à direção do bloco
     * @return		Direção absoluta (em relação ao norte do mundo)
     */
    public static Direction getAbsoluteDirection(Direction facingDir, Direction relativeDirection) {
        Function<Direction, Direction> rotationFunction = getRotationFunction(facingDir);
        return rotationFunction.apply(relativeDirection);
    }

    public Vec3i decomposeDirection(Direction direction) {
        if (direction.equals(Direction.NORTH)) return new Vec3i(0, 0, -1);
        if (direction.equals(Direction.SOUTH)) return new Vec3i(0, 0, 1);
        if (direction.equals(Direction.WEST)) return new Vec3i(-1, 0, 0);
        if (direction.equals(Direction.EAST)) return new Vec3i(1, 0, 0);
        if (direction.equals(Direction.UP)) return new Vec3i(0, 1, 0);
        if (direction.equals(Direction.DOWN)) return new Vec3i(0, -1, 0);
        throw new IllegalArgumentException("Direction " + direction + " not supported");
    }

    public Vec3i multiplyEveryCoordinate(Vec3i baseVec, Vec3i selectorVec) {
        return new Vec3i(baseVec.getX() * selectorVec.getX(), baseVec.getY() * selectorVec.getY(), baseVec.getZ() * selectorVec.getZ());
    }
}
