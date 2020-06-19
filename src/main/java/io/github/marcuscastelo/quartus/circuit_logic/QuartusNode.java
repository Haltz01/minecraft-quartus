package io.github.marcuscastelo.quartus.circuit_logic;

import io.github.marcuscastelo.quartus.block.circuit_components.AbstractGateBlock;
import io.github.marcuscastelo.quartus.block.circuit_components.AndGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class QuartusNode {
    public static class QuartusWrongNodeBlockException extends Exception {}

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

    public List<QuartusNode> getInputs() {
        return new ArrayList<>(inputs);
    }
    public List<QuartusNode> getOutputs() {
        return new ArrayList<>(outputs);
    }

    public void addInput(QuartusNode... from) {
        inputs.addAll(Arrays.asList(from));
    }
    public void addInput(List<QuartusNode> from) {
        inputs.addAll(from);
    }
    public void addOutput(QuartusNode... to) {
        outputs.addAll(Arrays.asList(to));
    }
    public void addOutput(List<QuartusNode> to) {
        outputs.addAll(to);
    }

    public void removeInput(QuartusNode... node) { inputs.removeAll(Arrays.asList(node)); }
    public void removeOutput(QuartusNode... node) { outputs.removeAll(Arrays.asList(node)); }

    //JÁ FUNCIONA

    public BlockPos getPos() {return this.pos;}
    public World getWorld() {return this.world;}
    public BlockState getBlockState() {
        return world.getBlockState(pos);
    }

    public boolean isDirectionInput(Direction direction) {
        return getPossibleInputDirections().contains(direction);
    }
    public boolean isDirectionOutput(Direction direction) {
        return getPossibleOutputDirections().contains(direction);
    }

    public List<Direction> getPossibleInputDirections() {
        return ((QuartusNodeConvertible)world.getBlockState(pos).getBlock()).getPossibleInputDirections(world, pos);
    }

    public List<Direction> getPossibleOutputDirections() {
        return ((QuartusNodeConvertible)world.getBlockState(pos).getBlock()).getPossibleOutputDirections(world, pos);
    }

    @Override
    public String toString() {
        return this.getNodeType() + "@" + pos.toShortString();
    }

    public long getUniqueID() { return pos.asLong(); }

    public String getNodeType() {
        return "GenericNode";
    }
}
