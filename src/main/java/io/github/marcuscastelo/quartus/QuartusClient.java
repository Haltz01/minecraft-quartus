package io.github.marcuscastelo.quartus;

import io.github.marcuscastelo.quartus.registry.QuartusCottonGUIs;
import net.fabricmc.api.ClientModInitializer;

/**
 * Classe que provê o método de inicilização dos recursos do mod que são destinados apenas ao cliente
 */
public class QuartusClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        QuartusCottonGUIs.initClient();
    }
}

