package io.github.marcuscastelo.quartus.circuit_logic.native_programs;

import io.github.marcuscastelo.quartus.block.circuit_components.AndGateBlock;
import io.github.marcuscastelo.quartus.block.circuit_components.NandGateBlock;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NandGateNode extends QuartusNode {
    public NandGateNode(World world, BlockPos pos) throws QuartusWrongNodeBlockException {
        super(world, pos);
        if (!(world.getBlockState(pos).getBlock() instanceof NandGateBlock)) throw new QuartusWrongNodeBlockException();
    }

    @Override
    public String getNodeType() {
        return "NandGate";
    }
}
