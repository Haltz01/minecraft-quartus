package io.github.marcuscastelo.quartus.circuit_logic.resolvable_nodes;

import io.github.marcuscastelo.quartus.block.circuit_components.ExtensorGateBlock;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusResolvableNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public final class ExtensorGateNode extends QuartusNode {
    @Override
    public List<Direction> getRelativeInputDirections() {
        return null;
    }

    @Override
    public List<Direction> getRelativeOutputDirections() {
        return null;
    }

    @Override
    public String getNodeType() {
        return "ExtensorGate";
    }

}
