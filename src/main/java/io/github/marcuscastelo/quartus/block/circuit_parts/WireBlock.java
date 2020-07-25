package io.github.marcuscastelo.quartus.block.circuit_parts;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.*;

/**
 *  Classe que define um bloco de fio (WireBlock) e seu comportamento.
 *  O WireBlock serve para conectar dois componentes de um circuito entre si.
 *  Por clareza, foi decidido que não serão permitidas bifurcações nos fios, em vez disso, use {@link io.github.marcuscastelo.quartus.registry.QuartusBlocks#DISTRIBUTOR_GATE}.
 *  O fio pode se conectar a dois tipos de blocos: {@link CircuitComponentBlock} e {@link WireBlock}
 *  As conexões podem ser feitas em qualquer direção horizontal. O fio atualizará sua textura se houver curvas.
 *  Cada conexão pode ser feita na mesma altura, um bloco abaixo ou um bloco acima.
 *  No caso de conexões que sobem, é preciso que não hajam blocos sólidos tampando a conexão.
 *  Essas regras são definidas ao longo da classe {@link WireConnector}.
 *  Tentou-se manter o comportamento do fio o mais próximo da experiência padrão do jogo, de modo a não causar estranheza aos jogadores
 */
public class WireBlock extends HorizontalFacingBlock {
    //Apelido para as propriedade WIRE_TURN (diminuir código)
    private static final BooleanProperty TURN = QuartusProperties.WIRE_TURN;
    //Apelido para as propriedade WIRE_POSITIVE (diminuir código)
    private static final BooleanProperty POSITIVE = QuartusProperties.WIRE_POSITIVE;
    //Apelido para as propriedade WIRE_UP (diminuir código)
    private static final EnumProperty<QuartusProperties.UpValue> UP = QuartusProperties.WIRE_UP;

