package io.github.marcuscastelo.quartus.circuit_logic.real_nodes;

import io.github.marcuscastelo.quartus.block.circuit_components.NorGateBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NorGateNode extends OrGateNode {
    @Override
    public String getNodeType() {
        return "NorGate";
    }
}
