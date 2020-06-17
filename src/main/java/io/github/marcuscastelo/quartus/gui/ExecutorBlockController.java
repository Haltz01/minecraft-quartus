package io.github.marcuscastelo.quartus.gui;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.marcuscastelo.quartus.block.ExtensorIOBlock;
import io.github.marcuscastelo.quartus.registry.QuartusBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.util.math.Direction;
import sun.jvm.hotspot.opto.Block;

public class ExecutorBlockController extends CottonCraftingController {
    BlockPos executorBlockPos;

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

        int nInputs = StringUtils.countMatches(circuitStr, "Input");
        int nOutputs = StringUtils.countMatches(circuitStr, "Output");

        Direction exploreDirection = world.getBlockState(executorBlockPos).get(Properties.HORIZONTAL_FACING).rotateYCounterclockwise();
        BlockPos extensorPos = executorBlockPos.offset(exploreDirection);
        BlockState extensorState = world.getBlockState(extensorPos);
        int distance = 1;
        while (extensorState.getBlock().equals(QuartusBlocks.EXTENSOR_IO)) {
            if (distance <= nInputs && distance <= nOutputs) {
                world.setBlockState(extensorPos, extensorState.with(ExtensorIOBlock.EXTENSOR_STATE, ExtensorIOBlock.ExtensorIOState.IO));
            } else if (distance <= nInputs) {
                world.setBlockState(extensorPos, extensorState.with(ExtensorIOBlock.EXTENSOR_STATE, ExtensorIOBlock.ExtensorIOState.INPUT));
            } else if (distance <= nOutputs) {
                world.setBlockState(extensorPos, extensorState.with(ExtensorIOBlock.EXTENSOR_STATE, ExtensorIOBlock.ExtensorIOState.OUPUT));
            } else break;

            distance++;

            extensorPos = extensorPos.offset(exploreDirection);
            extensorState = world.getBlockState(extensorPos);
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
