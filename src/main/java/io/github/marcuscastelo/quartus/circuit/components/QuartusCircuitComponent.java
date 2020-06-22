package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.ComponentConnections;
import io.github.marcuscastelo.quartus.circuit.QuartusBusInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class QuartusCircuitComponent {
    //Componentes nas portas de input (não precisam ser inputs de fato)
    List<ComponentConnections<QuartusCircuitComponent>> connections;

    public static int LAST_ID = 1;

    private final int ID;
    private final String componentName;
    public QuartusCircuitComponent(String componentName) {
        this.componentName = componentName;
        this.connections = new ArrayList<>();

        ID = LAST_ID++;
    }

    public int getID() { return ID; }

    public abstract QuartusBusInfo getOutput();

    public String getOutputConnectionsString() {
        StringBuilder str = new StringBuilder();

        for (ComponentConnections<QuartusCircuitComponent> componentConnections : connections) {
            if (componentConnections.getType() == ComponentConnections.ConnectionType.OUTPUT) //Exibe apenas as conexões de saída
                str.append(String.format("%s->%s\n", this.toString(), componentConnections.getB().toString()));
        }

        return str.toString();
    }

    @Override
    public String toString() {
        return componentName+getID();
    }
}
