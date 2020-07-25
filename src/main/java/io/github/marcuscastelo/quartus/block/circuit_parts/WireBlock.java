package io.github.marcuscastelo.quartus.block.circuit_parts;

import io.github.marcuscastelo.quartus.block.QuartusTransportInfoProvider;
import io.github.marcuscastelo.quartus.registry.QuartusProperties;
import io.github.marcuscastelo.quartus.util.WireConnector;
import io.github.marcuscastelo.quartus.util.WireShapes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.apache.http.impl.conn.Wire;

import java.util.*;

/**
 * Classe que define o comportamento de um WireBlock
 */
public class WireBlock extends HorizontalFacingBlock implements QuartusTransportInfoProvider {
	/**
	 * Sub-Classe que determina o valor do fio
	 */
    public enum UpValue implements StringIdentifiable {
        NONE("none"), FACING("facing"), BOTH("both");

        String identifier;

        UpValue(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String asString() {
            return identifier;
        }
    }
	//Variáveis auxiliares que definem o estado do fio
    private static final BooleanProperty TURN = QuartusProperties.WIRE_TURN;
    private static final BooleanProperty POSITIVE = QuartusProperties.WIRE_POSITIVE;
    private static final EnumProperty<UpValue> UP = QuartusProperties.WIRE_UP;

	/**
	 * Construtor padrão da Classe WireBlock
	 */
    public WireBlock() {
        super(Settings.copy(Blocks.REPEATER));
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(TURN, false).with(POSITIVE, false).with(UP, UpValue.NONE));
    }

