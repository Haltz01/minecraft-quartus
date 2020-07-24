package io.github.marcuscastelo.quartus.network.handlers;

import io.github.marcuscastelo.quartus.blockentity.ExecutorBlockEntity;
import io.github.marcuscastelo.quartus.network.QuartusExecutorStartC2SPacket;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Classe que gerencia os pacotes de dados do servidor com o client
 */
public class QuartusExecutorStartC2SPacketHandler {
    public static void register() {
        ServerSidePacketRegistry.INSTANCE.register(QuartusExecutorStartC2SPacket.ID, (packetContext, packetByteBuf) -> {
            final QuartusExecutorStartC2SPacket packet = new QuartusExecutorStartC2SPacket();
            packet.read(packetByteBuf);

            World world = packetContext.getPlayer().world;
            BlockPos executorPos = packet.getExecutorPos();

            packetContext.getTaskQueue().execute(() -> {
                BlockEntity currBlockEntity = world.getBlockEntity(executorPos);
                if (currBlockEntity instanceof ExecutorBlockEntity) {
                    ((ExecutorBlockEntity) currBlockEntity).startExecution(packetContext.getPlayer());
                }
            });
        });
    }
}
