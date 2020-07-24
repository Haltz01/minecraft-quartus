package io.github.marcuscastelo.quartus.network;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.ExecutorIOBlock;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

/**
 * Classe que atualiza o pacote de dados recebido pelo ExtensorIO
 */
public class QuartusExtensorIOUpdateC2SPacket implements QuartusPacket{
	//Variável que armazena o identificador ID do ExtensorIO
    public static final Identifier ID = Quartus.id("extensor_io_update_c2s");

	/**
	 * Método que retorna a posição do ExtensorIO
	 * @return	BlockPos com a posição do ExtensorIO
	 */
    public BlockPos getExtensorIOPos() {
        return extensorIOPos;
    }

	/**
	 * Método que retorna a propriedade/estado do ExtensorIO
	 * @return	Propriedade/Estado do ExtensorIO
	 */
    public ExecutorIOBlock.ExecutorIOState getExecutorIOState() {
        return executorIOState;
    }

	//Variáveis que armazenam a posição e o estado do ExtensorIO
    private BlockPos extensorIOPos;
    private ExecutorIOBlock.ExecutorIOState executorIOState;

    public QuartusExtensorIOUpdateC2SPacket() {}
	/**
	 * Construtor padrão de QuartusExtensorIOUpdateC2SPacket
	 * @param extensorIOPos	Posição do bloco
	 * @param newState	Estado/Propriedade do bloco
	 */
    public QuartusExtensorIOUpdateC2SPacket(BlockPos extensorIOPos, ExecutorIOBlock.ExecutorIOState newState) {
        this.extensorIOPos = extensorIOPos;
        this.executorIOState = newState;
    }

	/**
	 * Método que faz a leitura do pacote de bytes recebido
	 */
    @Override
    public void read(PacketByteBuf buf) {
        extensorIOPos = buf.readBlockPos();
        executorIOState = buf.readEnumConstant(ExecutorIOBlock.ExecutorIOState.class);
    }

	/**
	 * Método que faz a escrita na Entity do ExtensorIO
	 */
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(extensorIOPos);
        buf.writeEnumConstant(executorIOState);
    }

	/**
	 * Método que envia para o servidor o pacote de dados escrito
	 */
    @Override
    public void send(PacketByteBuf buf) {
        ClientSidePacketRegistry.INSTANCE.sendToServer(ID, buf);
    }
}
