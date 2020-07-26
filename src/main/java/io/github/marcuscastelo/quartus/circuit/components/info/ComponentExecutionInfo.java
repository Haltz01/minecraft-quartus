package io.github.marcuscastelo.quartus.circuit.components.info;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.circuit.QuartusBus;
import io.github.marcuscastelo.quartus.network.QuartusSerializer;
import io.github.marcuscastelo.quartus.network.QuartusSimetricSerializer;
import net.minecraft.client.render.model.CubeFace;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.SerializationException;

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
        if (!outputInfo.containsKey(direction)) //TODO: tirar debug
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

    public ImmutableMap<Direction, List<QuartusBus>> getInputInfo() { return ImmutableMap.copyOf(inputInfo); }
    public ImmutableMap<Direction, List<QuartusBus>> getOutputInfo() { return ImmutableMap.copyOf(outputInfo); }

    public static class Serializer implements QuartusSimetricSerializer<ComponentExecutionInfo, String> {
        /*
       North:>1,1,1,1|1,1,1,1#South:>...
    */
        @Override
        public String serialize(ComponentExecutionInfo componentExecutionInfo) {
            StringBuilder builder = new StringBuilder();
            QuartusBus.Serializer busSerializer = new QuartusBus.Serializer();
            for (Map.Entry<Direction, List<QuartusBus>> inputEntry: componentExecutionInfo.inputInfo.entrySet()) {
                builder.append(inputEntry.getKey().getName());
                builder.append("_I");
                builder.append(":>");
                for (QuartusBus bus: inputEntry.getValue()) {
                    builder.append(busSerializer.serialize(bus));
                }
                builder.append("#");
            }

            for (Map.Entry<Direction, List<QuartusBus>> inputEntry: componentExecutionInfo.outputInfo.entrySet()) {
                builder.append(inputEntry.getKey().getName());
                builder.append("_O");
                builder.append(":>");
                for (QuartusBus bus: inputEntry.getValue()) {
                    builder.append(busSerializer.serialize(bus));
                }
                builder.append("#");
            }
            return builder.toString();
        }

        @Override
        public ComponentExecutionInfo unserialize(String serial) {
            List<Direction> inputDirections = new ArrayList<>();
            List<Direction> outputDirections = new ArrayList<>();
            List<Map.Entry<Direction, ImmutableList<QuartusBus>>> inputDirectionEntries = new ArrayList<>();
            List<Map.Entry<Direction, ImmutableList<QuartusBus>>> outputDirectionEntries = new ArrayList<>();


            String[] pairs = serial.split("#");
            for (String pair: pairs) {
                String[] parts = pair.split(":>");
                if (parts.length != 2) throw new SerializationException();
                String[] directionInfo = parts[0].split("_");
                if (directionInfo.length != 2) throw new SerializationException();
                String directionName = directionInfo[0];
                String directionType = directionInfo[1];
                Direction dir = Direction.byName(directionName);
                String[] busesParts = parts[1].split("\\|");
                List<QuartusBus> buses = new ArrayList<>();
                for (String busInfo: busesParts) {
                    List<Boolean> values = Arrays.stream(busInfo.split(",")).map(b->b.equals("1")).collect(Collectors.toList());
                    buses.add(new QuartusBus(values));
                }
                if (directionType.equals("I")) {
                    if (!inputDirections.contains(dir)) inputDirections.add(dir);
                    inputDirectionEntries.add(new AbstractMap.SimpleEntry<>(dir, ImmutableList.copyOf(buses)));
                } else if (directionType.equals("O")) {
                    if (!outputDirections.contains(dir)) outputDirections.add(dir);
                    outputDirectionEntries.add(new AbstractMap.SimpleEntry<>(dir, ImmutableList.copyOf(buses)));
                } else throw new SerializationException("Unknown directionType " + directionType);
            }

            ComponentDirectionInfo directionInfo = new ComponentDirectionInfo(inputDirections, outputDirections);
            ComponentExecutionInfo executionInfo = new ComponentExecutionInfo(directionInfo);
            for (Map.Entry<Direction, ImmutableList<QuartusBus>> entry: inputDirectionEntries)
                executionInfo.setInput(entry.getKey(), entry.getValue());
            for (Map.Entry<Direction, ImmutableList<QuartusBus>> entry: outputDirectionEntries)
                executionInfo.setOutput(entry.getKey(), entry.getValue());

            return executionInfo;
        }
    }
}
