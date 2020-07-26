package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.circuit.components.info.ComponentDirectionInfo;
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
     * Construtor da Classe Output
     * @param ID		Identificador do componente
     */
    public CircuitInput(int ID) {
        super(COMP_NAME, inputDirectionInfo, ID, QuartusLogics.OUTPUT);
    }

    /**
     * Construtor da Classe Input, sem uso do identificador
     */
    public CircuitInput(QuartusCircuit circuit) {
        super(COMP_NAME, inputDirectionInfo, circuit.generateID(), QuartusLogics.INPUT);
    }

    public static class Builder extends CircuitComponent.Builder {
        @Override
        public CircuitComponent build() {
            return new CircuitInput(ID);
        }
    }
}
