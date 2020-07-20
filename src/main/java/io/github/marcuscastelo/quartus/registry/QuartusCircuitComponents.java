package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitInput;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitOutput;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.util.math.Direction;

public class QuartusCircuitComponents {
    public static class QuartusComponentInfo {
        public final Supplier<QuartusCircuitComponent> supplier;
        public final QuartusCircuitComponent.QuartusCircuitComponentDirectionInfo directionInfo;

        public QuartusComponentInfo(Supplier<QuartusCircuitComponent> supplier, QuartusCircuitComponent.QuartusCircuitComponentDirectionInfo directionInfo) {
            this.supplier = supplier;
            this.directionInfo = directionInfo;
        }
    }

    public static final QuartusCircuitComponent.QuartusCircuitComponentDirectionInfo WE2NDirInfo = new QuartusCircuitComponent.QuartusCircuitComponentDirectionInfo(Arrays.asList(Direction.EAST, Direction.WEST), Direction.NORTH);
    public static final QuartusCircuitComponent.QuartusCircuitComponentDirectionInfo WES2NDirInfo = new QuartusCircuitComponent.QuartusCircuitComponentDirectionInfo(Arrays.asList(Direction.EAST, Direction.WEST, Direction.SOUTH), Direction.NORTH);
    public static final QuartusCircuitComponent.QuartusCircuitComponentDirectionInfo S2NEWDirInfo = new QuartusCircuitComponent.QuartusCircuitComponentDirectionInfo(Direction.SOUTH, Arrays.asList(Direction.NORTH, Direction.EAST, Direction.WEST));

    public static final Map<String, QuartusComponentInfo> supplierMap = new HashMap<>();

    public static QuartusComponentInfo register(String componentName, Supplier<QuartusCircuitComponent> componentSupplier, QuartusCircuitComponent.QuartusCircuitComponentDirectionInfo directionInfo) {
        QuartusComponentInfo info = new QuartusComponentInfo(componentSupplier, directionInfo);
        supplierMap.putIfAbsent(componentName, info);
        return info;
    }

    @Nullable
    public static QuartusComponentInfo getComponentInfoByName(String componentName) {
        return supplierMap.getOrDefault(componentName, null);
    }

    public static final QuartusComponentInfo AND_GATE;
    public static final QuartusComponentInfo NAND_GATE;
    public static final QuartusComponentInfo OR_GATE;
    public static final QuartusComponentInfo NOR_GATE;
    public static final QuartusComponentInfo XOR_GATE;
    public static final QuartusComponentInfo XNOR_GATE;
    public static final QuartusComponentInfo NOT_GATE;
    public static final QuartusComponentInfo INPUT;
    public static final QuartusComponentInfo OUTPUT;
    public static final QuartusComponentInfo MULTIPLEXER;
    public static final QuartusComponentInfo DISTRIBUTOR;
    public static final QuartusComponentInfo EXTENSOR;


    static {
        AND_GATE = register("AndGate", () -> new QuartusCircuitComponent("AndGate", WE2NDirInfo, QuartusLogics.AND_GATE), WE2NDirInfo);
        NAND_GATE = register("NandGate", () -> new QuartusCircuitComponent("NandGate", WE2NDirInfo, QuartusLogics.NAND_GATE), WE2NDirInfo);
        OR_GATE = register("OrGate", () -> new QuartusCircuitComponent("OrGate", WE2NDirInfo, QuartusLogics.OR_GATE), WE2NDirInfo);
        NOR_GATE = register("NorGate", () -> new QuartusCircuitComponent("NorGate", WE2NDirInfo, QuartusLogics.NOR_GATE), WE2NDirInfo);
        XOR_GATE = register("XorGate", () -> new QuartusCircuitComponent("XorGate", WE2NDirInfo, QuartusLogics.XOR_GATE), WE2NDirInfo);
        XNOR_GATE = register("XnorGate", () -> new QuartusCircuitComponent("XnorGate", WE2NDirInfo, QuartusLogics.XNOR_GATE), WE2NDirInfo);
        NOT_GATE = register("NotGate", () -> new QuartusCircuitComponent("NotGate", WE2NDirInfo, QuartusLogics.NOT_GATE), WE2NDirInfo);

        //Os inputs e outputs são meros marcadores e por isso não possuem lógica interna (apenas repassam do sul para o norte relativos)
        INPUT = register(QuartusCircuitInput.TYPE, QuartusCircuitInput::new, QuartusCircuitInput.inputDirectionInfo);
        OUTPUT = register(QuartusCircuitOutput.TYPE, QuartusCircuitOutput::new, QuartusCircuitOutput.outputDirectionInfo);

        //TODO: dar implementação real ao multiplexer, dist e ext
        MULTIPLEXER = register("MultiplexerGate", () -> new QuartusCircuitComponent("MultiplexerGate", WES2NDirInfo, QuartusLogics.MULTIPLEXER), WES2NDirInfo);
        EXTENSOR = register("ExtensorGate", () -> new QuartusCircuitComponent("ExtensorGate", WES2NDirInfo, QuartusLogics.EXTENSOR), WES2NDirInfo);
        DISTRIBUTOR = register("DistributorGate", () -> new QuartusCircuitComponent("DistributorGate", S2NEWDirInfo, QuartusLogics.DISTRIBUTOR), S2NEWDirInfo);
    }

}
