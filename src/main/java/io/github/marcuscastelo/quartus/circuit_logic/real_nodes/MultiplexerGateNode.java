package io.github.marcuscastelo.quartus.circuit_logic.real_nodes;

import io.github.marcuscastelo.quartus.block.circuit_components.MultiplexerGateBlock;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusWorldNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class MultiplexerGateNode extends QuartusNode {

    @Override
    public List<Direction> getRelativeInputDirections() {
        return Arrays.asList(Direction.WEST, Direction.EAST, Direction.SOUTH);
    }

    @Override
    public List<Direction> getRelativeOutputDirections() {
        return Arrays.asList(Direction.NORTH);
    }

    @Override
    public String getNodeType() {
        return "MultiplexerGate";
    }
}
