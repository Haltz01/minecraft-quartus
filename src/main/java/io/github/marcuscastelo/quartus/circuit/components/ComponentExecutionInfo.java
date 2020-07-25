package io.github.marcuscastelo.quartus.circuit.components;

import com.google.common.collect.ImmutableList;
import io.github.marcuscastelo.quartus.circuit.QuartusBus;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe que gerencia as informações que passam através dos blocos do circuito
 */
public class ComponentExecutionInfo {
	//Variáveis que mapeiam os componentes e armazenam a direção da informação
    private final ComponentDirectionInfo componentDirectionInfo;
    public final Map<Direction, List<QuartusBus>> outputInfo;
    public final Map<Direction, List<QuartusBus>> inputInfo;

	/**
	 * Construção padrão da classe ComponentExecutionInfo
	 */
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

	/**
	 * Método que retorna uma lista com um Bus contendo
	 * os inputs do Executor
	 * @param direction	Direção a ser seguida
	 * @return	Lista com as direções dos inputs
	 */
    public ImmutableList<QuartusBus> getInput(Direction direction) {
        if (!componentDirectionInfo.possibleInputDirections.contains(direction))
            throw new IllegalArgumentException("Trying to get input at invalid direction " + direction );
        if (!inputInfo.containsKey(direction))
            throw new RuntimeException("Erro de programação: inputInfo não tem a chave que deveria ter");
        return ImmutableList.copyOf(inputInfo.get(direction));
    }

	/**
	 * Método que retorna uma lista com um Bus contendo
	 * os outputs do Executor
	 * @param direction	Direção a ser seguida
	 * @return	Lista com as direções dos outputs
	 */
    public ImmutableList<QuartusBus> getOutput(Direction direction) {
        if (!componentDirectionInfo.possibleOutputDirections.contains(direction))
            throw new IllegalArgumentException("Trying to get output at invalid direction");
        if (!outputInfo.containsKey(direction))
            throw new RuntimeException("Erro de programação: outputInfo não tem a chave que deveria ter");
        return ImmutableList.copyOf(outputInfo.get(direction));
    }

	/**
	 * Método que retorna uma lista com todos
	 * os Inputs do Executor
	 * @return	Lista com todos os inputs detectados
	 */
    public ImmutableList<QuartusBus> getAllInputs() {
        List<QuartusBus> allInputs = new ArrayList<>();
        for (Direction direction: componentDirectionInfo.possibleInputDirections) {
            allInputs.addAll(getInput(direction));
        }
        return ImmutableList.copyOf(allInputs);
    }

	/**
	 * Método que retorna uma lista com todos
	 * os Outputs do Executor
	 * @return	Lista com todos os outputs detectados
	 */
    public ImmutableList<QuartusBus> getOutputs() {
        List<QuartusBus> allOutputs = new ArrayList<>();
        for (Direction direction: componentDirectionInfo.possibleOutputDirections) {
            allOutputs.addAll(getOutput(direction));
        }
        return ImmutableList.copyOf(allOutputs);
    }

	/**
	 * Método auxiliar que setta os outputs conforme
	 * a direção e os Buses dados
	 * @param direction	Direção a ser seguida
	 * @param buses	Buses do circuito com os dados
	 */
    public void setOutput(Direction direction, QuartusBus ...buses) {
        setOutput(direction, Arrays.stream(buses).map(Optional::of).collect(Collectors.toList()));
    }

	/**
	 * Método que setta os Outputs conforme a
	 * direção e os Buses dados
	 * @param direction	Direção a ser seguida
	 * @param busesInfo	Buses com as informações do circuito
	 */
    @SafeVarargs
    public final void setOutput(Direction direction, Optional<QuartusBus>... busesInfo) {
        setOutput(direction, Arrays.asList(busesInfo));
    }

	/**
	 * Método que Setta os outputs conforme
	 * a direção e os buses dados
	 * @param direction	Direção a ser seguida
	 * @param buses	Buses do circuito com as informações
	 */
    public void setOutput(Direction direction, ImmutableList<QuartusBus> buses) {
        setOutput(direction, buses.stream().map(Optional::of).collect(Collectors.toList()));
    }

	/**
	 * Método que setta os outputs conforme
	 * a direção e os buses dados
	 * @param direction	Direção a ser seguida
	 * @param busesInfo	informação contida nos buses do circuito
	 */
    public void setOutput(Direction direction, List<Optional<QuartusBus>> busesInfo) {
        if (!componentDirectionInfo.possibleOutputDirections.contains(direction))
            throw new IllegalArgumentException("Trying to set output at invalid direction");
        if (!outputInfo.containsKey(direction))
            throw new RuntimeException("Erro de programação: outputInfo não tem a chave que deveria ter");
        outputInfo.get(direction).clear();
        outputInfo.get(direction).addAll(busesInfo.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));

    }

	/**
	 * Método que setta o input conforme
	 * a direção e os buses dados
	 * @param direction	Direção a ser seguida
	 * @param buses	Buses com as informações
	 */
    public void setInput(Direction direction, QuartusBus ...buses) {
        setInput(direction, Arrays.stream(buses).map(Optional::of).collect(Collectors.toList()));
    }

	/**
	 * Método que setta o input conforme
	 * a direção e os buses dados
	 * @param direction	Direção a ser seguida
	 * @param buses	Buses com as informações
	 */
    @SafeVarargs
    public final void setInput(Direction direction, Optional<QuartusBus>... buses) {
        setInput(direction, Arrays.asList(buses));
    }

	/**
	 * Método que setta o input conforme
	 * a direção e os buses dados
	 * @param direction	Direção a ser seguida
	 * @param buses	Buses com as informações
	 */
    public void setInput(Direction direction, ImmutableList<QuartusBus> buses) {
        setInput(direction, buses.stream().map(Optional::of).collect(Collectors.toList()));
    }

	/**
	 * Método que setta o input conforme
	 * a direção e os buses dados
	 * @param direction	Direção a ser seguida
	 * @param buses	Buses com as informações
	 */
    public void setInput(Direction direction, List<Optional<QuartusBus>> buses) {
        if (!componentDirectionInfo.possibleInputDirections.contains(direction))
            throw new IllegalArgumentException("Trying to set input at invalid direction");
        if (!inputInfo.containsKey(direction))
            throw new RuntimeException("Erro de programação: inputInfo não tem a chave que deveria ter");
        inputInfo.get(direction).clear();
        inputInfo.get(direction).addAll(buses.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }
}
