package io.github.marcuscastelo.quartus.network;

import net.minecraft.util.PacketByteBuf;

public interface QuartusPacket {
    void read(PacketByteBuf buf);
    void write(PacketByteBuf buf);
    void send(PacketByteBuf buf);
}
