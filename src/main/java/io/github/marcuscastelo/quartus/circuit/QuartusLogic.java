package io.github.marcuscastelo.quartus.circuit;

import io.github.marcuscastelo.quartus.circuit.components.ComponentExecutionInfo;

/**
 * Interface que possui a assinatura do método updateLogic
 * Facilita a escrita do código e modulariza a lógica
 */
@FunctionalInterface
public interface QuartusLogic {
    void updateLogic(ComponentExecutionInfo executionInfo);
}
