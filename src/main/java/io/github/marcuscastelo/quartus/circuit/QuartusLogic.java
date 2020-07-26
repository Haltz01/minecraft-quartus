package io.github.marcuscastelo.quartus.circuit;

import io.github.marcuscastelo.quartus.circuit.components.info.ComponentExecutionInfo;

/**
 * Interface que possui a assinatura do método updateLogic
 * Facilita a escrita do código e modulariza a lógica
 */
@FunctionalInterface
public interface QuartusLogic {

    /**
     * Determina o que acontece com os outputs de um componente
     * dado os inputs
     * @param executionInfo dá acesso aos inputs e outputs do componente
     */
    void execute(ComponentExecutionInfo executionInfo);
    QuartusLogic EMPTY_LOGIC = (executionInfo -> {});
}
