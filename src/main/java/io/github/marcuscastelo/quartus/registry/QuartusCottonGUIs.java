package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.CompilerBlock;
import io.github.marcuscastelo.quartus.blockentity.CompilerBlockEntity;
import io.github.marcuscastelo.quartus.gui.CompilerBlockController;
import io.github.marcuscastelo.quartus.gui.client.CompilerBlockScreen;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.BlockContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class QuartusCottonGUIs {
    public static void init() {}

    private static Inventory getBlockInventory(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CompilerBlockEntity)
            return (CompilerBlockEntity)blockEntity;
        return null;
    }

    static {
        ContainerProviderRegistry.INSTANCE.registerFactory(Quartus.id("compiler"),
                (syncId, identifier, playerEntity, packetByteBuf) -> {
                    Inventory blockInventory = getBlockInventory(playerEntity.world, packetByteBuf.readBlockPos());
                    return new CompilerBlockController(syncId, playerEntity.inventory, blockInventory);
                });

        ScreenProviderRegistry.INSTANCE.registerFactory(Quartus.id("compiler"), (syncId, identifier, playerEntity, packetByteBuf) -> {
            Inventory blockInventory = getBlockInventory(playerEntity.world, packetByteBuf.readBlockPos());
            CompilerBlockController controller = new CompilerBlockController(syncId, playerEntity.inventory, blockInventory);
            return new CompilerBlockScreen(controller, playerEntity);
        });
    }
}
