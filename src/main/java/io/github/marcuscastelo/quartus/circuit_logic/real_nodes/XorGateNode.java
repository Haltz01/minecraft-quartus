package io.github.marcuscastelo.quartus.circuit_logic.real_nodes;

import io.github.marcuscastelo.quartus.block.circuit_components.XorGateBlock;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusWorldNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class XorGateNode extends QuartusNode {
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
        return "XorGate";
    }
}
