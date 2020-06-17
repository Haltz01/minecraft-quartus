package io.github.marcuscastelo.quartus.network.handlers;

import io.github.marcuscastelo.quartus.block.ExtensorIOBlock;
import io.github.marcuscastelo.quartus.block.circuit_components.ExtensorGateBlock;
import io.github.marcuscastelo.quartus.blockentity.ImplementedInventory;
import io.github.marcuscastelo.quartus.network.QuartusExtensorIOUpdateC2SPacket;
import io.github.marcuscastelo.quartus.network.QuartusFloppyDiskUpdateC2SPacket;
import io.github.marcuscastelo.quartus.registry.QuartusItems;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class QuartusExtensorIOUpdateC2SPacketHandler {
    public static void register() {
        ServerSidePacketRegistry.INSTANCE.register(QuartusExtensorIOUpdateC2SPacket.ID, (packetContext, packetByteBuf) -> {
            final QuartusExtensorIOUpdateC2SPacket packet = new QuartusExtensorIOUpdateC2SPacket();
            packet.read(packetByteBuf);

            World world = packetContext.getPlayer().world;

            packetContext.getTaskQueue().execute(() -> {
                BlockState currBs = world.getBlockState(packet.getExtensorIOPos());
                if (currBs.getBlock() instanceof ExtensorIOBlock) {
                    world.setBlockState(packet.getExtensorIOPos(), currBs.with(ExtensorIOBlock.EXTENSOR_STATE, packet.getExtensorIOState()));
                }
            });
        });
    }
}
