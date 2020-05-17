package io.github.marcuscastelo.quartus.gui;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.marcuscastelo.quartus.Quartus;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.TranslatableText;

public class CompilerBlockController extends CottonCraftingController {
    public CompilerBlockController(int syncId, PlayerInventory playerInventory, Inventory blockInventory) {
        super(RecipeType.CRAFTING, syncId, playerInventory, blockInventory, null);

        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        root.setSize(150,100);

        WItemSlot itemSlot = WItemSlot.of(blockInventory, 0);
        root.add(itemSlot, 1, 1);

        WButton compileButton = new WButton(new TranslatableText("gui.quartus.compile_btn"));
        Quartus.LOGGER.info("Can: " + compileButton.canResize());
        compileButton.setOnClick(()->{
            Quartus.LOGGER.info("Botão clicado!!");
        });
        root.add(compileButton, 0, 3,  3, 20);


        root.add(this.createPlayerInventoryPanel(),0,5);
        root.validate(this);
    }
}
