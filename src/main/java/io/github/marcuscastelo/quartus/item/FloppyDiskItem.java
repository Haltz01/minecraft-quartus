package io.github.marcuscastelo.quartus.item;

import io.github.marcuscastelo.quartus.Quartus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class FloppyDiskItem extends Item {
    public FloppyDiskItem() {
        super(new Settings().group(Quartus.ITEMGROUP));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!user.world.isClient) return TypedActionResult.pass(stack);

        if (stack.getTag() == null || !stack.getTag().contains("circuit")) return TypedActionResult.pass(stack);
        MinecraftClient.getInstance().player.sendMessage(new LiteralText(stack.getOrCreateTag().getString("circuit")));
        
        return TypedActionResult.success(stack);
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
