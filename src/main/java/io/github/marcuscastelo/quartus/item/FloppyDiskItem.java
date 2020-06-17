package io.github.marcuscastelo.quartus.item;

import io.github.marcuscastelo.quartus.Quartus;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import java.util.List;

public class FloppyDiskItem extends Item {
    public FloppyDiskItem() {
        super(new Settings().group(Quartus.ITEMGROUP));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        stack.getOrCreateTag().get("circuit");
    }

    @Override
    public boolean hasEnchantmentGlint(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains("circuit");
    }
}
