package io.github.marcuscastelo.quartus.blockentity;

import io.github.marcuscastelo.quartus.registry.QuartusBlockEntities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;

public class CompilerBlockEntity extends BlockEntity implements ImplementedInventory {
    DefaultedList<ItemStack> inventoryItems;
    public CompilerBlockEntity() {
        super(QuartusBlockEntities.COMPILER_BLOCK_ENTITY_TYPE);
        inventoryItems = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventoryItems;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, inventoryItems);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        Inventories.fromTag(tag, inventoryItems);
    }
}
