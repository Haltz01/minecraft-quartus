package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.CircuitDescriptor;
import io.github.marcuscastelo.quartus.circuit.components.info.ComponentDirectionInfo;
import io.github.marcuscastelo.quartus.registry.QuartusLogics;
import net.minecraft.util.math.Direction;

/**
 * Classe que define o Output utilizado pelo Mod
 */
public class OutputDescriptor extends ComponentDescriptor {
    //Variáveis que definem o tipo e a direção do Input
    public static final String COMP_NAME = "Output";
    public static final ComponentDirectionInfo outputDirectionInfo = new ComponentDirectionInfo(Direction.SOUTH, Direction.NORTH);

    /**
     * Construtor da Classe Output
     * @param ID		Identificador do componente
     */
    public OutputDescriptor(int ID) {
        super(COMP_NAME, outputDirectionInfo, ID, QuartusLogics.OUTPUT);
    }

    /**
     * Construtor da Classe Input, sem uso do identificador
     */
    public OutputDescriptor(CircuitDescriptor circuit) {
        super(COMP_NAME, outputDirectionInfo, circuit.generateNextID(), QuartusLogics.INPUT);
    }

    public static class Builder extends ComponentDescriptor.Builder {
        @Override
        public ComponentDescriptor build() {
            return new OutputDescriptor(ID);
        }
    }
}
