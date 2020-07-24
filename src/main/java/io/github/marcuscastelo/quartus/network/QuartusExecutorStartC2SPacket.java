package io.github.marcuscastelo.quartus.network;

import io.github.marcuscastelo.quartus.Quartus;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

/**
 * Classe que executa os passos para funcionar o Executor
 * Métodos chamados quando pressionado o botão Execute
 */
public class QuartusExecutorStartC2SPacket implements QuartusPacket {
	//Variável que contém o identificador ID do bloco
    public static final Identifier ID = Quartus.id("executor_start_c2s");

	/**
	 * Método que retorna a posição do bloco Executor
	 * @return	Posição do executor
	 */
    public BlockPos getExecutorPos() {
        return executorPos;
    }

	//Variável que armazena a posição do Executor
    private BlockPos executorPos;

    public QuartusExecutorStartC2SPacket() {}
	/**
	 * Construtor padrão da classe QuartusExecutorStartC2SPacket
	 * @param executorPos	Posição do bloco
	 */
    public QuartusExecutorStartC2SPacket(BlockPos executorPos) {
        this.executorPos = executorPos;
    }

	/**
	 * Método que faz a leitura do pacote de dados recebio pelo Executor
	 */
    @Override
    public void read(PacketByteBuf buf) {
        executorPos = buf.readBlockPos();
    }

	/**
	 * Método que faz a escrita na Entity do Executor
	 * @param buf	Pacote de bytes com informações a serem escritas
	 */
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(executorPos);
    }

	/**
	 * Método que envia o pacote de dados para o servidor
	 */
    @Override
    public void send(PacketByteBuf buf) {
        ClientSidePacketRegistry.INSTANCE.sendToServer(ID, buf);
    }
}
