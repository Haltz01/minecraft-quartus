package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.network.handlers.QuartusExecutorStartC2SPacketHandler;
import io.github.marcuscastelo.quartus.network.handlers.QuartusExtensorIOUpdateC2SPacketHandler;
import io.github.marcuscastelo.quartus.network.handlers.QuartusFloppyDiskUpdateC2SPacketHandler;

public class QuartusNetworkHandlers {
    public static void init() {
        QuartusFloppyDiskUpdateC2SPacketHandler.register();
        QuartusExtensorIOUpdateC2SPacketHandler.register();
        QuartusExecutorStartC2SPacketHandler.register();
    }
}
