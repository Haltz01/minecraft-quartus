package io.github.marcuscastelo.quartus.registry;

import com.google.common.collect.ImmutableList;
import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.circuit.QuartusBus;
import io.github.marcuscastelo.quartus.circuit.QuartusLogic;
import io.github.marcuscastelo.quartus.circuit.components.CircuitInput;
import io.github.marcuscastelo.quartus.circuit.components.CircuitOutput;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe que contém as lógicas de cada componente (portas lógicas, entrada, saída, multiplexador, extensor e distribuidor)
 */
public class QuartusLogics {
    // Map que associa a lógica de cada componente ao nome dele
    private static final Map<String, QuartusLogic> logicPerName = new HashMap<>();

    /**
     * Método responsável por registrar as lógicas de cada componenete em um map
     * @param logicName   Nome que define a lógica do componenete (ex.: 'AndGate' refere-se à lógica da porta AND)
     * @param logic       Lógica do componenente
     * @return            Lógica registrada
     */
    public static QuartusLogic register(String logicName, QuartusLogic logic) {
        logicPerName.putIfAbsent(logicName, logic);
        return logic;
    }

    /**
     * Método responsável por obter a lógica de um componenete já registrado no map
     * @param logicName     Nome que define a lógica do componenete
     * @return              Lógica obtida do map (previamente registrada)
     */
    public static QuartusLogic getLogicByName(String logicName) {
        if (!logicPerName.containsKey(logicName)) throw new IllegalArgumentException("Unknown logic " + logicName);
        return logicPerName.get(logicName);
    }

    public static final QuartusLogic AND_GATE;
    public static final QuartusLogic NAND_GATE;
    public static final QuartusLogic OR_GATE;
    public static final QuartusLogic NOR_GATE;
    public static final QuartusLogic XOR_GATE;
    public static final QuartusLogic XNOR_GATE;
    public static final QuartusLogic NOT_GATE;
    public static final QuartusLogic INPUT;
    public static final QuartusLogic OUTPUT;
    public static final QuartusLogic MULTIPLEXER;
    public static final QuartusLogic EXTENSOR;
    public static final QuartusLogic DISTRIBUTOR;

    static {
        AND_GATE = register("AndGate", (executionInfo) -> executionInfo.setOutput(Direction.NORTH, executionInfo.getAllInputs().stream().reduce(QuartusBus::bitwiseAnd)));
        NAND_GATE = register("NandGate", (executionInfo) -> executionInfo.setOutput(Direction.NORTH, executionInfo.getAllInputs().stream().reduce(QuartusBus::bitwiseNand)));
        OR_GATE = register("OrGate", (executionInfo) -> executionInfo.setOutput(Direction.NORTH, executionInfo.getAllInputs().stream().reduce(QuartusBus::bitwiseOr)));
        NOR_GATE = register("NorGate", (executionInfo) -> executionInfo.setOutput(Direction.NORTH, executionInfo.getAllInputs().stream().reduce(QuartusBus::bitwiseNor)));
        XOR_GATE = register("XorGate", (executionInfo) -> executionInfo.setOutput(Direction.NORTH, executionInfo.getAllInputs().stream().reduce(QuartusBus::bitwiseXor)));
        XNOR_GATE = register("XnorGate", (executionInfo) -> executionInfo.setOutput(Direction.NORTH, executionInfo.getAllInputs().stream().reduce(QuartusBus::bitwiseXnor)));
        NOT_GATE = register("NotGate", (executionInfo) -> executionInfo.setOutput(Direction.NORTH, executionInfo.getInput(Direction.SOUTH).get(0).bitwiseNot()));

        //Os inputs e outputs são meros marcadores e por isso não possuem lógica interna (apenas repassam do sul para o norte relativos)
        QuartusLogic copyInputToOutput = ((executionInfo) -> executionInfo.setOutput(Direction.NORTH, executionInfo.getInput(Direction.SOUTH)));
        INPUT = register(CircuitInput.COMP_NAME, copyInputToOutput);
        OUTPUT = register(CircuitOutput.COMP_NAME, copyInputToOutput);

        //TODO: criar alguma medida para impedir que o multiplexer receba um extensor em qualquer lado exceto na saida
        MULTIPLEXER = register("MultiplexerGate", ((executionInfo) -> {
            QuartusBus selectorBusInfo = executionInfo.getInput(Direction.SOUTH).get(0);
            if (selectorBusInfo.getBusSize() != 1) {
                //TODO: support multibyte selector
                Quartus.LOGGER.warn("Ignoring multiplexer with multibyte selector");
                return;
            }

            QuartusBus westBusInfo = executionInfo.getInput(Direction.WEST).get(0);
            QuartusBus eastBusInfo = executionInfo.getInput(Direction.EAST).get(0);
            boolean pickEast = selectorBusInfo.equals(QuartusBus.HIGH1b);

            executionInfo.setOutput(Direction.NORTH, pickEast? eastBusInfo: westBusInfo);
        }));

        //TODO: definir lógica (talvez uma classe com override no updateComponent)
        EXTENSOR = register("ExtensorGate", ((executionInfo) -> {
            List<QuartusBus> inputBuses = new ArrayList<>(3);
            inputBuses.addAll(executionInfo.getAllInputs());
//            executionInfo.setOutput(Direction.NORTH, );
        }));

        DISTRIBUTOR = register("DistributorGate", ((executionInfo) -> {
            ImmutableList<QuartusBus> inputInfo = executionInfo.getInput(Direction.SOUTH);
            executionInfo.setOutput(Direction.NORTH, inputInfo);
            executionInfo.setOutput(Direction.WEST, inputInfo);
            executionInfo.setOutput(Direction.EAST, inputInfo);
        }));
    }
}