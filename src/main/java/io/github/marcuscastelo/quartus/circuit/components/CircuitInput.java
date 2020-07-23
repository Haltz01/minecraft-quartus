package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.registry.QuartusLogics;
import net.minecraft.util.math.Direction;

/**
 * Classe que define o Input utilizado pelo Mod
 */
public class CircuitInput extends CircuitComponent {

    //Variáveis que definem o tipo e a direção do Input
    public static final String COMP_NAME = "Input";
    public static final ComponentDirectionInfo inputDirectionInfo = new ComponentDirectionInfo(Direction.SOUTH, Direction.NORTH);

    /**
     * Construtor da Classe Input
     * @param ID	->	Identificador do componente
     */
    public CircuitInput(int ID) {
        super(COMP_NAME, inputDirectionInfo, ID, QuartusLogics.INPUT);
    }

    /**
     * Construtor da Classe Input, sem uso do identificador
     */
    public CircuitInput() {
        super(COMP_NAME, inputDirectionInfo, QuartusLogics.INPUT);
    }

    /**
     * Método que faz a chamada para dar update no Input no circuito
     */
    public void updateComponent(QuartusCircuit circuit) {
        super.updateComponent(circuit);
    }
}
