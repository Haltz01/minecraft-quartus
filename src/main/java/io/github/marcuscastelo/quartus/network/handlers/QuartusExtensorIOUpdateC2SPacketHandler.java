package io.github.marcuscastelo.quartus.network.handlers;

import io.github.marcuscastelo.quartus.block.ExecutorIOBlock;
import io.github.marcuscastelo.quartus.network.QuartusExtensorIOUpdateC2SPacket;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;

public class QuartusExtensorIOUpdateC2SPacketHandler {
    public static void register() {
        ServerSidePacketRegistry.INSTANCE.register(QuartusExtensorIOUpdateC2SPacket.ID, (packetContext, packetByteBuf) -> {
            final QuartusExtensorIOUpdateC2SPacket packet = new QuartusExtensorIOUpdateC2SPacket();
            packet.read(packetByteBuf);

            World world = packetContext.getPlayer().world;

            packetContext.getTaskQueue().execute(() -> {
                BlockState currBs = world.getBlockState(packet.getExtensorIOPos());
                if (currBs.getBlock() instanceof ExecutorIOBlock) {
                    world.setBlockState(packet.getExtensorIOPos(), currBs.with(ExecutorIOBlock.EXTENSOR_STATE, packet.getExecutorIOState()));
                }
            });
        });
    }
}
