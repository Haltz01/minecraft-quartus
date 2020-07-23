package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.circuit.QuartusLogic;
import io.github.marcuscastelo.quartus.circuit.components.*;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class QuartusCircuitComponents {
    public static final ComponentDirectionInfo WE2NDirInfo = new ComponentDirectionInfo(Arrays.asList(Direction.EAST, Direction.WEST), Direction.NORTH);
    public static final ComponentDirectionInfo WES2NDirInfo = new ComponentDirectionInfo(Arrays.asList(Direction.EAST, Direction.WEST, Direction.SOUTH), Direction.NORTH);
    public static final ComponentDirectionInfo S2NEWDirInfo = new ComponentDirectionInfo(Direction.SOUTH, Arrays.asList(Direction.NORTH, Direction.EAST, Direction.WEST));

    public static final Map<String, ComponentInfo> componentInfoPerComponentName = new HashMap<>();

    public static ComponentInfo registerComponent(String componentName, ComponentDirectionInfo directionInfo, QuartusLogic componentLogic) {
        Supplier<CircuitComponent> componentSupplier = () -> new CircuitComponent(componentName, directionInfo, componentLogic);
        return registerSpecialComponent(componentName, directionInfo, componentLogic, componentSupplier);
    }

    public static ComponentInfo registerSpecialComponent(String componentName, ComponentDirectionInfo directionInfo, QuartusLogic componentLogic, Supplier<CircuitComponent> componentSupplier) {
        ComponentInfo info = new ComponentInfo(componentSupplier, directionInfo, componentLogic);
        componentInfoPerComponentName.putIfAbsent(componentName, info);
        return info;
    }

    @Nullable
    public static ComponentInfo getComponentInfoByName(String componentName) {
        return componentInfoPerComponentName.getOrDefault(componentName, null);
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

        //Os inputs e outputs são meros marcadores e por isso não possuem lógica interna (apenas repassam do sul para o norte relativos)
        INPUT = registerSpecialComponent(CircuitInput.COMP_NAME, CircuitInput.inputDirectionInfo, QuartusLogics.INPUT, CircuitInput::new);
        OUTPUT = registerSpecialComponent(CircuitOutput.COMP_NAME, CircuitOutput.outputDirectionInfo, QuartusLogics.OUTPUT, CircuitOutput::new);
    }

}
