package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.CircuitDescriptor;
import io.github.marcuscastelo.quartus.circuit.components.info.ComponentDirectionInfo;
import io.github.marcuscastelo.quartus.registry.QuartusLogics;
import net.minecraft.util.math.Direction;

/**
 * Classe que define o Input utilizado pelo Mod
 */
public class InputDescriptor extends ComponentDescriptor {

    //Variáveis que definem o tipo e a direção do Input
    public static final String COMP_NAME = "Input";
    public static final ComponentDirectionInfo inputDirectionInfo = new ComponentDirectionInfo(Direction.SOUTH, Direction.NORTH);

    /**
     * Construtor da Classe Output
     * @param ID		Identificador do componente
     */
    public InputDescriptor(int ID) {
        super(COMP_NAME, inputDirectionInfo, ID, QuartusLogics.OUTPUT);
    }

    /**
     * Construtor da Classe Input, sem uso do identificador
     */
    public InputDescriptor(CircuitDescriptor circuit) {
        super(COMP_NAME, inputDirectionInfo, circuit.generateNextID(), QuartusLogics.INPUT);
    }

    public static class Builder extends ComponentDescriptor.Builder {
        @Override
        public ComponentDescriptor build() {
            return new InputDescriptor(ID);
        }
    }
}
