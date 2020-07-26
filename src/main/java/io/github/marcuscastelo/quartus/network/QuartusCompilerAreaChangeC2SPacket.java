package io.github.marcuscastelo.quartus.network;

import io.github.marcuscastelo.quartus.Quartus;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

/**
 * Classe que define o conteúdo de um pacote de alteração de área de compilação.
 * É um pacote C2S, ou seja Cliente para Servidor.
 * Ao ser recebido pelo servidor, o QuartusCompilerAreaChangeC2SPacketHandler entra em ação
 */
public class QuartusCompilerAreaChangeC2SPacket implements QuartusPacket {
    //Identificador usado para determinar qual classe processará o recebimento desse pacote (ver QuartusCompilerAreaChangeC2SPacketHandler)
    public static final Identifier ID = Quartus.id("compiler_area_change_c2s");

    //Posição do compilador que terá sua área de compilação alterada
    BlockPos compilerPos;

    //Novo tamanho da área de compilação
    int newSize;

    /**
     * Getter da posição do compilador
     * @return Posição do compilador
     */
    public BlockPos getCompilerPos() {
        return compilerPos;
    }

    /**
     * Getter do novo tamanho
     * @return novo tamanho da área de compilação
     */
    public int getNewSize() {
        return newSize;
    }

    /**
     * Construtor vazio, usado quando se quer receber um pacote
     */
    public QuartusCompilerAreaChangeC2SPacket() {}

    /**
     * Construtor com informações, usado ao criar um novo pacote
     * @param compilerPos   Posição do compilador
     * @param newSize       Novo tamanho de compilação
     */
    public QuartusCompilerAreaChangeC2SPacket(BlockPos compilerPos, int newSize) {
        this.newSize = newSize;
        this.compilerPos = compilerPos;
    }

    /**
     * Lê o buffer de pacote, atribuindo seus valores
     * ao atual objeto
     * @param buf buffer recebido pelo servidor
     */
    @Override
    public void read(PacketByteBuf buf) {
        compilerPos = buf.readBlockPos();
        newSize = buf.readInt();
    }

    /**
     * Escreve os valores do atual objeto
     * no buffer de pacote desejado
     * @param buf buffer a ser enviado pelo cliente
     */
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(compilerPos);
        buf.writeInt(newSize);
    }

    /**
     * Envia o buffer previamente preenchido para o servidor
     * @param buf buffer preenchido com o método write
     */
    @Override
    public void send(PacketByteBuf buf) {
        ClientSidePacketRegistry.INSTANCE.sendToServer(ID, buf);
    }
}
