package io.github.marcuscastelo.quartus.gui.client;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.marcuscastelo.quartus.gui.CompilerBlockController;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Classe auxiliar que gerencia a interface do bloco Compiler na tela do jogador
 */
public class CompilerBlockScreen extends CottonInventoryScreen<CompilerBlockController> {
    public CompilerBlockScreen(CompilerBlockController container, PlayerEntity player) {
        super(container, player);
    }
}
