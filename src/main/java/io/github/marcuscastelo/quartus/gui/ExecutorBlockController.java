package io.github.marcuscastelo.quartus.gui;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.marcuscastelo.quartus.block.ExecutorIOBlock;
import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.network.QuartusExecutorStartC2SPacket;
import io.github.marcuscastelo.quartus.network.QuartusExtensorIOUpdateC2SPacket;
import io.github.marcuscastelo.quartus.registry.QuartusBlocks;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
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
 * do bloco Executor
 */
public class ExecutorBlockController extends CottonCraftingController {
	//Variável que armazena a posição do bloco Executor
    BlockPos executorBlockPos;

	/**
	 * Método que atualiza os extensores I/O do Executor
	 * Recebe um pacote de dados, referente ao circuito
	 * e atribui a quantidade correta de inputs e outputs
	 * para a sequência de blocos à direita do Executor
	 * @param circuit		Circuito desenvolvido com blocos do Mod
	 */
    private void updateExtensorModels(QuartusCircuit circuit) {
        int nInputs = circuit.getInputCount();
        int nOutputs = circuit.getOutputCount();

        Direction exploreDirection = world.getBlockState(executorBlockPos).get(Properties.HORIZONTAL_FACING).rotateYCounterclockwise();
        BlockPos extensorPos = executorBlockPos.offset(exploreDirection);
        BlockState extensorState = world.getBlockState(extensorPos);
        int distance = 1;
        while (extensorState.getBlock().equals(QuartusBlocks.EXTENSOR_IO)) {
            ExecutorIOBlock.ExecutorIOState newExtensorState;
            if (distance <= nInputs && distance <= nOutputs) {
                newExtensorState =  ExecutorIOBlock.ExecutorIOState.IO;
            } else if (distance <= nInputs) {
                newExtensorState = ExecutorIOBlock.ExecutorIOState.INPUT;
            } else if (distance <= nOutputs) {
                newExtensorState = ExecutorIOBlock.ExecutorIOState.OUPUT;
            } else break;

            //Define os blocos no cliente
            world.setBlockState(extensorPos, extensorState.with(ExecutorIOBlock.EXTENSOR_STATE, newExtensorState));

            //Envia as alterações para o servidor
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            QuartusExtensorIOUpdateC2SPacket packet = new QuartusExtensorIOUpdateC2SPacket(extensorPos, newExtensorState);
            packet.write(buf);
            packet.send(buf);

            distance++;

            extensorPos = extensorPos.offset(exploreDirection);
            extensorState = world.getBlockState(extensorPos);
        }
    }

	/**
	 * Método que faz a execução do circuito fornecido ao executor,
	 * mediante ao clique no botão da interface do executor
	 */
    private void onExecuteButtonClicked() {
        ItemStack stack = blockInventory.getInvStack(0);
        assert MinecraftClient.getInstance().player != null;
        if (stack.isEmpty()) {
            MinecraftClient.getInstance().player.sendMessage(new TranslatableText("gui.quartus.executor.empty"));
            return;
        }

        if (!stack.getOrCreateTag().contains("circuit")) {
            MinecraftClient.getInstance().player.sendMessage(new TranslatableText("gui.quartus.executor.no_circuit"));
            return;
        }

        String circuitStr = stack.getOrCreateTag().getString("circuit");

        QuartusCircuit circuit = new QuartusCircuit();
        circuit.unserialize(circuitStr);

        updateExtensorModels(circuit);

        //Envia para o servidor o pedido de início da execução
        PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        QuartusExecutorStartC2SPacket quartusExecutorStartPacket = new QuartusExecutorStartC2SPacket(executorBlockPos);
        quartusExecutorStartPacket.write(packetByteBuf);
        quartusExecutorStartPacket.send(packetByteBuf);

    }

	/**
	 * Construtor padrão da classe ExecutorBlockController
	 * Adiciona uma interface ao compilador para facilitar o uso
	 * e deixa mais intuitivo seu uso
	 * @param syncId		Identificador ID do bloco
	 * @param playerInventory		Inventário do jogador
	 * @param blockInventory		Inventário do bloco
	 * @param compilerBlockPosition		Posição do bloco Executor
	 */
    public ExecutorBlockController(int syncId, PlayerInventory playerInventory, Inventory blockInventory, BlockPos executorBlockPos) {
        super(RecipeType.CRAFTING, syncId, playerInventory, blockInventory, null);
        this.executorBlockPos = executorBlockPos;

        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        root.setSize(150,100);

        WItemSlot itemSlot = WItemSlot.of(blockInventory, 0);
        root.add(itemSlot, 1, 1);

        WButton executeButton = new WButton(new TranslatableText("gui.quartus.executor.execute_btn"));
        executeButton.setOnClick(this::onExecuteButtonClicked);
        root.add(executeButton, 0, 3,  3, 20);

        root.add(this.createPlayerInventoryPanel(),0,5);
        root.validate(this);
    }

}
