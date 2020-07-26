package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.gui.CompilerBlockController;
import io.github.marcuscastelo.quartus.gui.ExecutorBlockController;
import io.github.marcuscastelo.quartus.gui.client.CompilerBlockScreen;
import io.github.marcuscastelo.quartus.gui.client.ExecutorBlockScreen;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Classe que registra as GUI (as telas) dos blocos executor e compilador
 * Obs.: só deve ser chamada para o cliente, o servidor não deve registrar
 */
public class QuartusCottonGUIs {
    /**
     * Registra as GUIs dos blocos
     */
    public static void initClient() {
        ScreenProviderRegistry.INSTANCE.registerFactory(Quartus.id("compiler"), (syncId, identifier, playerEntity, packetByteBuf) -> {
            BlockPos compilerPos = packetByteBuf.readBlockPos();
            Inventory blockInventory = getBlockInventory(playerEntity.world, compilerPos);
            CompilerBlockController controller = new CompilerBlockController(syncId, playerEntity.inventory, blockInventory, compilerPos);
            return new CompilerBlockScreen(controller, playerEntity);
        });

        ScreenProviderRegistry.INSTANCE.registerFactory(Quartus.id("executor"), (syncId, identifier, playerEntity, packetByteBuf) -> {
            BlockPos executorPos = packetByteBuf.readBlockPos();
            Inventory blockInventory = getBlockInventory(playerEntity.world, executorPos);
            ExecutorBlockController controller = new ExecutorBlockController(syncId, playerEntity.inventory, blockInventory, executorPos);
            return new ExecutorBlockScreen(controller, playerEntity);
        });
    }

    /**
     * Método que obtém um inventário no mundo dada uma posiçãp
     * @param world Mundo
     * @param pos   Posição
     * @return      Inventário
     */
    public static Inventory getBlockInventory(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof Inventory)
            return (Inventory) blockEntity;
        throw new RuntimeException("Trying to get inventory of " + pos + " which doesn't have any inventory");
    }
}
