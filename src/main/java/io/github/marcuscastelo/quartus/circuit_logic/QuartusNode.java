package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class QuartusNode {
    public static int LAST_ID = 1;
    public static class QuartusWrongNodeBlockException extends Exception {}

    private final List<QuartusNode> inputs;
    private final List<QuartusNode> outputs;

    public int getID() {
        return ID;
    }

    public final int ID = LAST_ID++;

    public QuartusNode() {
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }

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

    public boolean isDirectionInput(Direction direction) {return getRelativeInputDirections().contains(direction); }
    public boolean isDirectionOutput(Direction direction) {
        return getRelativeOutputDirections().contains(direction);
    }

    public abstract List<Direction> getRelativeInputDirections();
    public abstract List<Direction> getRelativeOutputDirections();

    @Override
    public String toString() {
        return this.getNodeType() + "#" + getID();
    }

    public String getNodeType() {
        return "GenericNode";
    }
}
