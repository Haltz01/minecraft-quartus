package io.github.marcuscastelo.quartus.blockentity;

import io.github.marcuscastelo.quartus.registry.QuartusBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class CompilerBlockEntity extends BlockEntity implements ImplementedInventory {
    DefaultedList<ItemStack> inventoryItems;
    public CompilerBlockEntity() {
        super(QuartusBlockEntities.COMPILER_BLOCK_ENTITY);
        inventoryItems = DefaultedList.ofSize(1, ItemStack.EMPTY);
        System.out.println("BlockEntity criada!!!");
    }

    @Override
    public boolean onBlockAction(int i, int j) {
        System.out.println("Teste de BA!??!");
        return super.onBlockAction(i, j);
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
