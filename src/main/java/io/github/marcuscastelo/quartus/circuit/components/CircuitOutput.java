package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.registry.QuartusLogics;
import net.minecraft.util.math.Direction;

/**
 * Classe que define o Output utilizado pelo Mod
 */
public class CircuitOutput extends CircuitComponent {
    //Variáveis que definem o tipo e a direção do Input
    public static final String COMP_NAME = "Output";
    public static final ComponentDirectionInfo outputDirectionInfo = new ComponentDirectionInfo(Direction.SOUTH, Direction.NORTH);

    /**
     * Construtor da Classe Output
     * @param ID		Identificador do componente
     */
    public CircuitOutput(int ID) {
        super(COMP_NAME, outputDirectionInfo, ID, QuartusLogics.OUTPUT);
    }

    /**
     * Construtor da Classe Output, sem uso do identificador
     */
    public CircuitOutput() {
        super(COMP_NAME, outputDirectionInfo, QuartusLogics.OUTPUT);
    }

}
