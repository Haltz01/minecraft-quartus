package io.github.marcuscastelo.quartus.network;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.ExecutorIOBlock;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class QuartusExtensorIOUpdateC2SPacket implements QuartusPacket{
    public static final Identifier ID = Quartus.id("extensor_io_update_c2s");

    public BlockPos getExtensorIOPos() {
        return extensorIOPos;
    }

    public ExecutorIOBlock.ExecutorIOState getExecutorIOState() {
        return executorIOState;
    }

    private BlockPos extensorIOPos;
    private ExecutorIOBlock.ExecutorIOState executorIOState;

    public QuartusExtensorIOUpdateC2SPacket() {}

    public QuartusExtensorIOUpdateC2SPacket(BlockPos extensorIOPos, ExecutorIOBlock.ExecutorIOState newState) {
        this.extensorIOPos = extensorIOPos;
        this.executorIOState = newState;
    }

    @Override
    public void read(PacketByteBuf buf) {
        extensorIOPos = buf.readBlockPos();
        executorIOState = buf.readEnumConstant(ExecutorIOBlock.ExecutorIOState.class);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(extensorIOPos);
        buf.writeEnumConstant(executorIOState);
    }

    @Override
    public void send(PacketByteBuf buf) {
        ClientSidePacketRegistry.INSTANCE.sendToServer(ID, buf);
    }
}
