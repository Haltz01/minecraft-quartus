package io.github.marcuscastelo.quartus.block.circuit_parts;

import io.github.marcuscastelo.quartus.block.QuartusInGameComponent;
import io.github.marcuscastelo.quartus.circuit.CircuitUtils;
import io.github.marcuscastelo.quartus.circuit.components.CircuitComponent;
import io.github.marcuscastelo.quartus.circuit.components.ComponentInfo;
import io.github.marcuscastelo.quartus.registry.QuartusCircuitComponents;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe que implementa o bloco de componente de circuito.
 * É um mero receptáculo, os blocos componentes são apenas carcaças.
 * Recebem {@link ComponentInfo} para determinar suas propriedades
 */
public class CircuitComponentBlock extends HorizontalFacingBlock implements QuartusInGameComponent {
    private final ComponentInfo componentInfo;

	/**
	 * Construtor padrão da classe CircuitComponentBlock
	 * Bem como o {@link WireBlock}, copia as propriedades do Repetidor de Redstone
	 * @see WireBlock#WireBlock()
	 * @param componentInfo		Informação do componente que está sendo criado nesse bloco
	 */
    public CircuitComponentBlock(ComponentInfo componentInfo) {
        super(Settings.copy(Blocks.REPEATER));
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
        this.componentInfo = componentInfo;
    }

	/**
	 * Método usado para criar instâncias do componente relacionado a este bloco
	 */
    @Override
    public CircuitComponent createCircuitComponent() {
        return componentInfo.componentSupplier.get();
    }

	/**
	 * Método que retorna o contorno do bloco que está 'na mira' do jogador
	 * @param state		Identifica o estado do bloco
	 * @param view		'Mundo' em que está o bloco
	 * @param pos		Posição do bloco 'mirado'
	 * @param context		Contexto da entidade que invocou o método
	 * @return		Retorna o contorno do bloco (outline)
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
	 * Verifica se o boco abaixo possui face quadrada no topo.
	 * @param state		Identifica o estado do bloco
	 * @param world		Mundo em que está sendo jogado
	 * @param pos		Posição do bloco no mundo
	 * @return		Retorna boolean que diz se é possível posicionar o bloco
	 */
    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState bottomBlockstate = world.getBlockState(pos.offset(Direction.DOWN));
        return bottomBlockstate.isSideSolidFullSquare(world,pos,Direction.UP);
    }

	/**
	 * Método que determina o blockstate após utilizar no método de atualizar os blocos de acordo com seus vizinhos
	 * @see CircuitComponentBlock#neighborUpdate(BlockState, World, BlockPos, Block, BlockPos, boolean)
	 * @param state		Identifica o estado do bloco após o método supracitado
	 * @param facing		Direção para o qual o bloco 'olha'
	 * @param neighborState		Estado do bloco vizinho
	 * @param world		Mundo que está sendo jogado
	 * @param pos		Posição do bloco no mundo
	 * @param neighborPos		Posição do bloco vizinho
	 * @return		BlockState do bloco em questão
	 */
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        return canPlaceAt(state, world, pos)? state: Blocks.AIR.getDefaultState();
    }

	/**
	 * Método que retorna uma lista de itens que serão derrubados
	 * @param state		Estado do bloco
	 * @param builder	Builder que configura as propriedades de drops
	 * @return		Lista com os itens a serem derrubados
	 */
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        return Collections.singletonList(new ItemStack(state.getBlock().asItem()));
    }

	/**
	 * Método que retorna o BlockState que determina as propriedades do bloco colocado no mundo
	 * @param ctx		Contexto em que o jogador colocou o bloco
	 * @return		BlockState do bloco após posicionar (virado para o lado que o jogador está olhando)
	 */
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

	/**
	 * Método que adiciona ao construtor de propriedades do bloco a propriedade de FACING (direção que 'olha')
	 * @param builder		Builder ao qual se adiciona FACING como propriedade do bloco
	 */
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

	/**
	 * Método que retorna uma lista com as direções absolutas (direções do mundo) das portas de input de um componente
	 */
    @Override
    public List<Direction> getPossibleInputDirections(Direction facingDirection) {
		return componentInfo.directionInfo.possibleInputDirections.stream().map(direction -> CircuitUtils.getAbsoluteDirection(facingDirection, direction)).collect(Collectors.toList());
    }

	/**
	 * Método que retorna uma lista com as direções absolutas (direções do mundo) das portas de output de um componente
	 */
    @Override
    public List<Direction> getPossibleOutputDirections(Direction facingDirection) {
		return componentInfo.directionInfo.possibleOutputDirections.stream().map(direction -> CircuitUtils.getAbsoluteDirection(facingDirection, direction)).collect(Collectors.toList());
    }
}
