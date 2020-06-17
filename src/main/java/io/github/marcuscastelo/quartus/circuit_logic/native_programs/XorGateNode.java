package io.github.marcuscastelo.quartus.circuit_logic.native_programs;

import io.github.marcuscastelo.quartus.block.circuit_components.OrGateBlock;
import io.github.marcuscastelo.quartus.block.circuit_components.XorGateBlock;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class XorGateNode extends QuartusNode{
    public XorGateNode(World world, BlockPos pos) throws QuartusNode.QuartusWrongNodeBlockException {
        super(world, pos);
        if (!(world.getBlockState(pos).getBlock() instanceof XorGateBlock)) throw new QuartusNode.QuartusWrongNodeBlockException();
    }

    @Override
    public String getNodeType() {
        return "XorGate";
    }
}
