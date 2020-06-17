package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import io.github.marcuscastelo.quartus.circuit_logic.native_programs.NandGateNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NandGateBlock extends AndGateBlock {
    @Override
    public QuartusNode createQuartusNode(World world, BlockPos pos) throws QuartusNode.QuartusWrongNodeBlockException {
        return new NandGateNode(world, pos);
    }
}
