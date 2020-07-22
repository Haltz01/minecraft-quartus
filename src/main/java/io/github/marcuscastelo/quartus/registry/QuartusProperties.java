package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.block.circuit_parts.WireBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;

public class QuartusProperties {
    public static final EnumProperty<WireBlock.UpValues> WIRE_UP;
    public static final BooleanProperty WIRE_TURN;
    public static final BooleanProperty WIRE_POSITIVE;


    static {
        WIRE_TURN = BooleanProperty.of("turn");
        WIRE_POSITIVE = BooleanProperty.of("positive");
        WIRE_UP = EnumProperty.of("up", WireBlock.UpValues.class);
    }
}
