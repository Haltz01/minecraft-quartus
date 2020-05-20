package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class QuartusNode {
    private final List<QuartusNode> inputs;
    private final List<QuartusNode> outputs;

    private boolean outputValue;
    private boolean updated;

    World world;
    BlockPos pos;

    public QuartusNode(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;

        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.outputValue = false;
    }

    //VINICIUS MUDAR

    protected abstract boolean calcOutputValue();

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean isUpdated() {
        return updated;
    }

    public boolean getOutputValue() {
        return this.outputValue;
    }

    public void updateOutputValue() {

    }

    public List<QuartusNode> getInputs() {
        return inputs;
    }

    public List<QuartusNode> getOutputs() {
        return outputs;
    }

    public void addInput(QuartusNode from) {
        inputs.add(from);
    }
    public void addOutput(QuartusNode from) {
        outputs.add(from);
    }


    //JÁ FUNCIONA

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

    @Override
    public String toString() {
        return this.getNodeType() + " " + pos.toShortString();
    }

    public long getUniqueID() { return pos.asLong(); }

    public String getNodeType() {
        return "GenericNode";
    }
}
