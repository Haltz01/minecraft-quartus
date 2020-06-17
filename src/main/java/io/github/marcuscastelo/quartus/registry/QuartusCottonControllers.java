package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.blockentity.ImplementedInventory;
import io.github.marcuscastelo.quartus.gui.CompilerBlockController;
import io.github.marcuscastelo.quartus.gui.ExecutorBlockController;
import io.github.marcuscastelo.quartus.gui.client.CompilerBlockScreen;
import io.github.marcuscastelo.quartus.gui.client.ExecutorBlockScreen;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class QuartusCottonControllers {
    public static void init() {
        ContainerProviderRegistry.INSTANCE.registerFactory(Quartus.id("compiler"),  (syncId, identifier, playerEntity, packetByteBuf) -> {
            BlockPos compilerPos = packetByteBuf.readBlockPos();
            Inventory blockInventory = (ImplementedInventory) playerEntity.world.getBlockEntity(compilerPos);
            return new CompilerBlockController(syncId, playerEntity.inventory, blockInventory, compilerPos);
        });

        ContainerProviderRegistry.INSTANCE.registerFactory(Quartus.id("executor"),  (syncId, identifier, playerEntity, packetByteBuf) -> {
            BlockPos executorPos = packetByteBuf.readBlockPos();
            Inventory blockInventory = (ImplementedInventory) playerEntity.world.getBlockEntity(executorPos);
            return new ExecutorBlockController(syncId, playerEntity.inventory, blockInventory, executorPos);
        });
    }
}
