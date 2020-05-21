package io.github.marcuscastelo.quartus.circuit_logic.native_programs;

import io.github.marcuscastelo.quartus.block.circuit_components.AndGateBlock;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusCircuit;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import io.github.marcuscastelo.quartus.registry.QuartusBlocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class AndGateNode extends QuartusNode {

    public AndGateNode(World world, BlockPos pos) throws QuartusWrongNodeBlockException {
        super(world, pos);
        if (!(world.getBlockState(pos).getBlock() instanceof AndGateBlock)) throw new QuartusWrongNodeBlockException();
    }

    @Override
    public String getNodeType() {
        return "AndGate";
    }

}
