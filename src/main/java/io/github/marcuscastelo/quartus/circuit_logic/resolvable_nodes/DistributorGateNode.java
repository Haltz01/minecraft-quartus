package io.github.marcuscastelo.quartus.circuit_logic.resolvable_nodes;

import io.github.marcuscastelo.quartus.block.circuit_components.DistributorGateBlock;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusResolvableNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class DistributorGateNode extends QuartusResolvableNode {
    public DistributorGateNode(World world, BlockPos pos) throws QuartusWrongNodeBlockException {
        super(world, pos);
        if (!(world.getBlockState(pos).getBlock() instanceof DistributorGateBlock)) throw new QuartusWrongNodeBlockException();
    }

    @Override
    public String getNodeType() {
        return "DistributorGate";
    }
}
