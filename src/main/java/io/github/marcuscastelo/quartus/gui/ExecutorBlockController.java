package io.github.marcuscastelo.quartus.gui;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.marcuscastelo.quartus.block.ExtensorIOBlock;
import io.github.marcuscastelo.quartus.blockentity.ExecutorBlockEntity;
import io.github.marcuscastelo.quartus.network.QuartusExtensorIOUpdateC2SPacket;
import io.github.marcuscastelo.quartus.registry.QuartusBlocks;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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
import org.apache.commons.lang3.StringUtils;

public class ExecutorBlockController extends CottonCraftingController {
    BlockPos executorBlockPos;

    private void updateExtensorModels(String circuitStr) {

        //TODO: usar método mais eficiente com o QuartusCircuit.of
        int nInputs = StringUtils.countMatches(circuitStr, "Input");
        int nOutputs = StringUtils.countMatches(circuitStr, "Output");

        Direction exploreDirection = world.getBlockState(executorBlockPos).get(Properties.HORIZONTAL_FACING).rotateYCounterclockwise();
        BlockPos extensorPos = executorBlockPos.offset(exploreDirection);
        BlockState extensorState = world.getBlockState(extensorPos);
        int distance = 1;
        while (extensorState.getBlock().equals(QuartusBlocks.EXTENSOR_IO)) {
            ExtensorIOBlock.ExtensorIOState newExtensorState;
            if (distance <= nInputs && distance <= nOutputs) {
                newExtensorState =  ExtensorIOBlock.ExtensorIOState.IO;
            } else if (distance <= nInputs) {
                newExtensorState = ExtensorIOBlock.ExtensorIOState.INPUT;
            } else if (distance <= nOutputs) {
                newExtensorState = ExtensorIOBlock.ExtensorIOState.OUPUT;
            } else break;

            //Define os blocos no cliente
            world.setBlockState(extensorPos, extensorState.with(ExtensorIOBlock.EXTENSOR_STATE, newExtensorState));

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

    //TODO: dar update no servidor
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

        updateExtensorModels(circuitStr);

//        QuartusCircuitExplorationGraph circuit = QuartusCircuitExplorationGraph.of(circuitStr);

        BlockEntity be = world.getBlockEntity(executorBlockPos);
        if (be instanceof ExecutorBlockEntity) {
            ((ExecutorBlockEntity) be).onExecutionStart(world, executorBlockPos);
        }

    }

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
