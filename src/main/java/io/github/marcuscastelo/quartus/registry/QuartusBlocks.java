package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.CompilerBlock;
import io.github.marcuscastelo.quartus.block.ExecutorBlock;
import io.github.marcuscastelo.quartus.block.ExecutorIOBlock;
import io.github.marcuscastelo.quartus.block.circuit_components.*;
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

    static {
        WIRE = register("wire", new WireBlock());
        EXTENSOR_GATE = register("extensor_gate", new ExtensorGateBlock());
        DISTRIBUTOR_GATE = register("distributor_gate", new DistributorGateBlock());

        AND_GATE = register("and_gate", new AndLogicGateBlock());
        NAND_GATE = register("nand_gate", new NandLogicGateBlock());
        OR_GATE = register("or_gate", new OrLogicGateBlock());
        NOR_GATE = register("nor_gate", new NorLogicGateBlock());
        XOR_GATE = register("xor_gate", new XorLogicGateBlock());
        XNOR_GATE = register("xnor_gate", new XnorLogicGateBlock());
        NOT_GATE = register("not_gate", new NotLogicGateBlock());

        MULTIPLEXER_GATE = register("multiplexer", new MultiplexerLogicGateBlock());

        COMPILER = register("compiler", new CompilerBlock());
        EXECUTOR = register("executor", new ExecutorBlock());

        EXTENSOR_IO = register("extensor_io", new ExecutorIOBlock());

        INPUT = register("input", new InputGateBlock());
        OUTPUT = register("output", new OutputGateBlock());
    }
}
