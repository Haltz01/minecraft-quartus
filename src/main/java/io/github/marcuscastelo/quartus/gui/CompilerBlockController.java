package io.github.marcuscastelo.quartus.gui;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.marcuscastelo.quartus.blockentity.CompilerBlockEntity;
import io.github.marcuscastelo.quartus.circuit.CircuitDescriptor;
import io.github.marcuscastelo.quartus.circuit.CircuitCompiler;
import io.github.marcuscastelo.quartus.network.QuartusFloppyDiskUpdateC2SPacket;
import io.github.marcuscastelo.quartus.registry.QuartusItems;
import io.netty.buffer.Unpooled;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.function.Consumer;


/**
 * Classe que controla o GUI (Graphical User Interface - Interface Gráfico de Usuário)
 * do bloco Compiler
 */
public class CompilerBlockController extends CottonCraftingController {
	//Variável que armazena a posição do bloco Compiler
    private final BlockPos compilerBlockPosition;

    private final Consumer<Integer> onCompileAreaSizeSetted;
    private void setCompilingAreaSize(int newSize) {
        BlockEntity be = world.getBlockEntity(compilerBlockPosition);
        if (!(be instanceof CompilerBlockEntity)) return;
        ((CompilerBlockEntity) be).setCompilingAreaSize(newSize);
        if (onCompileAreaSizeSetted != null) onCompileAreaSizeSetted.accept(getCompilingAreaSize());
    }

    private int getCompilingAreaSize() {
        BlockEntity be = world.getBlockEntity(compilerBlockPosition);
        if (!(be instanceof CompilerBlockEntity)) return 0;
        return ((CompilerBlockEntity) be).getCompilingAreaSize();
    }

	/**
	 * Método que recebe um pacote de informações e atualiza o Disquete
	 * dentro do compilador para todos os jogadores
	 * @param floppyItemStack		Pilha de item no inventário do blcoo
	 * @param circuit		Circuito analisado
	 */
    private void updateFloppyDisk(ItemStack floppyItemStack, CircuitDescriptor circuit) {
        //Update client's itemstack
        floppyItemStack.getOrCreateTag().putString("circuit", circuit.serialize());

        //Send updated itemstack to server
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        QuartusFloppyDiskUpdateC2SPacket floppyDiskUpdateC2SPacket = new QuartusFloppyDiskUpdateC2SPacket(compilerBlockPosition, floppyItemStack);

        floppyDiskUpdateC2SPacket.write(buf);
        floppyDiskUpdateC2SPacket.send(buf);
    }

    //TODO: compile on server (mas achar os blocos no cliente for n³)
	/**
	 * Método que compila um circuito dentro de uma área
	 * @return		Circuito compilado
	 */
    private Optional<CircuitDescriptor> compileCircuit() {
        int size = getCompilingAreaSize();
        Direction facingDir = world.getBlockState(compilerBlockPosition).get(Properties.HORIZONTAL_FACING);
        BlockPos startPos = compilerBlockPosition.offset(facingDir.rotateYClockwise(), size /2).offset(facingDir.getOpposite(), size).offset(Direction.DOWN, size);
        BlockPos endPos = compilerBlockPosition.offset(facingDir.rotateYCounterclockwise(), size).offset(Direction.UP, size);

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
            Optional<CircuitDescriptor> circuit = compileCircuit();
            if (!circuit.isPresent()) return;
            updateFloppyDisk(floppyItemStack, circuit.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Construtor padrão da classe CompilerBlockController
	 * Adiciona uma interface ao compilador para facilitar o uso
	 * e deixa mais intuitivo seu uso
	 * @param syncId		Identificador ID do bloco
	 * @param playerInventory		Inventário do jogador
	 * @param blockInventory		Inventário do bloco
	 * @param compilerBlockPosition		Posição do bloco Compiler
	 */
    public CompilerBlockController(int syncId, PlayerInventory playerInventory, Inventory blockInventory, BlockPos compilerBlockPosition) {
        super(RecipeType.CRAFTING, syncId, playerInventory, blockInventory, null);
        this.compilerBlockPosition = compilerBlockPosition;

        WGridPanel root = new WGridPanel(1);
        setRootPanel(root);

        root.setSize(150,100);

        WTextField textField = new WTextField();
        textField.setTextPredicate(StringUtils::isNumeric);
        textField.setMaxLength(2);
        onCompileAreaSizeSetted = newSize -> textField.setText(String.valueOf(newSize));
        onCompileAreaSizeSetted.accept(getCompilingAreaSize());
        textField.setChangedListener(text -> {
            int currentCompilingAreaSize = getCompilingAreaSize();
            int newCompilingAreaSize = Integer.parseInt(text);
            if (currentCompilingAreaSize != newCompilingAreaSize) {
                setCompilingAreaSize(newCompilingAreaSize);
            }
        });
        root.add(textField, 110, 10, 20, 20);

        WItemSlot itemSlot = WItemSlot.of(blockInventory, 0);
        root.add(itemSlot, 20, 15);

        WButton compileButton = new WButton(new TranslatableText("gui.quartus.compiler.compile_btn"));
        compileButton.setOnClick(() -> {
            onCompilerButtonClick();
            textField.onFocusLost();
        });
        root.add(compileButton, 0, 40,  60, 20);

        WButton increaseButton = new WButton(new LiteralText("+"));
        increaseButton.setOnClick(() -> {
            setCompilingAreaSize(getCompilingAreaSize()+2);
            textField.onFocusLost();
        });
        root.add(increaseButton, 80, 0,  20, 20);

        WButton decreaseButton = new WButton(new LiteralText("-"));
        decreaseButton.setOnClick(() -> {
            setCompilingAreaSize(getCompilingAreaSize()-2);
            textField.onFocusLost();
        });
        root.add(decreaseButton, 80, 20,  20, 20);



        root.add(this.createPlayerInventoryPanel(),0,80);
        root.validate(this);
    }
}
