package io.github.marcuscastelo.quartus.gui.client;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.marcuscastelo.quartus.gui.CompilerBlockController;
import io.github.marcuscastelo.quartus.gui.ExecutorBlockController;
import net.minecraft.entity.player.PlayerEntity;

public class ExecutorBlockScreen extends CottonInventoryScreen<ExecutorBlockController> {
    public ExecutorBlockScreen(ExecutorBlockController container, PlayerEntity player) {
        super(container, player);
    }
}
