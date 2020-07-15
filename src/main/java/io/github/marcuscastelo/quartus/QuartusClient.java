package io.github.marcuscastelo.quartus;

import io.github.marcuscastelo.quartus.registry.QuartusCottonGUIs;
import net.fabricmc.api.ClientModInitializer;

public class QuartusClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        QuartusCottonGUIs.initClient();
    }
}

