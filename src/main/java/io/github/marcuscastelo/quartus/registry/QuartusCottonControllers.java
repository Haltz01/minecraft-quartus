package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.blockentity.ImplementedInventory;
import io.github.marcuscastelo.quartus.gui.CompilerBlockController;
import io.github.marcuscastelo.quartus.gui.ExecutorBlockController;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;

/**
 * Classe responsável por registrar os controladores de interface gráfica de inventário (retirada da LibGUI)
 */
public class QuartusCottonControllers {
    /**
     * Método que registra todos os controladores (registra uma Factory de controladores - lambda function)
     */
    public static void init() {
        // Registrando controlador da GUI do compilador
        ContainerProviderRegistry.INSTANCE.registerFactory(Quartus.id("compiler"),  (syncId, identifier, playerEntity, packetByteBuf) -> {
            BlockPos compilerPos = packetByteBuf.readBlockPos();
            Inventory blockInventory = (ImplementedInventory) playerEntity.world.getBlockEntity(compilerPos);
            return new CompilerBlockController(syncId, playerEntity.inventory, blockInventory, compilerPos);
        });
        // Registrando controlador da GUI do executor
        ContainerProviderRegistry.INSTANCE.registerFactory(Quartus.id("executor"),  (syncId, identifier, playerEntity, packetByteBuf) -> {
            BlockPos executorPos = packetByteBuf.readBlockPos();
            Inventory blockInventory = (ImplementedInventory) playerEntity.world.getBlockEntity(executorPos);
            return new ExecutorBlockController(syncId, playerEntity.inventory, blockInventory, executorPos);
        });
    }
}
