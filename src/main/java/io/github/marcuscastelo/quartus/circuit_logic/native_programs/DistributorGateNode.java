package io.github.marcuscastelo.quartus.circuit_logic.native_programs;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public abstract class DistributorGateNode extends QuartusNode {
    public DistributorGateNode(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public String getNodeType() {
        return "DistributorGate";
    }
}
