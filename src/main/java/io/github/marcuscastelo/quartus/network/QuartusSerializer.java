package io.github.marcuscastelo.quartus.network;

import org.apache.commons.lang3.SerializationException;

import java.io.Serializable;

public interface QuartusSerializer<O, R, S extends Serializable> {
    S serialize(O obj) throws SerializationException;
    R unserialize(S serial) throws SerializationException;
}
