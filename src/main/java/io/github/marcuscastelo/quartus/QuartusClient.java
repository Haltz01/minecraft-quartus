package io.github.marcuscastelo.quartus;

import io.github.marcuscastelo.quartus.registry.QuartusCottonGUIs;
import net.fabricmc.api.ClientModInitializer;

/**
 * Método que inicializa o Mod criado no momento em que o jogo é executado
 */
public class QuartusClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        QuartusCottonGUIs.initClient();
    }
}

