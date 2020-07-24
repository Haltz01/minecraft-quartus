package io.github.marcuscastelo.quartus.network;

import net.minecraft.util.PacketByteBuf;

/**
 * Interface que possui as assinaturas dos métodos
 * de leitura, escrita e envio dos pacotes de dados
 */
public interface QuartusPacket {
    void read(PacketByteBuf buf);
    void write(PacketByteBuf buf);
    void send(PacketByteBuf buf);
}
