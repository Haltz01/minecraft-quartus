package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class QuartusResolvableNode extends QuartusNode {
    public QuartusResolvableNode(World world, BlockPos pos) {
        super(world, pos);
    }

    public void resolve() {
        for (QuartusNode inputNode: getInputs()) {
            inputNode.removeOutput(this);
            inputNode.addOutput(getOutputs());
        }

        for (QuartusNode outputNode: getOutputs()) {
            outputNode.removeInput(this);
            outputNode.addInput(getInputs());
        }
    }
}
