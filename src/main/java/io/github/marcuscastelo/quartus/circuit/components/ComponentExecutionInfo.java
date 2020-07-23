package io.github.marcuscastelo.quartus.circuit.components;

import com.google.common.collect.ImmutableList;
import io.github.marcuscastelo.quartus.circuit.QuartusBus;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentExecutionInfo {
    private final ComponentDirectionInfo componentDirectionInfo;
    public final Map<Direction, List<QuartusBus>> outputInfo;
    public final Map<Direction, List<QuartusBus>> inputInfo;

    public ComponentExecutionInfo(ComponentDirectionInfo componentDirectionInfo) {
        this.inputInfo = new HashMap<>();
        this.outputInfo = new HashMap<>();
        this.componentDirectionInfo = componentDirectionInfo;

        for (Direction dir : componentDirectionInfo.possibleInputDirections) {
            int busesInThisDirection = componentDirectionInfo.getNumberOfBusesInDirection(dir);
            inputInfo.put(dir, new ArrayList<>(busesInThisDirection));
            for (int i = 0; i < busesInThisDirection; i++) inputInfo.get(dir).add(new QuartusBus(false));
        }

        for (Direction dir : componentDirectionInfo.possibleOutputDirections) {
            int busesInThisDirection = componentDirectionInfo.getNumberOfBusesInDirection(dir);
            outputInfo.put(dir, new ArrayList<>(busesInThisDirection));
            for (int i = 0; i < busesInThisDirection; i++) outputInfo.get(dir).add(new QuartusBus(false));
        }
    }

    public ImmutableList<QuartusBus> getInput(Direction direction) {
        if (!componentDirectionInfo.possibleInputDirections.contains(direction))
            throw new IllegalArgumentException("Trying to get input at invalid direction");
        if (!inputInfo.containsKey(direction))
            throw new RuntimeException("Erro de programação: inputInfo não tem a chave que deveria ter");
        return ImmutableList.copyOf(inputInfo.get(direction));
    }

    public ImmutableList<QuartusBus> getOutput(Direction direction) {
        if (!componentDirectionInfo.possibleOutputDirections.contains(direction))
            throw new IllegalArgumentException("Trying to get output at invalid direction");
        if (!outputInfo.containsKey(direction))
            throw new RuntimeException("Erro de programação: outputInfo não tem a chave que deveria ter");
        return ImmutableList.copyOf(outputInfo.get(direction));
    }


    public ImmutableList<QuartusBus> getAllInputs() {
        List<QuartusBus> allInputs = new ArrayList<>();
        for (Direction direction: componentDirectionInfo.possibleInputDirections) {
            allInputs.addAll(getInput(direction));
        }
        return ImmutableList.copyOf(allInputs);
    }

    public ImmutableList<QuartusBus> getOutputs() {
        List<QuartusBus> allOutputs = new ArrayList<>();
        for (Direction direction: componentDirectionInfo.possibleOutputDirections) {
            allOutputs.addAll(getInput(direction));
        }
        return ImmutableList.copyOf(allOutputs);
    }


    public void setOutput(Direction direction, QuartusBus ...buses) {
        setOutput(direction, Arrays.stream(buses).map(Optional::of).collect(Collectors.toList()));
    }

    @SafeVarargs
    public final void setOutput(Direction direction, Optional<QuartusBus>... busesInfo) {
        setOutput(direction, Arrays.asList(busesInfo));
    }

    public void setOutput(Direction direction, ImmutableList<QuartusBus> buses) {
        setOutput(direction, buses.stream().map(Optional::of).collect(Collectors.toList()));
    }

    public void setOutput(Direction direction, List<Optional<QuartusBus>> busesInfo) {
        if (!componentDirectionInfo.possibleOutputDirections.contains(direction))
            throw new IllegalArgumentException("Trying to set output at invalid direction");
        if (!outputInfo.containsKey(direction))
            throw new RuntimeException("Erro de programação: outputInfo não tem a chave que deveria ter");
        outputInfo.get(direction).clear();
        outputInfo.get(direction).addAll(busesInfo.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));

    }

    public void setInput(Direction direction, QuartusBus ...buses) {
        setInput(direction, Arrays.stream(buses).map(Optional::of).collect(Collectors.toList()));
    }

    @SafeVarargs
    public final void setInput(Direction direction, Optional<QuartusBus>... busesInfo) {
        setInput(direction, Arrays.asList(busesInfo));
    }

    public void setInput(Direction direction, ImmutableList<QuartusBus> buses) {
        setInput(direction, buses.stream().map(Optional::of).collect(Collectors.toList()));
    }

    public void setInput(Direction direction, List<Optional<QuartusBus>> busesInfo) {
        if (!componentDirectionInfo.possibleInputDirections.contains(direction))
            throw new IllegalArgumentException("Trying to set input at invalid direction");
        if (!inputInfo.containsKey(direction))
            throw new RuntimeException("Erro de programação: inputInfo não tem a chave que deveria ter");
        inputInfo.get(direction).clear();
        inputInfo.get(direction).addAll(busesInfo.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }
}
