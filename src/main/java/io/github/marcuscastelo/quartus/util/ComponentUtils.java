package io.github.marcuscastelo.quartus.util;

import io.github.marcuscastelo.quartus.block.QuartusInGameComponent;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Classe auxiliar que verifica se há componentes do Mod e,
 * em seguida, se há entradas ou saídas disponíveis
 */
public class ComponentUtils {
	/**
	 * Método que verifica se há componentes do Mod e,
	 * em seguida, se há entradas ou saídas disponíveis
	 * @param world	->	Mundo que está sendo jogado
	 * @param pos	->	Posição do bloco no mundo
	 * @param inToOutDirection	->	Direção do Input ao Output
	 * @return	->	Boolean que confirma se há direções a seguir
	 */
    public static boolean isComponentConnectableAtDirection(World world, BlockPos pos, Direction inToOutDirection) {
        BlockState componentBs = world.getBlockState(pos);
        if (!(componentBs.getBlock() instanceof QuartusInGameComponent)) throw new IllegalArgumentException("Expecting a component position");
        Direction facingDirection = componentBs.get(Properties.HORIZONTAL_FACING);
        return ((QuartusInGameComponent) componentBs.getBlock()).getPossibleOutputDirections(facingDirection).contains(inToOutDirection) ||
                ((QuartusInGameComponent) componentBs.getBlock()).getPossibleInputDirections(facingDirection).contains(inToOutDirection);
    }
}
