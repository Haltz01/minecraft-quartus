package io.github.marcuscastelo.quartus.circuit;

import net.minecraft.util.math.Direction;

import java.util.Map;

@FunctionalInterface
public interface QuartusLogic {
    void updateLogic(Map<Direction, QuartusBusInfo> inputs, Map<Direction, QuartusBusInfo> outputs);
}
