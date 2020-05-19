package io.github.marcuscastelo.quartus.item;

import io.github.marcuscastelo.quartus.Quartus;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPropertyGetter;
import net.minecraft.item.ItemStack;

public class FloppyDiskItem extends Item {
    public FloppyDiskItem() {
        super(new Settings().group(Quartus.ITEMGROUP));
    }

    @Override
    public boolean hasEnchantmentGlint(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains("circuit");
    }
}
