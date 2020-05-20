package io.github.marcuscastelo.quartus.gui;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.circuit_logic.CircuitCompiler;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusCircuit;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CompilerBlockController extends CottonCraftingController {
    BlockPos compilerPosition;


    private void updateFloppyDisk(QuartusCircuit circuit) {
        ItemStack floppyItemStack = blockInventory.getInvStack(0);
        if (floppyItemStack.isEmpty()) {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.sendMessage(new TranslatableText("gui.quartus.compiler.empty"));
            return;
        }

        CompoundTag tag = floppyItemStack.getOrCreateTag();
        tag.putBoolean("circuit", true);

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(compilerPosition);
        buf.writeCompoundTag(tag);

        ClientSidePacketRegistry.INSTANCE.sendToServer(Quartus.id("foda"), buf);
    }

    private QuartusCircuit compileCircuit() {
        BlockPos startPos = compilerPosition.offset(Direction.EAST, 10).offset(Direction.NORTH,10);
        BlockPos endPos = compilerPosition.offset(Direction.WEST, 10);
        CircuitCompiler compiler = new CircuitCompiler(world, compilerPosition, startPos, endPos);
        System.out.println("Começando compilação");
        return compiler.compile();
    }

    private void onCompilerButtonClick() {
        QuartusCircuit circuit = compileCircuit();
        updateFloppyDisk(circuit);

        //TODO: simulação
//        for (QuartusNode node: circuit.getNodeConnections().keySet()) {
//            node.getOutputValue()
//        }

    }

    public CompilerBlockController(int syncId, PlayerInventory playerInventory, Inventory blockInventory, BlockPos compilerPosition) {
        super(RecipeType.CRAFTING, syncId, playerInventory, blockInventory, null);
        this.compilerPosition = compilerPosition;

        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        root.setSize(150,100);

        WItemSlot itemSlot = WItemSlot.of(blockInventory, 0);
        root.add(itemSlot, 1, 1);

        WButton compileButton = new WButton(new TranslatableText("gui.quartus.compiler.compile_btn"));
        compileButton.setOnClick(()-> onCompilerButtonClick());
        root.add(compileButton, 0, 3,  3, 20);

        root.add(this.createPlayerInventoryPanel(),0,5);
        root.validate(this);
    }
}
