package io.github.marcuscastelo.quartus.circuit;

public class ComponentConnection<T> {
    public enum ConnectionType {
        INPUT, OUTPUT, SELECTOR
    }

    private final ConnectionType type;
    private final T A, B;
    public ComponentConnection(ConnectionType type, T A, T B) {
        this.A = A;
        this.B = B;
        this.type = type;
    }

    public T getA() { return A; }
    public T getB() { return B; }

    public ConnectionType getType() {
        return type;
    }
}
