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

/**
 * Classe que faz o registro do blocos criados para o Mod
 */
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

    /**
     * Método responsável por registrar os blocos no jogo
     * @param block_name    Nome do bloco que será registrado
     * @param block         Bloco a ser registrado (pode-se entender como sendo o "tipo" do bloco que existe no mundo)
     * @return              Bloco que foi registrado
     */
    private static Block registerBlock(String block_name, Block block) {
        return Registry.register(Registry.BLOCK, Quartus.id(block_name), block);
    }

    /**
     * Método responsável por registrar um bloco de componente (CircuitComponentBlock)
     * @param block_name    Nome do bloco de componente a ser registrado
     * @param componentName Nome do componente associado ao bloco
     * @return              Bloco registrado
     */
    private static Block registerComponentBlock(String block_name, String componentName) {
        ComponentInfo componentInfo = QuartusCircuitComponents.getComponentInfoByName(componentName);
        if (componentInfo == null) throw new IllegalArgumentException("Unknown componentName = " + componentName);
        return registerBlock(block_name, new CircuitComponentBlock(componentInfo));
    }

    static {
        WIRE = registerBlock("wire", new WireBlock());
        EXTENSOR_GATE = registerComponentBlock("extensor_gate", "ExtensorGate");
        DISTRIBUTOR_GATE = registerComponentBlock("distributor_gate", "DistributorGate");

        AND_GATE = registerComponentBlock("and_gate", "AndGate");
        NAND_GATE = registerComponentBlock("nand_gate", "NandGate");
        OR_GATE = registerComponentBlock("or_gate", "OrGate");
        NOR_GATE = registerComponentBlock("nor_gate", "NorGate");
        XOR_GATE = registerComponentBlock("xor_gate", "XorGate");
        XNOR_GATE = registerComponentBlock("xnor_gate", "XnorGate");
        NOT_GATE = registerComponentBlock("not_gate", "NotGate");

        MULTIPLEXER_GATE = registerComponentBlock("multiplexer", "MultiplexerGate");

        COMPILER = registerBlock("compiler", new CompilerBlock());
        EXECUTOR = registerBlock("executor", new ExecutorBlock());

        EXTENSOR_IO = registerBlock("extensor_io", new ExecutorIOBlock());

        INPUT = registerComponentBlock("input", "Input");
        OUTPUT = registerComponentBlock("output", "Output");
    }
}
