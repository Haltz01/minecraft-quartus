package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class QuartusOutput extends QuartusNode {
    public QuartusOutput(World world, BlockPos pos) {
        super(world, pos);
    }

    public abstract void setInGameValue(boolean newValue);

    @Override
    public String getNodeType() {
        return "GenericOutput";
    }
}
