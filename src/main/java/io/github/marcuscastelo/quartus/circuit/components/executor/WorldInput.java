package io.github.marcuscastelo.quartus.circuit.components.executor;

import io.github.marcuscastelo.quartus.circuit.ComponentConnection;
import io.github.marcuscastelo.quartus.circuit.QuartusBus;
import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.circuit.components.CircuitInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class WorldInput extends CircuitInput {
    public final World world;
    public final BlockPos pos;

    public WorldInput(World world, BlockPos pos, CircuitInput inputImport) {
        super(inputImport.getID());
        this.world = world;
        this.pos = pos;

        Map<Direction, List<ComponentConnection>> connectionMap = inputImport.getConnections();
        for (Map.Entry<Direction, List<ComponentConnection>> connectionEntry: connectionMap.entrySet()) {
            Direction direction = connectionEntry.getKey();
            List<ComponentConnection> connectionsToImport = connectionEntry.getValue();
            this.getConnections().get(direction).addAll(connectionsToImport);
        }
    }

    @Override
    public void updateComponent(QuartusCircuit circuit) {
        QuartusBus inputBus = getExecutionInfo().getInput(Direction.SOUTH).get(0);

        try {
            boolean powered = world.isReceivingRedstonePower(pos);
            inputBus.setValue(powered);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        //Propagate input -> output
        super.updateComponent(circuit);
    }
}
