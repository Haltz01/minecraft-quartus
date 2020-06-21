package io.github.marcuscastelo.quartus.network;

import net.minecraft.util.PacketByteBuf;

public interface QuartusSerializable<ObjectType, SerialType> {
    SerialType serialize();
    void unserialize(SerialType serial);
}
