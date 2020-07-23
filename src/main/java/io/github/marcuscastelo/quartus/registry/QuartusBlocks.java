package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.CompilerBlock;
import io.github.marcuscastelo.quartus.block.ExecutorBlock;
import io.github.marcuscastelo.quartus.block.ExecutorIOBlock;
import io.github.marcuscastelo.quartus.block.circuit_parts.CircuitComponentBlock;
import io.github.marcuscastelo.quartus.block.circuit_parts.WireBlock;
import io.github.marcuscastelo.quartus.circuit.components.ComponentInfo;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;

public class QuartusBlocks {
    public static final Block WIRE;
    public static final Block EXTENSOR_GATE;
    public static final Block DISTRIBUTOR_GATE;

    public static final Block AND_GATE;
    public static final Block NAND_GATE;
    public static final Block OR_GATE;
    public static final Block NOR_GATE;
    public static final Block XOR_GATE;
    public static final Block XNOR_GATE;
    public static final Block NOT_GATE;
    public static final Block MULTIPLEXER_GATE;

    public static final Block COMPILER;
    public static final Block EXECUTOR;
    public static final Block EXTENSOR_IO;

    public static final Block INPUT;
    public static final Block OUTPUT;

    public static void init() {}

    private static Block register(String block_name, Block block) {
        return Registry.register(Registry.BLOCK, Quartus.id(block_name), block);
    }

    private static Block registerComponent(String block_name, String componentName) {
        ComponentInfo componentInfo = QuartusCircuitComponents.getComponentInfoByName(componentName);
        if (componentInfo == null) throw new IllegalArgumentException("Unknown componentName = " + componentName);
        return register(block_name, new CircuitComponentBlock(componentInfo));
    }

    static {
        WIRE = register("wire", new WireBlock());
        EXTENSOR_GATE = registerComponent("extensor_gate", "ExtensorGate");
        DISTRIBUTOR_GATE = registerComponent("distributor_gate", "DistributorGate");

        AND_GATE = registerComponent("and_gate", "AndGate");
        NAND_GATE = registerComponent("nand_gate", "NandGate");
        OR_GATE = registerComponent("or_gate", "OrGate");
        NOR_GATE = registerComponent("nor_gate", "NorGate");
        XOR_GATE = registerComponent("xor_gate", "XorGate");
        XNOR_GATE = registerComponent("xnor_gate", "XnorGate");
        NOT_GATE = registerComponent("not_gate", "NotGate");

        MULTIPLEXER_GATE = registerComponent("multiplexer", "MultiplexerGate");

        COMPILER = register("compiler", new CompilerBlock());
        EXECUTOR = register("executor", new ExecutorBlock());

        EXTENSOR_IO = register("extensor_io", new ExecutorIOBlock());

        INPUT = registerComponent("input", "Input");
        OUTPUT = registerComponent("output", "Output");
    }
}
