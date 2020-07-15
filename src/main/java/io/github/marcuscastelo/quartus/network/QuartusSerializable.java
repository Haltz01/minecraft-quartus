package io.github.marcuscastelo.quartus.network;

public interface QuartusSerializable<ObjectType, SerialType> {
    SerialType serialize();
    void unserialize(SerialType serial);
}
