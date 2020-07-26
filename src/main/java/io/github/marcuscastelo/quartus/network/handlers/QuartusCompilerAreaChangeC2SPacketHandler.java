package io.github.marcuscastelo.quartus.network.handlers;

import io.github.marcuscastelo.quartus.blockentity.CompilerBlockEntity;
import io.github.marcuscastelo.quartus.blockentity.ExecutorBlockEntity;
import io.github.marcuscastelo.quartus.network.QuartusCompilerAreaChangeC2SPacket;
import io.github.marcuscastelo.quartus.network.QuartusExecutorStartC2SPacket;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Classe que define o comportamento do recebimento do pacote
 * de alteração da área do compilador.
 */
public class QuartusCompilerAreaChangeC2SPacketHandler {
    /**
     * Registra o comportamento acima citado no jogo
     */
    public static void register() {
        ServerSidePacketRegistry.INSTANCE.register(QuartusCompilerAreaChangeC2SPacket.ID, (packetContext, packetByteBuf) -> {
            QuartusCompilerAreaChangeC2SPacket packet = new QuartusCompilerAreaChangeC2SPacket();
            packet.read(packetByteBuf);
            World world = packetContext.getPlayer().world;

            BlockPos compilerPos = packet.getCompilerPos();
            int newSize = packet.getNewSize();

            BlockEntity be = world.getBlockEntity(compilerPos);
            if (!(be instanceof CompilerBlockEntity)) return;
            ((CompilerBlockEntity) be).setCompilingAreaSize(newSize);
        });
    }
}
