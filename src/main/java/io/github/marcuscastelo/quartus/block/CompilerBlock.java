package io.github.marcuscastelo.quartus.block;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.blockentity.CompilerBlockEntity;
import io.github.marcuscastelo.quartus.circuit.CircuitUtils;
import io.github.marcuscastelo.quartus.registry.QuartusCottonGUIs;
import io.github.marcuscastelo.quartus.registry.QuartusItems;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

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
	 * @param builder -> Especifica que o bloco criado terá
	 * 					como propriedade FACING -> orientação no mundo
	 */
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

	/**
	 * Método que retorna o estado do bloco (blockState) quando posicionado no mundo.
	 * Quando posicionar, o bloco deve estar virado para o jogador que o colocou.
	 * @param ctx -> contexto em que o bloco é posicionado
	 * 					Identifica a direção do jogador,
	 * 					posicionando-o para 'encará-lo'
	 * @return	->	retorna o blockState
	 */
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

	/**
	 * Método que cria o BlockEntity do bloco, que define suas características
	 * e funções, armazenando os dados dentro do bloco.
	 * @param view	->	'Mundo' que cria a BlockEntity
	 * @return	->	Retorna a BlockEntity criada
	 */
    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new CompilerBlockEntity();
    }

	/**
	 * Método chamado quando o jogador tenta usar o bloco (clicar com o botão direito do mouse).
	 * Pode ser um bloco presente no mundo ou que o jogador esté segurando.
	 * @param state	->	Identifica o estado do bloco (energizado, dureza, etc)
	 * @param world	->	Mundo em que está sendo jogado
	 * @param pos	->	Posição do bloco no mundo
	 * @param player->	Jogador que tentou usar o bloco
	 * @param hand	->	Mão que o jogador usou para ativar o bloco
	 * @param hit	->	Resultado de acertar um bloco
	 * @return	->	Retorna o efeito da ação de tentar usar um bloco, na mão ou no mundo
	 */
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        ContainerProviderRegistry.INSTANCE.openContainer(Quartus.id("compiler"), player, packetByteBuf -> packetByteBuf.writeBlockPos(pos));

        return ActionResult.SUCCESS;
    }

	/**
	 * Método auxiliar posicionar o bloco no mundo.
	 * Caso o bloco já contenha um FloppyDisk (disquete) dentro,
	 * posiciona o compilador com o disquete e seu circuito.
	 * @param world	->	Mundo em que está sendo jogado
	 * @param pos	->	Posição do bloco no mundo
	 * @param compilerIS	->	Item/bloco Compiler presente no inventário do jogador
	 */
    private void handleBlockTagOnPlace(World world, BlockPos pos, ItemStack compilerIS) {
        if (compilerIS.getTag() == null) return;
        if (!compilerIS.getTag().contains("hasFloppy")) return;

        if (!compilerIS.getTag().getBoolean("hasFloppy")) return;

		//'Cria' o disquete e atrela ao BlockEntity do novo Compiler no inventário do jogador
        Inventory inv = QuartusCottonGUIs.getBlockInventory(world, pos);
        ItemStack floppyIS = new ItemStack(QuartusItems.FLOPPY_DISK, 1);
        if (compilerIS.getTag().contains("floppyTag"))
            floppyIS.setTag(compilerIS.getTag().getCompound("floppyTag"));

        assert inv != null;
        inv.setInvStack(0, floppyIS);
    }

	/**
	 * Método chamado quando um bloco Compiler é posicionado no mundo.
	 * @param world	->	Mundo em que está sendo jogado
	 * @param pos	->	Posição do bloco no mundo
	 * @param state	->	Identifica o estado do bloco (energizado, dureza, etc)
	 * @param placer->	Identifica quem está colocando o bloco (próprio jogador, um monstro do jogo)
	 * @param compilerIS	->	Item Compiler presente no inventário da entidade que posiciona o bloco
	 */
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack compilerIS) {
        handleBlockTagOnPlace(world, pos, compilerIS);
        CircuitUtils.outlineCompileRegionForClient(world, pos, 10, Blocks.DIRT);
    }

	/**
	 * Método que define o que ocorre quando o Compilador é removido/destruído.
	 * Caso tenha um FloppyDisk dentro do Compiler, o item será derrubado integralmente
	 * @param state	->	Identifica o estado do bloco (energizado, dureza, etc)
	 * @param world	->	Mundo em que está sendo jogado
	 * @param pos	->	Posição do bloco no mundo
	 * @param newState	->	Novo estado do bloco após a ação
	 * @param moved	->	Boolean que identifica se o bloco foi simplesmente movido ou de fato apagado
	 */
    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        //TODO: make it work
//        CircuitUtils.outlineCompileRegionForClient(world, pos, 10, Blocks.COAL_BLOCK);
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

			//Se houve um FloppyDisk, derruba-o no mundo
            if (blockEntity != null)
                ItemScatterer.spawn(world, pos, (Inventory)blockEntity);

            world.updateHorizontalAdjacent(pos, this);

            super.onBlockRemoved(state, world, pos, newState, moved);
        }
    }

	/**
	 * Método auxiliar para copiar um bloco CompilerBlock já posicionado no mundo.
	 * Caso o bloco já contenha um FloppyDisk (disquete) dentro,
	 * copia o disquete e seu circuito para a BlockEntity
	 * do compilador presente no inventário do jogador.
	 * Quando posicionar o compilador copiado no mundo, já conterá o disquete
	 * e o circuito do bloco original (copiado).
	 * @param world	->	Mundo em que está sendo jogado
	 * @param pos	->	Posição do bloco no mundo
	 * @param state	->	Identifica o estado do bloco (energizado, dureza, etc)
	 * @return	->	ItemStack copiado do mundo
	 */
    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        if (!(world instanceof World)) {
            return new ItemStack(this);
        }
        Inventory inv = QuartusCottonGUIs.getBlockInventory((World)world, pos);
        ItemStack floppyIS = inv.getInvStack(0);

        if (floppyIS.isEmpty()) return new ItemStack(this);

        ItemStack compilerIS = new ItemStack(this, 1);
        CompoundTag floppyTag = floppyIS.getTag();
        compilerIS.getOrCreateTag().putBoolean("hasFloppy", true);
        if (floppyTag == null) return compilerIS;

        compilerIS.getOrCreateTag().put("floppyTag", floppyTag);
        return compilerIS;
    }
}
