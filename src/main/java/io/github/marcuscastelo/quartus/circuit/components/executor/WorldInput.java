package io.github.marcuscastelo.quartus.circuit.components.executor;

import io.github.marcuscastelo.quartus.circuit.QuartusBusInfo;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitInput;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WorldInput extends QuartusCircuitInput {
    public final World world;
    public final BlockPos pos;

    public WorldInput(World world, BlockPos pos, int ID) {
        super(ID);
        this.world = world;
        this.pos = pos;
    }

    @Override
    public void updateComponent() {
        QuartusBusInfo inputBus = getInputInfo().get(Direction.SOUTH);

        try {
            boolean powered = world.isReceivingRedstonePower(pos);
            inputBus.setValue(powered);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        //Propagate input -> output
        super.updateComponent();
    }
}
