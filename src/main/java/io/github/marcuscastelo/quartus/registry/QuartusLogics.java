package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.circuit.QuartusBusInfo;
import io.github.marcuscastelo.quartus.circuit.QuartusLogic;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.util.math.Direction;
import net.minecraft.block.Blocks;
import sun.jvm.hotspot.opto.Block;

import java.util.HashMap;
import java.util.Map;

public class QuartusLogics {
    private static Map<String, QuartusLogic> logicPerName = new HashMap<>();

    public static QuartusLogic register(String logicID, QuartusLogic logic) {
        logicPerName.putIfAbsent(logicID, logic);
        return logic;
    }

    @Nullable
    public static QuartusLogic getLogicByID(String logicID) {
        return logicPerName.getOrDefault(logicID, null);
    }

    public static final QuartusLogic AND_GATE;
    public static final QuartusLogic NAND_GATE;
    public static final QuartusLogic OR_GATE;
    public static final QuartusLogic NOR_GATE;
    public static final QuartusLogic XOR_GATE;
    public static final QuartusLogic XNOR_GATE;
    public static final QuartusLogic NOT_GATE;

    static {
        AND_GATE = register("AndGate", (inputs, outputs) -> outputs.get(Direction.NORTH).setValue(inputs.values().stream().allMatch(busInfo -> busInfo.equals(QuartusBusInfo.HIGH1b))));
        NAND_GATE = register("NandGate", (inputs, outputs) -> outputs.get(Direction.NORTH).setValue(inputs.values().stream().anyMatch(busInfo -> busInfo.equals(QuartusBusInfo.LOW1b))));
        OR_GATE = register("OrGate", (inputs, outputs) -> outputs.get(Direction.NORTH).setValue(inputs.values().stream().anyMatch(busInfo -> busInfo.equals(QuartusBusInfo.HIGH1b))));
        NOR_GATE = register("NorGate", (inputs, outputs) -> outputs.get(Direction.NORTH).setValue(inputs.values().stream().allMatch(busInfo -> busInfo.equals(QuartusBusInfo.LOW1b))));
        XOR_GATE = register("XorGate", (inputs, outputs) -> outputs.get(Direction.NORTH).setValue(inputs.values().stream().filter(busInfo -> busInfo.equals(QuartusBusInfo.HIGH1b)).count() % 2 == 1));
        XNOR_GATE = register("XnorGate", (inputs, outputs) -> outputs.get(Direction.NORTH).setValue(inputs.values().stream().filter(busInfo -> busInfo.equals(QuartusBusInfo.HIGH1b)).count() % 2 == 0));
        NOT_GATE = register("NotGate", (inputs, outputs) -> outputs.get(Direction.NORTH).setValue(inputs.get(Direction.SOUTH).equals(QuartusBusInfo.LOW1b)));
    }
}