package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.QuartusLogic;

import java.util.function.Supplier;

public class ComponentInfo {
    public final Supplier<CircuitComponent> componentSupplier;
    public final ComponentDirectionInfo directionInfo;
    public final QuartusLogic componentLogic;

    public ComponentInfo(Supplier<CircuitComponent> componentSupplier, ComponentDirectionInfo directionInfo, QuartusLogic componentLogic) {
        this.componentSupplier = componentSupplier;
        this.directionInfo = directionInfo;
        this.componentLogic = componentLogic;
    }
}
