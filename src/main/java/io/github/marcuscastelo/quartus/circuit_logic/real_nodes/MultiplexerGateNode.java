package io.github.marcuscastelo.quartus.circuit_logic.real_nodes;

import io.github.marcuscastelo.quartus.block.circuit_components.MultiplexerGateBlock;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MultiplexerGateNode extends QuartusNode {
    public MultiplexerGateNode(World world, BlockPos pos) throws QuartusWrongNodeBlockException{
        super(world, pos);
        if (!(world.getBlockState(pos).getBlock() instanceof MultiplexerGateBlock)) throw new QuartusWrongNodeBlockException();
    }

    @Override
    public String getNodeType() {
        return "MultiplexerGate";
    }
}
