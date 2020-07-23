package io.github.marcuscastelo.quartus.circuit;

import io.github.marcuscastelo.quartus.circuit.components.ComponentExecutionInfo;
import net.minecraft.util.math.Direction;

import java.util.Map;

@FunctionalInterface
public interface QuartusLogic {
    void updateLogic(ComponentExecutionInfo executionInfo);
}