	/**
	 * Método que faz o updateNeighbor dos blocos quando um WireBlock é posicionado
	 * @param world		Mundo que está sendo jogado
	 * @param pos		Posição do bloco no Mundo
	 * @param state		BlockState do bloco em pos
	 * @param placer	Entidade viva (jogador) que posiciona o bloco
	 * @param itemStack		Pilha de itens do inventário do jogador
	 */
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.updateNeighbor(pos, state.getBlock(), pos);
    }

	/**
	 * Método que faz o neighborUpdate dos blocos ao redor do bloco recém-colocado
	 * @param state		BlockState do bloco posicionado
	 * @param world		Mundo que está sendo jogado
	 * @param currPos		Posição atual do bloco
	 * @param block		Bloco que causou neighborUpdate do bloco atual (que esta em pos)
	 * @param neighborPos		Posição do bloco que causou neighborUpdate
	 * @param moved		Boolean que verifica se o bloco foi destruído ou simplesmente movido
	 */
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos currPos, Block block, BlockPos neighborPos, boolean moved) {
        //O processamento é feito apenas no servidor
        if (world.isClient) return;

        //Se o fio atual já possui duas conexões, não precisa atualizar
        List<BlockPos> alreadyEstabilishedConnections = WireConnector.getWireEstabilishedConnections(world, currPos);
        if (alreadyEstabilishedConnections.size() == 2) {
            if (block != Blocks.END_PORTAL)
                WireConnector.updateUnnaturalNeighborsIfWires(world, currPos);
            return;
        }

        int freeConnectionSlotsCount = 2 - alreadyEstabilishedConnections.size();

        List<BlockPos> newConnectionList = new ArrayList<>();
        newConnectionList.addAll(alreadyEstabilishedConnections);
        newConnectionList.addAll(WireConnector.findConnectableQuartusBlocks(world, currPos, alreadyEstabilishedConnections, freeConnectionSlotsCount));

        WireConnector.connectTo(world, currPos, newConnectionList);

        //Atualiza os fios que podem estar em baixo ou em cima para detectar a existência desse novo fio (a não ser que isso já tenha acontecido: flag END_PORTAL)
        if (block != Blocks.END_PORTAL){
            WireConnector.updateUnnaturalNeighborsIfWires(world, currPos);
        }
    }

	/**
	 * Método que decide se um bloco pode ser posicionado, por meio de um boolean
	 * @param state		BlockState do bloco
	 * @param world		Mundo que está sendo jogado
	 * @param pos		Posição que se deseja posicionar o bloco
	 * @return		Boolean que diz se é possível colocar o bloco
	 */
    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState bottomBlockstate = world.getBlockState(pos.offset(Direction.DOWN));
        return bottomBlockstate.isSideSolidFullSquare(world,pos,Direction.UP);
    }

	/**
	 * Método que retorna um BlockState do bloco após decidir se
	 * é possível posicioná-lo no lugar desejado
	 * @param state		BlockState do bloco
	 * @param facing		Direção ao qual o bloco 'olha'
	 * @param neighborState		BlockState do bloco vizinho
	 * @param world		Mundo que está sendo jogado
	 * @param pos		Posição do bloco
	 * @param neighborPos		Posição do bloco vizinho
	 * @return		BlockState resultante (se coloca ou não)
	 */
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        return canPlaceAt(state, world, pos)? state: Blocks.AIR.getDefaultState();
    }

	/**
	 * Método que retorna uma lista com pilha de itens que cairão
	 * caso o item bloco seja destruído
	 * @param state		BlockState do bloco
	 * @param builder		Efeito de sorte ao destruir um bloco,
	 * 						com lista de blocos que podem ser derrubados
	 * @return		Lista de ItemStack que podem ser derrubados
	 */
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        return Collections.singletonList(new ItemStack(state.getBlock().asItem()));
    }


	/**
	 * Método que retorna a forma do bloco
	 * @param state		BlockState do bloco
	 * @param view		Visão/Mundo do bloco
	 * @param pos		Posição do bloco no Mundo
	 * @param context		Contexto da entidade do bloco (relativo ao seu estado)
	 * @return		Forma (shape) do bloco
	 */
    //TODO: arrumar de acordo com as novas mudanças
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        Direction facingDirection = state.get(FACING);
        Direction auxDirection = WireConnector.getAuxDirection(state);

        UpValue upValue = state.get(UP);

        VoxelShape totalShape;
        VoxelShape baseShape = WireShapes.getBaseShape(facingDirection, auxDirection);
        VoxelShape upShape = WireShapes.getUpVoxelShape(facingDirection, auxDirection, upValue);

        totalShape = VoxelShapes.union(baseShape, upShape);

        return totalShape;
    }

	/**
	 * Método que decide o que acontece quando um bloco é removido
	 * @param oldState		Estado anterior do bloco
	 * @param world		Mundo que está sendo jogado
	 * @param pos		Posição do bloco no Mundo
	 * @param newState		Novo estado do bloco
	 * @param moved		Boolean que verifica se o bloco foi destruído ou simplesmente movido
	 */
    @Override
    public void onBlockRemoved(BlockState oldState, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved) return;
        if (newState.getBlock() == this) return;
        int flagToAvoidEndlessLoop = 64 | 2;

        world.setBlockState(pos, oldState, flagToAvoidEndlessLoop);
        List<BlockPos> oldConnections = WireConnector.getWireEstabilishedConnections(world, pos);
        world.setBlockState(pos, newState, flagToAvoidEndlessLoop);
        WireConnector.updateUnnaturalNeighborsIfWires(world, pos, oldConnections);
        System.out.println(oldConnections);
    }

	/**
	 * Método que adiciona propriedades ao bloco
	 * @param builder		Adiciona propriedades ao bloco
	 */
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TURN, POSITIVE, UP, FACING);
    }

	/**
	 * Método que retorna o BlockState de um bloco de acordo com o Contexto
	 * @param ctx		Contexto do bloco (se está virado para o jogador)
	 * @return		BlockState do bloco
	 */
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

	/**
	 * Método que retorna a próxima direção a ser seguida
	 * @param world		Mundo que está sendo jogado
	 * @param pos		Posição do bloco no Mundo
	 * @param facingBefore		Direção ao qual o bloco 'olhava' anteiormente
	 * @return		Próxima direção a ser seguida
	 */
    @Override
    public Direction nextDirection(World world, BlockPos pos, Direction facingBefore) {
        //TODO: copiar do WireConnector
        return Direction.NORTH;
    }
}
