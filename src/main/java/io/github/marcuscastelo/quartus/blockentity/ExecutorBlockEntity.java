package io.github.marcuscastelo.quartus.blockentity;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.registry.QuartusBlockEntities;
import io.github.marcuscastelo.quartus.registry.QuartusBlocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExecutorBlockEntity extends BlockEntity implements ImplementedInventory {
    QuartusCircuit currentCircuit = null;
    DefaultedList<ItemStack> inventoryItems;

    //TODO: drop disk
    public ExecutorBlockEntity() {
        super(QuartusBlockEntities.EXECUTOR_BLOCK_ENTITY_TYPE);
        this.inventoryItems = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    @Override
    public void setLocation(World world, BlockPos pos) {
        super.setLocation(world, pos);
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

    public void setCircuit(QuartusCircuit circuit) {
        this.currentCircuit = circuit;
    }

    public boolean hasCircuit() {
        return currentCircuit != null;
    }

    public void tick() {
//        if (!inventoryItems.get(0).getOrCreateTag().contains("circuit")) setCircuit(null);
        //TODO: remove debug message
        if (!hasCircuit())  {
            System.out.println("Acabou a execução");
            return;
        }

        //TODO: definir inputs e outputs do circuito de acordo com os extensores IO
        //TODO: mensagem de erro extensores insuficientes
        currentCircuit.updateCircuit();

        System.out.println(currentCircuit.serialize());

        assert world != null;
        world.getBlockTickScheduler().schedule(pos, QuartusBlocks.EXECUTOR, 20);
    }
}
