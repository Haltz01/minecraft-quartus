package io.github.marcuscastelo.quartus.block;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.blockentity.ExecutorBlockEntity;
import io.github.marcuscastelo.quartus.registry.QuartusBlocks;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
/**
 * Classe ExecutorBlock, que define o bloco que simula a execução do circuito montado
 */
public class ExecutorBlock extends HorizontalFacingBlock implements BlockEntityProvider {

	/**
	 * Construtor padrão da classe.
	 * Coloca como características do bloco as propriedades de uma bigorna
	 * devido às semelhanças físicas que compartilham.
	 * Estado de orientação padrão - 'olhar' para o norte do mundo (campo não pode ser vazio).
	 */
    public ExecutorBlock() {
        super(Settings.copy(Blocks.ANVIL));
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
    }

	/**
	 * Método chamado quando o jogador tenta usar o bloco (clicar com o botão direito do mouse).
	 * @param state		Identifica o estado do bloco (energizado, dureza, etc)
	 * @param world		Mundo em que está sendo jogado
	 * @param pos		Posição do bloco no mundo
	 * @param player	Jogador que tentou usar o bloco
	 * @param hand		Mão que o jogador usou para ativar o bloco
	 * @param hit		Resultado de acertar um bloco
	 * @return		Retorna o efeito da ação de tentar usar um bloco, na mão ou no mundo
	 */
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        //TODO: verificar se não foi sobreposto peo cliente por outro bloco (causa crash)

        ContainerProviderRegistry.INSTANCE.openContainer(Quartus.id("executor"), player, packetByteBuf -> packetByteBuf.writeBlockPos(pos));

        return ActionResult.SUCCESS;
    }

	/**
	 * Método auxiliar que simula o clock (tick) do bloco durante a execução
	 * O método agenda o tempo de duração entre os tick's
	 * @param state		Identifica o estado do bloco (energizado, dureza, etc)
	 * @param world		Mundo em que está sendo jogado
	 * @param pos		Posição do bloco no mundo
	 * @param random	Classe Random nativa java, mas não utilizada no método
	 */
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ExecutorBlockEntity)
            ((ExecutorBlockEntity) be).tick();
    }

	/**
	 * Método que define as propriedades que o bloco designado terá.
	 * @param builder  Especifica que o bloco criado terá
	 * 					como propriedade FACING -> orientação no mundo
	 */
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

	/**
	 * Método que retorna o estado do bloco (blockState) quando posicionado no mundo.
	 * Quando posicionar, o bloco deve estar virado para o jogador que o colocou.
	 * @param ctx  contexto em que o bloco é posicionado
	 * 					Identifica a direção do jogador,
	 * 					posicionando-o para 'encará-lo'
	 * @return		retorna o blockState
	 */
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

	/**
	 * Método que cria o BlockEntity do bloco, que define suas características
	 * e funções, armazenando os dados dentro do bloco.
	 * @param view		'Mundo' que cria a BlockEntity
	 * @return		Retorna a BlockEntity criada
	 */
    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new ExecutorBlockEntity();
    }

	/**
	 * Método que define o que ocorre quando o bloco ExecutorBlock é destruído/removido
	 * Caso tenha um FloppyDisk dentro do Compiler, o item será derrubado integralmente
	 * @param state		Identifica o estado do bloco (energizado, dureza, etc)
	 * @param world		Mundo em que está sendo jogado
	 * @param pos		Posição do bloco no mundo
	 * @param newState		Novo estado do bloco após a ação
	 * @param moved		Boolean que identifica se o bloco foi simplesmente movido ou de fato apagado
	 */
    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        //Dropa o disquete dentro do executor em caso de este ser quebrado
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

			//Se não for uma entidade nula na posição fornecida, derruba os itens contidos
            if (blockEntity != null)
                ItemScatterer.spawn(world, pos, (Inventory)blockEntity);

            world.updateHorizontalAdjacent(pos, this);

            super.onBlockRemoved(state, world, pos, newState, moved);
        }
    }

	/**
	 * Método que retorna uma lista de itens que foram derrubados
	 * @param state		Estado do bloco
	 * @param builder		Builder que configura as propriedades dos blocos
	 * @return		Lista com os itens a serem derrubados
	 */
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		return Arrays.asList(new ItemStack(state.getBlock().asItem()));
	}

	/**
	 * Método chamado quando um outro bloco é posicionado/alterado nas posições vizinhas do ExecutorBlock
	 * Caso seja um bloco que não faz parte do sistema de execução dos circuitos, não faz nada
	 * Se fizer parte da execução, escaneia toda a sequência e decide mudanças na sequência
	 * @param state		Identifica o estado do bloco (energizado, dureza, etc)
	 * @param world		Mundo em que está sendo jogado
	 * @param pos		Posição do bloco no mundo
	 * @param previousBlock		Bloco anterior (posição) ao bloco em pos
	 * @param neighborPos		Posição do bloco vizinho
	 * @param moved		Boolean que verifica se o bloco foi modificado/colocado ou simplesmente movido
	 */
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block previousBlock, BlockPos neighborPos, boolean moved) {
        BlockEntity be = world.getBlockEntity(pos);
        boolean IOChainHasChanged = previousBlock == QuartusBlocks.EXTENSOR_IO || previousBlock == Blocks.END_PORTAL;

        if (!(be instanceof ExecutorBlockEntity)) return;
        ExecutorBlockEntity executorBe = (ExecutorBlockEntity) be;

        if (IOChainHasChanged) executorBe.chainChanged();
    }
}
