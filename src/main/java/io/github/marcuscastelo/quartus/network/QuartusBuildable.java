package io.github.marcuscastelo.quartus.network;

public abstract class QuartusBuildable<T> {
    public static abstract class Builder<T> {
        public abstract T build();
    }
}
