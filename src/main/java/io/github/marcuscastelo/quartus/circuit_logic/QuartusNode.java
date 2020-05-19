package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class QuartusNode {
    private static final HashMap<String, Integer> nextIDForNodeType = new HashMap<>();
    private final int ID;

    World world;
    BlockPos pos;
    public QuartusNode(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;

        //Funciona para subclasses também
        String nodeType = this.getNodeType();
        this.ID = nextIDForNodeType.getOrDefault(nodeType, 0);
        nextIDForNodeType.put(nodeType, ID + 1);
    }

    public BlockPos getPos() {return this.pos;}
    public World getWorld() {return this.world;}
    public BlockState getBlockState() {
        return world.getBlockState(pos);
    }

    public boolean isDirectionInput(Direction direction) {
        return getPossibleInputDirections().contains(direction);
    }

    public abstract List<Direction> getPossibleOutputDirections();
    public abstract List<Direction> getPossibleInputDirections();

    public int getID() { return this.ID; }

    @Override
    public String toString() {
        return this.getNodeType() + getID();
    }

    public String getNodeType() {
        return "GenericNode";
    }
}
