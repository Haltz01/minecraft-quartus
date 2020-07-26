package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.circuit.QuartusLogic;
import io.github.marcuscastelo.quartus.circuit.components.*;
import io.github.marcuscastelo.quartus.circuit.components.info.ComponentDirectionInfo;
import io.github.marcuscastelo.quartus.circuit.components.info.ComponentInfo;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Classe que gerencia as informações do blocos do Mod
 */
public class QuartusCircuitComponents {
    // Descreve as direções de um componenete cujos inputs são oeste, leste e outputs são norte (direções relativas a direção para que o componente olha) [West, East to North Direction Info]
    public static final ComponentDirectionInfo WE2NDirInfo = new ComponentDirectionInfo(Arrays.asList(Direction.EAST, Direction.WEST), Direction.NORTH);
    // Descreve as direções de um componenete cujos inputs são oeste, leste, sul e outputs são norte (direções relativas a direção para que o componente olha) [West, East, South to North Direction Info]
    public static final ComponentDirectionInfo WES2NDirInfo = new ComponentDirectionInfo(Arrays.asList(Direction.EAST, Direction.WEST, Direction.SOUTH), Direction.NORTH);
    // Descreve as direções de um componenete cujos inputs são sul e outputs são norte, leste, oeste (direções relativas a direção para que o componente olha) [South to North, East, West Direction Info]
    public static final ComponentDirectionInfo S2NEWDirInfo = new ComponentDirectionInfo(Direction.SOUTH, Arrays.asList(Direction.NORTH, Direction.EAST, Direction.WEST));

    // Map que guarda a informação de cada tipo de componenete
    public static final Map<String, ComponentInfo> componentInfoPerComponentName = new HashMap<>();

    /**
     * Método responsável por registrar um tipo de componente (ex.: as portas lógicas)
     * @param componentName     Nome do componente a ser registrado
     * @param directionInfo     Informação sobre a direção relativa de input/output do componente
     * @param componentLogic    Lógica do componente (ex.: portas lógicas AND, OR, XOR, NOT etc.)
     * @return                  Classe com as informações do tipo de componenete que foi registrado
     */
    public static ComponentInfo registerComponent(String componentName, ComponentDirectionInfo directionInfo, QuartusLogic componentLogic) {
        Supplier<CircuitComponent> componentSupplier = () -> new CircuitComponent(componentName, directionInfo, componentLogic);
        return registerSpecialComponent(componentName, directionInfo, componentLogic, componentSupplier);
    }

    /**
     * Método responsável por registrar componentes especiais: utilizam classes que herdam da classe CircuitComponent (input e output)
     * @param componentName         Nome do componenete a ser registrado
     * @param directionInfo         Informação sobre a direção relativa de input/output do componente
     * @param componentLogic        Lógica do componente
     * @param componentSupplier     Construtor da classe que herda CircuitComponent
     * @return                      Classe com as informações do tipo de componenete que foi registrado
     */
    public static ComponentInfo registerSpecialComponent(String componentName, ComponentDirectionInfo directionInfo, QuartusLogic componentLogic, Supplier<CircuitComponent> componentSupplier) {
        ComponentInfo info = new ComponentInfo(componentSupplier, directionInfo, componentLogic);
        componentInfoPerComponentName.putIfAbsent(componentName, info);
        return info;
    }

    /**
     * Método que obtém as informações de um tipo de componente a partir de seu nome
     * @param componentName         Nome do componenete
     * @return                      Classe com informações do tipo de componenete (usado para instanciar um componenete)
     */
    public static ComponentInfo getComponentInfoByName(String componentName) {
        if (!componentInfoPerComponentName.containsKey(componentName)) throw new IllegalArgumentException("Unknown component " + componentName);
        return componentInfoPerComponentName.get(componentName);
    }

    public static final ComponentInfo AND_GATE;
    public static final ComponentInfo NAND_GATE;
    public static final ComponentInfo OR_GATE;
    public static final ComponentInfo NOR_GATE;
    public static final ComponentInfo XOR_GATE;
    public static final ComponentInfo XNOR_GATE;
    public static final ComponentInfo NOT_GATE;
    public static final ComponentInfo INPUT;
    public static final ComponentInfo OUTPUT;
    public static final ComponentInfo MULTIPLEXER_GATE;
    public static final ComponentInfo DISTRIBUTOR_GATE;
    public static final ComponentInfo EXTENSOR_GATE;


    static {
        AND_GATE = registerComponent("AndGate", WE2NDirInfo, QuartusLogics.AND_GATE);
        NAND_GATE = registerComponent("NandGate", WE2NDirInfo, QuartusLogics.NAND_GATE);
        OR_GATE = registerComponent("OrGate", WE2NDirInfo, QuartusLogics.OR_GATE);
        NOR_GATE = registerComponent("NorGate", WE2NDirInfo, QuartusLogics.NOR_GATE);
        XOR_GATE = registerComponent("XorGate", WE2NDirInfo, QuartusLogics.XOR_GATE);
        XNOR_GATE = registerComponent("XnorGate", WE2NDirInfo, QuartusLogics.XNOR_GATE);
        NOT_GATE = registerComponent("NotGate", WE2NDirInfo, QuartusLogics.NOT_GATE);

        MULTIPLEXER_GATE = registerComponent("MultiplexerGate", WES2NDirInfo,  QuartusLogics.MULTIPLEXER);
        DISTRIBUTOR_GATE = registerComponent("DistributorGate", S2NEWDirInfo, QuartusLogics.DISTRIBUTOR);

        //TODO: dar implementação real ao extensor
        EXTENSOR_GATE = registerComponent("ExtensorGate", WES2NDirInfo, QuartusLogics.EXTENSOR);

        INPUT = registerSpecialComponent(CircuitInput.COMP_NAME, CircuitInput.inputDirectionInfo, QuartusLogics.INPUT, CircuitInput::new);
        OUTPUT = registerSpecialComponent(CircuitOutput.COMP_NAME, CircuitOutput.outputDirectionInfo, QuartusLogics.OUTPUT, CircuitOutput::new);
    }

}
