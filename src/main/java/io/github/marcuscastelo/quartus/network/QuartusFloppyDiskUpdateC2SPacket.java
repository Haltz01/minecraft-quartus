package io.github.marcuscastelo.quartus.network;

import io.github.marcuscastelo.quartus.Quartus;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

/**
 * Classe que gerencia e atualiza o pacote de dados recebidos pelo FloppyDisk
 */
public class QuartusFloppyDiskUpdateC2SPacket implements QuartusPacket {
	//Variável que armazena o identificador ID
    public static final Identifier ID = Quartus.id("floppy_disk_update_c2s");
	//Variável que armazena a posição do bloco
	private BlockPos compilerPos;
	//Variável que armazena a Tag do bloco
    private CompoundTag compoundTag;

	/**
	 * Método que retorna a Tag do bloco QuartusFloppyDiskUpdateC2SPacket
	 * @return	Tag do bloco, com os dados armazenados
	 */
    public CompoundTag getCompoundTag() {
        return compoundTag;
    }

	/**
	 * Método que retorna a posição do bloco
	 * @return	Posição do bloco
	 */
    public BlockPos getCompilerPos() {
        return compilerPos;
    }

    public QuartusFloppyDiskUpdateC2SPacket() {}
	/**
	 * Construtor padrão da classe QuartusFloppyDiskUpdateC2SPacket
	 * @param compilerPos	Posição do compilador no mundo
	 * @param floppyItemStack	Pilha de itens do  inventário
	 */
    public QuartusFloppyDiskUpdateC2SPacket(BlockPos compilerPos, ItemStack floppyItemStack) {
        this.compilerPos = compilerPos;
        this.compoundTag = floppyItemStack.getOrCreateTag();
    }

	/**
	 * Método que faz a leitura do pacote de dados e
	 * armazena na Tag do FloppyDisk.
	 * Também lê a posição do bloco para o compilerPos
	 * @param buf	Pacote de dados recebido a ser lido
	 */
    @Override
    public void read(PacketByteBuf buf) {
        this.compilerPos = buf.readBlockPos();
        this.compoundTag = buf.readCompoundTag();
    }

	/**
	 * Método que faz a escrita do pacote de dados
	 * na Tag do FloppyDisk.
	 * Escreve a posição do bloco Compiler
	 * @param buf	Pacote de dados a ser escrito
	 */
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(compilerPos);
        buf.writeCompoundTag(compoundTag);
    }

	/**
	 * Método que faz o envio do pacote de dados
	 * da Entity para o servidor
	 * @param buf	Pacote de dados a ser enviado apra o servidor
	 */
    @Override
    public void send(PacketByteBuf buf) {
        ClientSidePacketRegistry.INSTANCE.sendToServer(ID, buf);
    }
}
