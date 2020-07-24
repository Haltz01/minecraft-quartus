package io.github.marcuscastelo.quartus.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Interface que define a assinatura da função que retorna a direção que deve seguir
 * uma informação (entrada)
 */
public interface QuartusTransportInfoProvider {
	/**
	 * Assinatura de método que retorna qual direção o Mod deve seguir para passar informação adiante
	 * @param world		Mundo que está sendo jogado
	 * @param pos		Posição do bloco no mundo
	 * @param facingBefore		Direção que o bloco estava 'olhando'
	 * @return		Direção a ser seguida
	 */
    Direction nextDirection(World world, BlockPos pos, Direction facingBefore);
}