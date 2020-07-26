package io.github.marcuscastelo.quartus.network;

import org.apache.commons.lang3.SerializationException;

import java.io.Serializable;

/**
 * Interface que possui as assinaturas dos métodos
 * de serializar de desserializar (serialize e unserialize)
 * de um dado circuito
 */
public interface QuartusSimetricSerializer<ObjectType, SerialType extends Serializable> extends QuartusSerializer<ObjectType, ObjectType, SerialType> {
}
