package io.github.marcuscastelo.quartus.block;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.blockentity.CompilerBlockEntity;
import io.github.marcuscastelo.quartus.circuit.CircuitUtils;
import io.github.marcuscastelo.quartus.gui.CompilerBlockController;
import io.github.marcuscastelo.quartus.registry.QuartusCottonGUIs;
import io.github.marcuscastelo.quartus.registry.QuartusItems;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
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
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Classe que implementa o bloco Compilador.
 * Utilizado para interpretar o circuito criado e gerar um item Floppy Disk
 * que contém a lógica desenhada.
 */
public class CompilerBlock extends HorizontalFacingBlock implements BlockEntityProvider {
	/**
	 * Construtor padrão da classe.
	 * Coloca como características do bloco as propriedades de uma bigorna
	 * devido às semelhanças físicas que compartilham.
	 * Estado de orientação padrão - 'olhar' para o norte do mundo (campo não pode ser vazio)
	 */
    public CompilerBlock() {
        super(Settings.copy(Blocks.ANVIL));
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
    }

	/**
	 * Método que define as propriedades que o bloco designado terá.
	 * @param builder  Construtor de propriedades ao qual se especifica que o bloco criado terá
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
        return new CompilerBlockEntity();
    }

	/**
	 * Método chamado quando o jogador tenta usar o bloco presente no mundo (clicar com o botão direito do mouse)..
	 * @param state		Identifica o estado do bloco
	 * @param world		Mundo em que está sendo jogado
	 * @param pos		Posição do bloco no mundo
	 * @param player	Jogador que tentou usar o bloco
	 * @param hand		Mão que o jogador usou para ativar o bloco
	 * @param hit		Resultado de acertar um bloco
	 * @return		Retorna o efeito da ação de tentar usar um bloco no mundo
	 */
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    	//O processamento não precisa ser feito no cliente, apenas no servidor
        if (world.isClient) return ActionResult.SUCCESS;

        //Avisa o servidor que o jogador está abrindo um inventário de compilador
		ContainerProviderRegistry.INSTANCE.openContainer(Quartus.id("compiler"), player, packetByteBuf -> packetByteBuf.writeBlockPos(pos));

		//Indica para todos os jogadores que o jogador que abriu o compilador deve mexer a mão como sinal de interação bem sucedida
        return ActionResult.SUCCESS;
    }

	/**
	 * Método auxiliar que posiciona um bloco no mundo no caso especial do BlockItem possuir tags NBT.
	 * Caso o BlockItem contenha um FloppyDisk (disquete) válido dentro,
	 * posiciona o compilador com o disquete e seu circuito já no inventário.
	 * OBS: esse bloco com tag especial pode ser obtido no modo criativo por meio de CTRL+MOUSE0 (Ctrl + click) no compilador
	 * @param world		Mundo em que está sendo jogado
	 * @param pos		Posição do bloco no mundo
	 * @param compilerIS		Item/bloco Compiler com a tag especial presente no inventário do jogador
	 */
    private void handleBlockTagOnPlace(World world, BlockPos pos, ItemStack compilerIS) {
    	//Se o disquete for inválido, ignora a chamada
        if (compilerIS.getTag() == null) return;
        if (!compilerIS.getTag().contains("hasFloppy")) return;
        if (!compilerIS.getTag().getBoolean("hasFloppy")) return;

		/*
			Cria um novo o disquete com o circuito presente no BlockItem e
			o coloca no inventário do novo bloco Compiler
		 */
        Inventory inv = QuartusCottonGUIs.getBlockInventory(world, pos);
        ItemStack floppyIS = new ItemStack(QuartusItems.FLOPPY_DISK, 1);

        //Se o disquete possuir um circuito salvo, copie-o também.
        if (compilerIS.getTag().contains("floppyTag"))
            floppyIS.setTag(compilerIS.getTag().getCompound("floppyTag"));

		inv.setInvStack(0, floppyIS);
    }

	/**
	 * Método chamado quando um bloco Compiler é posicionado no mundo.
	 * @param world		Mundo em que está sendo jogado
	 * @param pos		Posição do bloco no mundo
	 * @param state		Identifica o estado do bloco (energizado, dureza, etc)
	 * @param placer	Identifica quem está colocando o bloco (próprio jogador, um monstro do jogo)
	 * @param compilerIS		Item Compiler presente no inventário da entidade que posiciona o bloco
	 */
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack compilerIS) {
    	//Trata o caso de o BlockItem conter tags do disquete previamente nele
        handleBlockTagOnPlace(world, pos, compilerIS);
    }

	/**
	 * Método que define o que ocorre quando o Compilador é removido/destruído.
	 * Caso tenha um FloppyDisk dentro do Compiler, o item será derrubado integralmente
	 * @param state		Identifica o estado do bloco
	 * @param world		Mundo em que está sendo jogado
	 * @param pos		Posição do bloco no mundo
	 * @param newState		Novo estado do bloco após a ação
	 * @param moved		Boolean que identifica se o bloco foi apenas movido ou de fato apagado
	 */
    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        //Dropa o disquete dentro do compilador em caso de este ser quebrado
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

			//Se o compilador estiver em condições normais (com blockentity na posição certa)
            if (blockEntity != null)
                ItemScatterer.spawn(world, pos, (Inventory)blockEntity);

            world.updateHorizontalAdjacent(pos, this);

            //Remove a BlockEntity que estava atrelada à posição atual do mundo
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
		return Collections.singletonList(new ItemStack(state.getBlock().asItem()));
	}

	/**
	 * Método chamado todos os ticks para renderizar efeitos especiais no bloco de compilador.
	 * É usado para renderizar uma partícula nas bordas do circuito
	 * @param state 	Estado do bloco
	 * @param world 	Mundo em que o bloco está
	 * @param pos 		Posição do bloco no mundo
	 * @param random 	Uma instância da classe Random usada para evitar que o bloco sempre seja atualizado
	 */
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!world.isClient) return;

		//Gera partículas nas bordas do circuito
		if (random.nextFloat() > 0.05f)
			CircuitUtils.outlineCompileRegionForClient(world, pos, CompilerBlockController.COMPILING_AREA_SIDE);
	}
}
