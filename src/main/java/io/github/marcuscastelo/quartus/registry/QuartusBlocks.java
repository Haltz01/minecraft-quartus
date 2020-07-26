package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.CompilerBlock;
import io.github.marcuscastelo.quartus.block.ExecutorBlock;
import io.github.marcuscastelo.quartus.block.ExecutorIOBlock;
import io.github.marcuscastelo.quartus.block.circuit_parts.CircuitComponentBlock;
import io.github.marcuscastelo.quartus.block.circuit_parts.WireBlock;
import io.github.marcuscastelo.quartus.circuit.components.info.ComponentInfo;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;

/**
 * Classe que faz o registro do blocos criados para o Mod
 * Para entender o que cada bloco faz, leia a documentação individual
 */
public class QuartusBlocks {
    public static final WireBlock WIRE;
    public static final CircuitComponentBlock EXTENSOR_GATE;
    public static final CircuitComponentBlock DISTRIBUTOR_GATE;

    public static final CircuitComponentBlock AND_GATE;
    public static final CircuitComponentBlock NAND_GATE;
    public static final CircuitComponentBlock OR_GATE;
    public static final CircuitComponentBlock NOR_GATE;
    public static final CircuitComponentBlock XOR_GATE;
    public static final CircuitComponentBlock XNOR_GATE;
    public static final CircuitComponentBlock NOT_GATE;
    public static final CircuitComponentBlock MULTIPLEXER_GATE;

    public static final CompilerBlock COMPILER;
    public static final ExecutorBlock EXECUTOR;
    public static final ExecutorIOBlock EXECUTOR_IO;

    public static final CircuitComponentBlock INPUT;
    public static final CircuitComponentBlock OUTPUT;

    /**
     *  Método usado como artifício de inicialização dos campos estáticos da classe.
     *  O Java não inicializaria os campos estáticos da classe no momento certo se este método não fosse
     *  chamado na função onInitialize
     */
    public static void init() {}

    /**
     * Método responsável por registrar um bloco no jogo
     * @param block_name    Nome do bloco que será registrado
     * @param block         Bloco a ser registrado (pode-se entender como sendo o "tipo" do bloco que existe no mundo)
     * @param <T>           Define qual filho da classe Block está sendo registrado
     * @return              Bloco que foi registrado (como objeto de {@link T})
     */
    private static <T extends Block> T registerBlock(String block_name, T block) {
        return Registry.register(Registry.BLOCK, Quartus.id(block_name), block);
    }

    /**
     * Método responsável por registrar um bloco de componente (CircuitComponentBlock)
     * Busca pelo nome do componente no registro de componentes e cria um {@link CircuitComponentBlock} com as informações do componente
     * @see QuartusCircuitComponents
     * @param block_name    Nome do bloco de componente a ser registrado
     * @param componentName Nome do componente associado ao bloco
     * @return              Bloco registrado
     */
    private static CircuitComponentBlock registerComponentBlock(String block_name, String componentName) {
        ComponentInfo componentInfo = QuartusCircuitComponents.getComponentInfoByName(componentName);
        if (componentInfo == null) throw new IllegalArgumentException("Unknown componentName = " + componentName);
        return registerBlock(block_name, new CircuitComponentBlock(componentInfo));
    }

    //Registra todos os blocos do mod
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

        EXECUTOR_IO = registerBlock("executor_io", new ExecutorIOBlock());

        INPUT = registerComponentBlock("input", "Input");
        OUTPUT = registerComponentBlock("output", "Output");
    }
}
