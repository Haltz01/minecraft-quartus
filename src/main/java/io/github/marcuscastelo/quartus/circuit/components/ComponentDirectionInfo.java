package io.github.marcuscastelo.quartus.circuit.components;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe que define a direção que a informação que passa
 * por um componente deve seguir, de acordo com seus inputs e outputs
 */
public class ComponentDirectionInfo {
    public final ImmutableList<Direction> possibleInputDirections, possibleOutputDirections;
    public final ImmutableMap<Direction, Integer> numberOfBusesInDirectionMap;

    public ComponentDirectionInfo(List<Direction> possibleInputDirections, List<Direction> possibleOutputDirections, Pair<Direction, Integer> ...busesCountInDirectionArray) {
        Map<Direction, Integer> _numberOfBusesInDirectionMap = new HashMap<>();
        this.possibleInputDirections = ImmutableList.copyOf(possibleInputDirections);
        this.possibleOutputDirections = ImmutableList.copyOf(possibleOutputDirections);

        //Por padrão, cada direção possui apenas um bus
        for (Direction inputDirection: possibleInputDirections) {
            _numberOfBusesInDirectionMap.put(inputDirection, 1);
        }

        for (Direction outputDirection: possibleOutputDirections) {
            _numberOfBusesInDirectionMap.put(outputDirection, 1);
        }

        //Mas se for especificado, é possível ter mais de um bus por direção
        if (busesCountInDirectionArray != null && busesCountInDirectionArray.length != 0) {
            for (Pair<Direction, Integer> pairToAdd: busesCountInDirectionArray) {
                if (pairToAdd == null) continue;

                Direction direction = pairToAdd.getLeft();
                Integer busCount = pairToAdd.getRight();

                if (!possibleInputDirections.contains(direction) && !possibleOutputDirections.contains(direction))
                    throw new IllegalArgumentException("Trying to define a impossible direction's bus count");

                if (busCount == 0) throw new IllegalArgumentException("Trying to define a busCount to 0");
                _numberOfBusesInDirectionMap.put(direction, busCount);
            }
        }

        numberOfBusesInDirectionMap = ImmutableMap.copyOf(_numberOfBusesInDirectionMap);
    }

    public ComponentDirectionInfo(List<Direction> possibleInputDirections, List<Direction> possibleOutputDirections) {
        this(possibleInputDirections, possibleOutputDirections, (Pair<Direction, Integer>) null);
    }

    public ComponentDirectionInfo(Direction possibleInputDirection, List<Direction> possibleOutputDirections) {
        this(Collections.singletonList(possibleInputDirection), possibleOutputDirections);
    }

    public ComponentDirectionInfo(List<Direction> possibleInputDirections, Direction possibleOutputDirection) {
        this(possibleInputDirections, Collections.singletonList(possibleOutputDirection));
    }

    public ComponentDirectionInfo(Direction possibleInputDirection, Direction possibleOutputDirection) {
        this(Collections.singletonList(possibleInputDirection), Collections.singletonList(possibleOutputDirection));
    }

    public int getNumberOfBusesInDirection(Direction direction) {
        if (!numberOfBusesInDirectionMap.containsKey(direction)) return 0;
        if (!possibleInputDirections.contains(direction) && !possibleOutputDirections.contains(direction)) return 0;
        return numberOfBusesInDirectionMap.getOrDefault(direction, 0);
    }
}
