package io.github.marcuscastelo.quartus.network;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.ExtensorIOBlock;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class QuartusExtensorIOUpdateC2SPacket implements QuartusPacket{
    public static final Identifier ID = Quartus.id("extensor_io_update_c2s");

    public BlockPos getExtensorIOPos() {
        return extensorIOPos;
    }

    public ExtensorIOBlock.ExtensorIOState getExtensorIOState() {
        return extensorIOState;
    }

    private BlockPos extensorIOPos;
    private ExtensorIOBlock.ExtensorIOState extensorIOState;

    public QuartusExtensorIOUpdateC2SPacket() {}

    public QuartusExtensorIOUpdateC2SPacket(BlockPos extensorIOPos, ExtensorIOBlock.ExtensorIOState newState) {
        this.extensorIOPos = extensorIOPos;
        this.extensorIOState = newState;
    }

    @Override
    public void read(PacketByteBuf buf) {
        extensorIOPos = buf.readBlockPos();
        extensorIOState = buf.readEnumConstant(ExtensorIOBlock.ExtensorIOState.class);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(extensorIOPos);
        buf.writeEnumConstant(extensorIOState);
    }

    @Override
    public void send(PacketByteBuf buf) {
        ClientSidePacketRegistry.INSTANCE.sendToServer(ID, buf);
    }
}
