package io.github.marcuscastelo.quartus.gui;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.circuit_components.WireBlock;
import io.github.marcuscastelo.quartus.circuit_logic.CircuitCompiler;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusCircuit;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Map;

public class CompilerBlockController extends CottonCraftingController {
    private void deletemeplease() {
        BlockPos pos = playerInventory.player.getBlockPos();
        BlockPos startPos = pos.offset(Direction.EAST, 10).offset(Direction.NORTH,10);
        BlockPos endPos = pos.offset(Direction.WEST, 10);
        CircuitCompiler compiler = new CircuitCompiler(world, pos, startPos, endPos);
        System.out.println("Começando compilação");
        QuartusCircuit circuit = compiler.compile();

        System.out.println(circuit);

    }

    public CompilerBlockController(int syncId, PlayerInventory playerInventory, Inventory blockInventory) {
        super(RecipeType.CRAFTING, syncId, playerInventory, blockInventory, null);

        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        root.setSize(150,100);

        WItemSlot itemSlot = WItemSlot.of(blockInventory, 0);
        root.add(itemSlot, 1, 1);

        WButton compileButton = new WButton(new TranslatableText("gui.quartus.compile_btn"));
        compileButton.setOnClick(()->deletemeplease());
        root.add(compileButton, 0, 3,  3, 20);


        root.add(this.createPlayerInventoryPanel(),0,5);
        root.validate(this);
    }
}
