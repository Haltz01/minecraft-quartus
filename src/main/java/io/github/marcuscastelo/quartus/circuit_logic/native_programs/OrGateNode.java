package io.github.marcuscastelo.quartus.circuit_logic.native_programs;

import io.github.marcuscastelo.quartus.block.circuit_components.NorGateBlock;
import io.github.marcuscastelo.quartus.block.circuit_components.OrGateBlock;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusCircuit;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OrGateNode extends QuartusNode {
    public OrGateNode(World world, BlockPos pos) throws QuartusWrongNodeBlockException {
        super(world, pos);
        if (!(world.getBlockState(pos).getBlock() instanceof OrGateBlock)) throw new QuartusWrongNodeBlockException();
    }

    @Override
    public String getNodeType() {
        return "OrGate";
    }
}
