package io.github.marcuscastelo.quartus.network;

/**
 * Interface que possui as assinaturas dos métodos
 * de serializar de desserializar (serialize e unserialize)
 * de um dado circuito
 */
public interface QuartusSerializable<ObjectType, SerialType> {
    SerialType serialize();
    void unserialize(SerialType serial);
}