	/**
	 * Construtor padrão da Classe WireBlock.
     * Cria um "tipo" de bloco que copia as características de um repetidor, são elas:
     *  1 - O som ao colocar ou quebrar o fio (grupo de som da madeira).
     *  2 - O fato de que o fio quebra instantaneamente, com ou sem ferramentas.
     * Além disso, define o estado padrão de um bloco colocado no mundo (blockstate).
	 */
    public WireBlock() {
        super(Settings.copy(Blocks.REPEATER));
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(TURN, false).with(POSITIVE, false).with(UP, QuartusProperties.UpValue.NONE));
    }

	/**
	 * Define o comportamento do fio ao ser colocado no mundo.
     * Ao ser colocado, simula uma atualização de bloco em si mesmo
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

        //Se o fio atual já possui duas conexões, não precisa atualizar a si mesmo
        List<BlockPos> alreadyEstabilishedConnections = WireConnector.getWireEstabilishedConnections(world, currPos);
        if (alreadyEstabilishedConnections.size() == 2) {
            //Atualiza os fios aos quais é possível o fio atual se conectar para detectar a existência desse novo fio (a menos que isso já tenha acontecido: flag END_PORTAL)
            if (block != Blocks.END_PORTAL)
                WireConnector.updateUnnaturalNeighborsIfWires(world, currPos);
            return;
        }

        //Busca por novos blocos para se conectar e monta uma lista com todas as conexões
        int freeConnectionSlotsCount = 2 - alreadyEstabilishedConnections.size();
        List<BlockPos> newConnectionList = new ArrayList<>();
        newConnectionList.addAll(alreadyEstabilishedConnections);
        newConnectionList.addAll(WireConnector.findConnectableQuartusBlocks(world, currPos, alreadyEstabilishedConnections, freeConnectionSlotsCount));

        //Realiza a conexão efetiva deste fio com os fios acima determinados
        WireConnector.connectTo(world, currPos, newConnectionList);

        //Atualiza os fios aos quais é possível o fio atual se conectar para detectar a existência desse novo fio (a menos que isso já tenha acontecido: flag END_PORTAL)
        if (block != Blocks.END_PORTAL){
            WireConnector.updateUnnaturalNeighborsIfWires(world, currPos);
        }
    }

	/**
	 * Método que decide se um fio pode ser colocado no mundo, dado o contexto.
     * Se o bloco sobre o qual se deseja colocar o fio for um bloco cuja face superior é um quadrado sólido, pode colocar.
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
	 * Método que retorna um BlockState do bloco após uma atualização de bloco.
     * Esse método avalia se o bloco em baixo do fio foi quebrado.
     * Em caso positivo, destrua o fio, trocando-o por ar.
     * Em caso negativo, mantenha o estado atual.
	 * @param state		Estado atual do fio
	 * @param facing		Direção ao qual o fio 'olha'
	 * @param neighborState		BlockState do bloco que iniciou a atualização de bloco anterior
	 * @param world		Mundo que está sendo jogado
	 * @param pos		Posição do fio
	 * @param neighborPos		Posição do bloco que iniciou a atualização de bloco anterior
	 * @return		BlockState resultante (se coloca ou não)
	 */
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        return canPlaceAt(state, world, pos)? state: Blocks.AIR.getDefaultState();
    }

	/**
	 * Método que retorna uma lista com pilha de itens que cairão
	 * caso o fio seja destruído
	 * @param state		BlockState do fio
	 * @param builder	Contexto que provê informações como sorte do jogador (não é utilizado para o WireBlock)
	 * @return		    Lista de ItemStack que serão derrubados
	 */
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        return Collections.singletonList(new ItemStack(state.getBlock().asItem()));
    }


	/**
	 * Método que retorna a forma do bloco (contorno preto usado para determinar se o jogador está olhando para o bloco)
	 * @param state		    BlockState do fio
	 * @param view		    Mundo em que se está jogando
	 * @param pos		    Posição do bloco no Mundo
	 * @param context		Contexto da entidade do bloco (relativo ao seu estado)
	 * @return		        Forma (shape) do bloco
	 */
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        Direction facingDirection = state.get(FACING);
        Direction auxDirection = WireConnector.getAuxDirection(state);

        QuartusProperties.UpValue upValue = state.get(UP);

        VoxelShape totalShape;

        //Cria uma forma incompleta, representando a base do fio, (direções horizontais)
        VoxelShape baseShape = WireShapes.getBaseShape(facingDirection, auxDirection);

        //Cria uma forma incompleta, representando apenas a forma que sobe a parede do bloco de apoio da direção dada.
        VoxelShape upShape = WireShapes.getUpVoxelShape(facingDirection, auxDirection, upValue);

        //Une as formas incompletas na representação total do fio.
        totalShape = VoxelShapes.union(baseShape, upShape);

        return totalShape;
    }

	/**
	 * Método que decide o que acontece quando um bloco é removido
	 * @param oldState		Estado anterior do bloco (antes de ser removido)
	 * @param world		Mundo que está sendo jogado
	 * @param pos		Posição do bloco no Mundo
	 * @param newState		Novo estado do bloco
	 * @param moved		Boolean que verifica se o bloco foi destruído ou simplesmente movido
	 */
    @Override
    public void onBlockRemoved(BlockState oldState, World world, BlockPos pos, BlockState newState, boolean moved) {
        /*
            Passos:

            1 - Determina uma flag de atualização de blocos (explicações mais a frente)
            2 - Define o estado atual como o estado antigo (necessário para o passo 3)
            3 - Obtém as conexões estabelecidas do estado atual
            4 - Reverte o estado atual para o estado novo (antes de ser mudado manualmente no passo 2)
            5 - Atualiza os fios que estavam conectados aos fios

            No passo 2 e 4, o jogo por padrão já notifica os vizinhos imediatos (isto é, dos blocos adjacentes sem contar diagonais).
            Isso por si só é um problema de performance, mas o grande problema é que ele também chama a função onBlockRemoved para o fio atual, o que causaria
            um loop infinito.
            Para evitar isso, li o código fonte do jogo e percebi que existem flags numéricas que evitam a atualização de blocos vizinhos
            e para evitar o loop infinito, eu finjo que o bloco foi movido por um pistão (parâmetro moved), já que o bloco nunca vai ser legitimamente movido por um pistão, dado suas características.
         */

        //Se etiver para entrar em um loop infinito, corta o loop, como descrito acima
        if (moved) return;

        //Se a chamada tiver vindo recursivamente do passo 2, ignora
        if (newState.getBlock() == this) return;

        //Passo 1:
        int flagToAvoidEndlessLoop = 64 | 2;

        //Passo 2:
        world.setBlockState(pos, oldState, flagToAvoidEndlessLoop);

        //Passo 3:
        List<BlockPos> oldConnections = WireConnector.getWireEstabilishedConnections(world, pos);

        //Passo 4:
        world.setBlockState(pos, newState, flagToAvoidEndlessLoop);

        //Passo 5:
        WireConnector.updateUnnaturalNeighborsIfWires(world, pos, oldConnections);
    }

	/**
	 * Método que determina as propriedades ao bloco
	 * @param builder		Construtor de propriedades do bloco, dado pelo jogo para ser alimentado
	 */
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TURN, POSITIVE, UP, FACING);
    }

	/**
	 * Método que determina o estado do bloco ao ser colocado por meio de seu item.
     * A determinação leva em conta a direção em que o player olha.
	 * @param ctx		Contexto do colocação do BlockItem
	 * @return		    Estado determinado
	 */
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
    }
}
