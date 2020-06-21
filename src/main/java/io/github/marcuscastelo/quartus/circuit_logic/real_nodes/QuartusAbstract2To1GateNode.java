package io.github.marcuscastelo.quartus.circuit_logic.real_nodes;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.List;

public class QuartusAbstract2To1GateNode extends QuartusNode {
    @Override
    public List<Direction> getRelativeInputDirections() {
        return Arrays.asList(Direction.EAST, Direction.WEST);
    }

    @Override
    public List<Direction> getRelativeOutputDirections() {
        return Arrays.asList(Direction.NORTH);
    }
}
