package io.github.marcuscastelo.quartus.circuit_logic.native_programs;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusCircuit;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AndGateNode extends QuartusNode {

    public AndGateNode(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public String getNodeType() {
        return "AndGate";
    }

    @Override
    protected boolean calcOutputValue() {
        for (QuartusNode inp: getInputs()) {
            if (!inp.getOutputValue()) return false;
        }
        return true;
    }
}
