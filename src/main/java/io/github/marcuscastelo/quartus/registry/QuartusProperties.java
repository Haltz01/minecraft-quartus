package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.block.circuit_parts.WireBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;

/**
 * Classe que armazena propriedades usadas em blocos do mod
 */
public class QuartusProperties {
    /**
     * Enum que determina os valores possíveis para a propriedade {@link QuartusProperties#WIRE_UP}.
     * NONE significa que não existem conexões que sobem no fio
     * FACING significa que existe apenas uma conexão que sobe e a direção desta é a direção que o fio está olhando
     * BOTH significa que as duas conexões são conexões que sobem
     * O jogo exige que esse enum seja conversível para string, portanto tal funcionalidade foi implementada por meio da interface StringIdentifiable
     */
    public enum UpValue implements StringIdentifiable {
        NONE("none"), FACING("facing"), BOTH("both");

        String identifier;

        UpValue(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String asString() {
            return identifier;
        }
    }

    // Indica quais lados do WireBlock estão conectados com blocos um nível acima do deles
    public static final EnumProperty<UpValue> WIRE_UP;
    // Indica se o WireBlock faz curva (não estã reto)
    public static final BooleanProperty WIRE_TURN;
    // Indica se o ângulo entre a direção principal e a auxiliar, medida no sentido anti-horário, é positiva ou não (vale pensar nos eixos X e Y do sistema cartesiano em uma base orientada positivamente)
    public static final BooleanProperty WIRE_POSITIVE;


    static {
        WIRE_TURN = BooleanProperty.of("turn");
        WIRE_POSITIVE = BooleanProperty.of("positive");
        WIRE_UP = EnumProperty.of("up", UpValue.class);
    }
}
