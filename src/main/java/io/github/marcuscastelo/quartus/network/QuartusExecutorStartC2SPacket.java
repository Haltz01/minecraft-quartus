package io.github.marcuscastelo.quartus.network;

import io.github.marcuscastelo.quartus.Quartus;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class QuartusExecutorStartC2SPacket implements QuartusPacket {
    public static final Identifier ID = Quartus.id("executor_start_c2s");

    public BlockPos getExecutorPos() {
        return executorPos;
    }

    private BlockPos executorPos;

    public QuartusExecutorStartC2SPacket() {}

    public QuartusExecutorStartC2SPacket(BlockPos executorPos) {
        this.executorPos = executorPos;
    }

    @Override
    public void read(PacketByteBuf buf) {
        executorPos = buf.readBlockPos();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(executorPos);
    }

    @Override
    public void send(PacketByteBuf buf) {
        ClientSidePacketRegistry.INSTANCE.sendToServer(ID, buf);
    }
}
