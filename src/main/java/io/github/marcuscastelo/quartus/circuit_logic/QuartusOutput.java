package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class QuartusOutput extends QuartusNode {
    public QuartusOutput(World world, BlockPos pos) {
        super(world, pos);
    }

    List<Direction> DIRECTIONS_NONE = new ArrayList<>();

    public abstract void setInGameValue(boolean newValue);

    @Override
    public List<Direction> getPossibleOutputDirections() {
        return DIRECTIONS_NONE;
    }

    @Override
    public List<Direction> getPossibleInputDirections() {
        return CircuitUtils.getHorizontalDirections();
    }

    @Override
    public String getNodeType() {
        return "Output";
    }
}
