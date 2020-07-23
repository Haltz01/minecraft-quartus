package io.github.marcuscastelo.quartus.gui;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.circuit.analyze.CircuitCompiler;
import io.github.marcuscastelo.quartus.network.QuartusFloppyDiskUpdateC2SPacket;
import io.github.marcuscastelo.quartus.registry.QuartusItems;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Classe que controla o GUI (Graphical User Interface - Interface Gráfico de Usuário)
 * do bloco Compiler
 */
public class CompilerBlockController extends CottonCraftingController {
	//Variável que armazena a posição do bloco Compiler
    final BlockPos compilerBlockPosition;

	/**
	 * Método que recebe um pacote de informações e atualiza o Disquete
	 * dentro do compilador para todos os jogadores
	 * @param floppyItemStack	->	Pilha de item no inventário do blcoo
	 * @param circuit	->	Circuito analisado
	 */
    private void updateFloppyDisk(ItemStack floppyItemStack, QuartusCircuit circuit) {
        System.out.println(circuit.serialize());

        //Update client's itemstack
        floppyItemStack.getOrCreateTag().putString("circuit", circuit.serialize());

        //Send updated itemstack to server
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        QuartusFloppyDiskUpdateC2SPacket floppyDiskUpdateC2SPacket = new QuartusFloppyDiskUpdateC2SPacket(compilerBlockPosition, floppyItemStack);

        floppyDiskUpdateC2SPacket.write(buf);
        floppyDiskUpdateC2SPacket.send(buf);
    }

	//TODO: support areas bigger than 10x10
	/**
	 * Método que compila um circuito dentro de uma área
	 * @return	->	Circuito compilado
	 */
    private QuartusCircuit compileCircuit() {
        Direction facingDir = world.getBlockState(compilerBlockPosition).get(Properties.HORIZONTAL_FACING);
        BlockPos startPos = compilerBlockPosition.offset(facingDir.rotateYClockwise(), 5).offset(facingDir.getOpposite(),10).offset(Direction.DOWN,5);
        BlockPos endPos = compilerBlockPosition.offset(facingDir.rotateYCounterclockwise(), 5).offset(Direction.UP, 5);

        System.out.println("Compiling from " + startPos + " to " + endPos);
        CircuitCompiler compiler = new CircuitCompiler(world, startPos, endPos);
        System.out.println("Começando compilação");
        return compiler.compile();
    }

	/**
	 * Método que faz a compilação mediante ao clique no botão
	 * do compilador
	 */
    private void onCompilerButtonClick() {
        ItemStack floppyItemStack = blockInventory.getInvStack(0);
        if (floppyItemStack.isEmpty() || !floppyItemStack.getItem().equals(QuartusItems.FLOPPY_DISK)) {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.sendMessage(new TranslatableText("gui.quartus.compiler.empty"));
            return;
        }
        try{
            QuartusCircuit circuit = compileCircuit();
            updateFloppyDisk(floppyItemStack, circuit);
            System.out.println(circuit.serialize());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

	/**
	 * Construtor padrão da classe CompilerBlockController
	 * Adiciona uma interface ao compilador para facilitar o uso
	 * e deixa mais intuitivo seu uso
	 * @param syncId	->	Identificador ID do bloco
	 * @param playerInventory	->	Inventário do jogador
	 * @param blockInventory	->	Inventário do bloco
	 * @param compilerBlockPosition	->	Posição do bloco Compiler
	 */
    public CompilerBlockController(int syncId, PlayerInventory playerInventory, Inventory blockInventory, BlockPos compilerBlockPosition) {
        super(RecipeType.CRAFTING, syncId, playerInventory, blockInventory, null);
        this.compilerBlockPosition = compilerBlockPosition;

        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        root.setSize(150,100);

        WItemSlot itemSlot = WItemSlot.of(blockInventory, 0);
        root.add(itemSlot, 1, 1);

        WButton compileButton = new WButton(new TranslatableText("gui.quartus.compiler.compile_btn"));
        compileButton.setOnClick(this::onCompilerButtonClick);
        root.add(compileButton, 0, 3,  3, 20);

        root.add(this.createPlayerInventoryPanel(),0,5);
        root.validate(this);
    }
}
