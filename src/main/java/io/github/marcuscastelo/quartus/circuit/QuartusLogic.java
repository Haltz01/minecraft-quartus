package io.github.marcuscastelo.quartus.circuit;

import net.minecraft.util.math.Direction;

import java.util.Map;

/**
 * Interface que possui a assinatura do método updateLogic
 * Facilita a escrita do código e modulariza a lógica
 */
@FunctionalInterface
public interface QuartusLogic {
    void updateLogic(Map<Direction, QuartusBusInfo> inputs, Map<Direction, QuartusBusInfo> outputs);
}
