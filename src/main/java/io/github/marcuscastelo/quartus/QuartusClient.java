package io.github.marcuscastelo.quartus;

import io.github.marcuscastelo.quartus.registry.QuartusCottonGUIs;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;

public class QuartusClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        QuartusCottonGUIs.initClient();
    }
}
