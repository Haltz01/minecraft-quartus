package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.network.handlers.QuartusExecutorStartC2SPacketHandler;
import io.github.marcuscastelo.quartus.network.handlers.QuartusExtensorIOUpdateC2SPacketHandler;
import io.github.marcuscastelo.quartus.network.handlers.QuartusFloppyDiskUpdateC2SPacketHandler;

/**
 * Classe responsável por registrar os handlers dos pacotes recebidos por rede
 */
public class QuartusNetworkHandlers {
    public static void init() {
        // Handler que trata o pacote de atualização do Floppy Disk com o circuito
        QuartusFloppyDiskUpdateC2SPacketHandler.register();
        // Handler que trata a atualização de texturas das portas E/S do executor
        QuartusExtensorIOUpdateC2SPacketHandler.register();
        // Handler que trata o pedido de início de execução do circuito no executor
        QuartusExecutorStartC2SPacketHandler.register();
    }
}
