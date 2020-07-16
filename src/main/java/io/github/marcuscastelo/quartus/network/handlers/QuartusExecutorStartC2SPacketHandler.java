package io.github.marcuscastelo.quartus.network.handlers;

import io.github.marcuscastelo.quartus.block.ExecutorBlock;
import io.github.marcuscastelo.quartus.network.QuartusExecutorStartC2SPacket;
import io.github.marcuscastelo.quartus.registry.QuartusBlocks;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class QuartusExecutorStartC2SPacketHandler {
    public static void register() {
        ServerSidePacketRegistry.INSTANCE.register(QuartusExecutorStartC2SPacket.ID, (packetContext, packetByteBuf) -> {
            final QuartusExecutorStartC2SPacket packet = new QuartusExecutorStartC2SPacket();
            packet.read(packetByteBuf);

            World world = packetContext.getPlayer().world;
            BlockPos executorPos = packet.getExecutorPos();

            packetContext.getTaskQueue().execute(() -> {
                Block currBlock = world.getBlockState(executorPos).getBlock();
                if (currBlock instanceof ExecutorBlock) {
                    world.getBlockTickScheduler().schedule(executorPos, QuartusBlocks.EXECUTOR, 2);
                }
            });
        });
    }
}
