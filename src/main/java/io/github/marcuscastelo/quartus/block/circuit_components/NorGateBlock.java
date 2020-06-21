package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusWorldNode;
import io.github.marcuscastelo.quartus.circuit_logic.real_nodes.NorGateNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NorGateBlock extends OrGateBlock {
    @Override
    public QuartusNode createQuartusNode() {
        return new NorGateNode();
    }
}
