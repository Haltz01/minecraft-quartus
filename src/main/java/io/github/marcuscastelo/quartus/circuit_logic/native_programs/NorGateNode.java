package io.github.marcuscastelo.quartus.circuit_logic.native_programs;

import io.github.marcuscastelo.quartus.block.circuit_components.ExtensorGateBlock;
import io.github.marcuscastelo.quartus.block.circuit_components.NorGateBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NorGateNode extends OrGateNode {
    public NorGateNode(World world, BlockPos pos) throws QuartusWrongNodeBlockException {
        super(world, pos);
        if (!(world.getBlockState(pos).getBlock() instanceof NorGateBlock)) throw new QuartusWrongNodeBlockException();
    }
}
