package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class QuartusInput extends QuartusNode {
    public QuartusInput(World world, BlockPos pos) {
        super(world, pos);
    }

    List<Direction> DIRECTIONS_NONE = new ArrayList<>();

    @Override
    public List<Direction> getPossibleOutputDirections() {
        return CircuitUtils.getHorizontalDirections();
    }

    @Override
    public List<Direction> getPossibleInputDirections() {
        return DIRECTIONS_NONE;
    }

    public abstract boolean getInGameValue();

    @Override
    public String getNodeType() {
        return "Input";
    }
}
