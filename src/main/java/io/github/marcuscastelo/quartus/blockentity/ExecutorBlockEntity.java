package io.github.marcuscastelo.quartus.blockentity;

import io.github.marcuscastelo.quartus.registry.QuartusBlockEntities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExecutorBlockEntity extends BlockEntity implements ImplementedInventory, Tickable {
    DefaultedList<ItemStack> inventoryItems;

//    public CircuitExecutor getCircuitExecutor() {
//        return circuitExecutor;
//    }

//    CircuitExecutor circuitExecutor = null;

    public ExecutorBlockEntity() {
        super(QuartusBlockEntities.EXECUTOR_BLOCK_ENTITY_TYPE);
        this.inventoryItems = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    @Override
    public void setLocation(World world, BlockPos pos) {
        super.setLocation(world, pos);
//        this.circuitExecutor = new CircuitExecutor(world, pos, null);
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

    public void onExecutionStop(World world, BlockPos pos) {

    }

    //TODO: refazer
    public void onExecutionStart(World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ExecutorBlockEntity) {
            //Obtém o disquete
            ItemStack stack = ((ExecutorBlockEntity) be).getInvStack(0);
            //Obtém o circuito salvo no disquete
            //TODO: checar se não houver circuito no disquete
//            String circuitDescription = stack.getOrCreateTag().getString("circuit");
//            CircuitExecutor executor = ((ExecutorBlockEntity) be).getCircuitExecutor();
//            if (executor == null) return;
//            //Define o circuito no executor
//            executor.setCircuit(QuartusCircuitExplorationGraph.of(circuitDescription));
//            //Inicia a simulação
//            executor.start();
        }
    }

    @Override
    public void tick() {

    }

//    @Override
//    public void tick() {
//        circuitExecutor.tick();
//    }
}
