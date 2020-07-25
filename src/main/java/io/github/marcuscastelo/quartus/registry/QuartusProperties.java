package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.block.circuit_parts.WireBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;

/**
 * Classe que armazena propriedades usadas em blocos do mod
 */
public class QuartusProperties {
    // Indica quais lados do WireBlock estão conectados com blocos um nível acima do deles
    public static final EnumProperty<WireBlock.UpValue> WIRE_UP;
    // Indica se o WireBlock faz curva (não estã reto)
    public static final BooleanProperty WIRE_TURN;
    // Indica se o ângulo entre a direção principal e a auxiliar, medida no sentido anti-horário, é positiva ou não (vale pensar nos eixos X e Y do sistema cartesiano em uma base orientada positivamente)
    public static final BooleanProperty WIRE_POSITIVE;


    static {
        WIRE_TURN = BooleanProperty.of("turn");
        WIRE_POSITIVE = BooleanProperty.of("positive");
        WIRE_UP = EnumProperty.of("up", WireBlock.UpValue.class);
    }
}
