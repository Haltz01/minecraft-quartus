package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.CompilerBlock;
import io.github.marcuscastelo.quartus.block.RENDER_TEST_BLOCK;
import io.github.marcuscastelo.quartus.block.circuit_components.*;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;

public class QuartusBlocks {
    public static final Block WIRE;
    public static final Block EXTENSOR_GATE;
    public static final Block DISTRIBUTOR_GATE;

    public static final Block AND_GATE;
    public static final Block OR_GATE;
    public static final Block XOR_GATE;
    public static final Block NOT_GATE;
    public static final Block NAND_GATE;
    public static final Block NOR_GATE;
    public static final Block COMPILER;

    public static final Block INPUT;

    public static void init() {}

    private static Block register(String block_name, Block block) {
        return Registry.register(Registry.BLOCK, Quartus.id(block_name), block);
    }

    static {
        WIRE = register("wire", new WireBlock());
        EXTENSOR_GATE = register("extensor_gate", new ExtensorGateBlock());
        DISTRIBUTOR_GATE = register("distributor_gate", new DistributorGateBlock());

        AND_GATE = register("and_gate", new AndGateBlock());
        OR_GATE = register("or_gate", new OrGateBlock());
        XOR_GATE = NOR_GATE = NOT_GATE = NAND_GATE = null;

        COMPILER = register("compiler", new CompilerBlock());

        INPUT = register("input", new InputBlock());



        register("render", new RENDER_TEST_BLOCK());
    }
}
