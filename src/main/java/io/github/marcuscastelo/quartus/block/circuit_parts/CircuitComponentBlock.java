package io.github.marcuscastelo.quartus.block.circuit_parts;

import io.github.marcuscastelo.quartus.block.QuartusInGameComponent;
import io.github.marcuscastelo.quartus.circuit.CircuitUtils;
import io.github.marcuscastelo.quartus.circuit.components.CircuitComponent;
import io.github.marcuscastelo.quartus.circuit.components.ComponentInfo;
import io.github.marcuscastelo.quartus.registry.QuartusCircuitComponents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe que implementa as propriedades básicas dos blocos adicionados
 */
public class CircuitComponentBlock extends HorizontalFacingBlock implements QuartusInGameComponent {
	//Variável que armazena qual tipo de bloco está sendo analisado
	//Define qual tipo de bloco implementado será
    private final ComponentInfo componentInfo;

	/**
	 * Construtor padrão da classe CircuitComponentBlock
	 * @param componentInfo	->	Informação do bloco que está sendo feito
	 */
    public CircuitComponentBlock(ComponentInfo componentInfo) {
        super(Settings.copy(Blocks.REPEATER));
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
        this.componentInfo = componentInfo;
    }

	/**
	 * Método auxiliar que cria um objeto por meio de um ponteiro para a função
	 */
    @Override
    public CircuitComponent getCircuitComponent() {
        return componentInfo.componentSupplier.get();
    }

	/**
	 * Método que retorna o contorno do bloco que está 'na mira' do jogador
	 * @param state	->	Identifica o estado do bloco (energizado, dureza, etc)
	 * @param view	->	'Mundo' em que está o bloco
	 * @param pos	->	Posição do bloco 'mirado'
	 * @param context	->	Contexto em que o bloco se encontra (ambiente ao redor)
	 * @return	->	Retorna o contorno do bloco (hitbox)
	 */
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return VoxelShapes.cuboid(0f, 0.0f, 0f, 1f, 2/16f, 1f);
    }

	/**
	 * Método auxiliar que retorna o lado para o qual o bloco está virado
	 */
    @Override
    public Direction getFacingDirection(BlockState state) {
        return state.get(FACING);
    }

	/**
	 * Método auxiliar que define em quais tipos de lugares o bloco pode ser posicionado
	 * @param state	->	Identifica o estado do bloco (energizado, dureza, etc)
	 * @param world	->	Mundo em que está sendo jogado
	 * @param pos	->	Posição do bloco no mundo
	 * @return	->	Retorna boolean que diz se é possível posicionar o bloco
	 */
    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState bottomBlockstate = world.getBlockState(pos.offset(Direction.DOWN));
        return bottomBlockstate.isSideSolidFullSquare(world,pos,Direction.UP);
    }

	/**
	 * Método que retorna o BlockState para utilizar no método de atualizar os blocos de acordo com seus vizinhos
	 * @param state	->	Identifica o estado do bloco (energizado, dureza, etc)
	 * @param facing	->	Direção para o qual o bloco 'olha'
	 * @param neighborState	->	Estado do bloco vizinho
	 * @param world	->	Mundo que está sendo jogado
	 * @param pos	->	Posição do bloco no mundo
	 * @param neighborPos	->	Posição do bloco vizinho
	 * @return	->	BlockState do bloco em questão
	 */
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        return canPlaceAt(state, world, pos)? state: Blocks.AIR.getDefaultState();
    }

	/**
	 * Método que retorna uma lista de itens que foram derrubados
	 * @param state	->	Estado do bloco
	 * @param builder	->	Builder que configura as propriedades dos blocos
	 * @return	->	Lista com os itens a serem derrubados
	 */
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        return Collections.singletonList(new ItemStack(state.getBlock().asItem()));
    }

	/**
	 * Método que retorna o BlockState de um bloco recém posicionado
	 * @param ctx	->	Contexto em que o bloco se encontra
	 * @return	->	BlockState do bloco após posicionar (virado para o jogador)
	 */
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

	/**
	 * Método auxiliar que adiciona ao bloco a propriedade de FACING (direção que 'olha')
	 * @param builder	->	Builder que adiciona FACING às propriedades do bloco
	 */
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

	/**
	 * Método que retorna uma lista com as direções dos Inputs de um componente
	 */
    @Override
    public List<Direction> getPossibleInputDirections(Direction facingDirection) {
		return componentInfo.directionInfo.possibleInputDirections.stream().map(direction -> CircuitUtils.getAbsoluteDirection(facingDirection, direction)).collect(Collectors.toList());
    }

	/**
	 * Método que retorna uma lista com as direções dos Outputs de um componente
	 */
    @Override
    public List<Direction> getPossibleOutputDirections(Direction facingDirection) {
		return componentInfo.directionInfo.possibleOutputDirections.stream().map(direction -> CircuitUtils.getAbsoluteDirection(facingDirection, direction)).collect(Collectors.toList());
    }
}
