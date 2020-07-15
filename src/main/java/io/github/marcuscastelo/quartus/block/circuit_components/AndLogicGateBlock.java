package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit.QuartusBusInfo;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;

import java.util.function.Supplier;

public class AndLogicGateBlock extends AbstractLogicGateBlock {
    @Override
    public QuartusCircuitComponent getCircuitComponent() {
        return new QuartusCircuitComponent("AndGate") {
            @Override
            public void updateComponent() {
                //Logica and foda
                
            }
        };
    }
}
