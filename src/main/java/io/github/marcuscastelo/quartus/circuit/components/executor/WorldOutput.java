package io.github.marcuscastelo.quartus.circuit.components.executor;

import io.github.marcuscastelo.quartus.circuit.QuartusBusInfo;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitOutput;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;

public class WorldOutput extends QuartusCircuitOutput {
    public final World world;
    public final BlockPos pos;

    public WorldOutput(World world, BlockPos pos, int ID) {
        super(ID);
        this.world = world;
        this.pos = pos;
    }

    @Override
    public void updateComponent() {
        //Propagate input -> output
        super.updateComponent();
        QuartusBusInfo outputBus = getOutputInfo().get(Direction.NORTH);

        BlockState blockState = world.getBlockState(pos);
        try {
            world.setBlockState(pos, blockState.with(Properties.POWERED, outputBus.equals(QuartusBusInfo.HIGH1b)));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
