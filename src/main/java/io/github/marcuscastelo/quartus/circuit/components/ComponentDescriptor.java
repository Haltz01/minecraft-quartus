package io.github.marcuscastelo.quartus.circuit.components;

import com.google.common.collect.ImmutableList;
import io.github.marcuscastelo.quartus.circuit.*;
import io.github.marcuscastelo.quartus.circuit.components.info.ComponentDirectionInfo;
import io.github.marcuscastelo.quartus.circuit.components.info.ComponentExecutionInfo;
import io.github.marcuscastelo.quartus.circuit.components.info.ComponentInfo;
import io.github.marcuscastelo.quartus.registry.QuartusCircuitComponents;
import io.github.marcuscastelo.quartus.registry.QuartusLogics;
import io.github.marcuscastelo.quartus.util.DirectionUtils;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

import java.util.*;

/**
 * Classe que define genericamente um componente do circuito, utilizado no Mod
 */


public class ComponentDescriptor {
    private final ComponentDirectionInfo componentDirectionInfo;
    private final Map<Direction, List<ComponentConnection>> connections;

    //TODO: ver como fazer pros extensores

    private final QuartusLogic logic;
    private int ID;
    private final String componentName;

    /**
	 * Contrutor padrão da classe QuartusCircuitComponent
	 * @param componentName		        Nome do componente
	 * @param componentDirectionInfo	Possíveis direções de input e output do componente
	 * @param ID		                Identificador do componente
	 */
    protected ComponentDescriptor(String componentName, ComponentDirectionInfo componentDirectionInfo, int ID, QuartusLogic logic) {
        this.componentName = componentName;
        this.connections = new HashMap<>();
        this.logic = logic;

        this.componentDirectionInfo = componentDirectionInfo;

        this.ID = ID;

        for (Direction dir: DirectionUtils.HORIZONTAL_DIRECTIONS) {
            connections.put(dir, new ArrayList<>());
        }
    }

	//Método que retorna o ID de um componente
    public int getID() { return ID; }

    @Deprecated
    public void overrideID(int ID) { this.ID = ID;}

    public QuartusLogic getLogic() {
        return logic;
    }

    /**
	 * Método que verifica se um dado componente possui conexões nas suas saídas
	 * @return		Boolean que verifica se há conexões nas saídas
	 */
    public boolean hasOutputConnections() {
        for (List<ComponentConnection> connectionsPerDir: connections.values()) {
            for (ComponentConnection connection: connectionsPerDir) {
                if (connection.getType().equals(ComponentConnection.ConnectionType.OUTPUT)) return true;
            }
        }
        return false;
    }

	/**
	 * Método que adiciona uma conexão a um componente de acordo com a direção dada
	 * @param direction		Direção dada para adicionar conexão
	 * @param connection		Conexão a ser adicionada
	 */
    public void addConnection(Direction direction, ComponentConnection connection) {
        connections.get(direction).add(connection);
    }

	/**
	 * Método que retorna como String as conexões nos outputs de um componente
	 * @return		String com as conexões das saídas do componente
	 */
    public String getOutputConnectionsString() {
        StringBuilder str = new StringBuilder();

        for (Map.Entry<Direction, List<ComponentConnection>> connectionsEntries : connections.entrySet()) {
            Direction AtoBDirection = connectionsEntries.getKey();
            List<ComponentConnection> connectionsForThatDirection = connectionsEntries.getValue();
            for (ComponentConnection connection: connectionsForThatDirection) {
                if (connection.getType() == ComponentConnection.ConnectionType.OUTPUT) //Exibe apenas as conexões de saída
                    str.append(String.format("%s / %s -> %s / %s\n", this.toString(), AtoBDirection.getName(), connection.BtoADirection, connection.connectToCompStr));
            }
        }

        return str.toString();
    }

	/**
	 * Método que retorna uma lista com as conexões das saídas de um componente
	 * @return		Lista com as conexões
	 */
    public List<ComponentConnection> getOutputConnections() {
        List<ComponentConnection> outputConnections = new ArrayList<>();
        for (List<ComponentConnection> connectionsPerDir: connections.values())
            for (ComponentConnection connection: connectionsPerDir)
                if (connection.getType() == ComponentConnection.ConnectionType.OUTPUT)
                    outputConnections.add(connection);
        return outputConnections;
    }

    public ComponentDirectionInfo getComponentDirectionInfo() {
        return componentDirectionInfo;
    }

    /**
	 * Método que retorna uma lista com as conexões nos inputs de um componente
	 * @return lista de conexões do tipo input
	 */
    public List<ComponentConnection> getInputConnections() {
        List<ComponentConnection> inputConnections = new ArrayList<>();
        for (List<ComponentConnection> connectionsPerDir: connections.values())
            for (ComponentConnection connection: connectionsPerDir)
                if (connection.getType() != ComponentConnection.ConnectionType.OUTPUT)
                    inputConnections.add(connection);
        return inputConnections;
    }

	/**
	 * Método que retorna um mapeamento das conexões dos componentes
	 * e suas direções
	 * @return		Mapeamento das conexões e direções
	 */
    public Map<Direction, List<ComponentConnection>> getConnections() {
        return connections;
    }

	//Métodos que retornam uma lista com as possíveis direções de inputs e outputs, respectivamente
    public final ImmutableList<Direction> getPossibleInputDirections() { return componentDirectionInfo.possibleInputDirections; }
    public final ImmutableList<Direction> getPossibleOutputDirections() { return componentDirectionInfo.possibleOutputDirections; }

	/**
	 * Método que retorna a toString de um componente, adicionando seu ID
	 * no final da String, facilitando identificação dos componentes
	 * durante compilição e execução
	 */
    @Override
    public String toString() {
        return componentName+"_"+getID();
    }


    public static class Builder {
        protected String componentName = "UnnamedComponent";
        protected int ID = 0xF0F0F0F0;
        protected ComponentDirectionInfo directionInfo = new ComponentDirectionInfo(Collections.emptyList(), Collections.emptyList());
        protected QuartusLogic logic = QuartusLogic.EMPTY_LOGIC;
        public Builder() {}

        public Builder setName(String componentName) {
            this.componentName = componentName;
            return this;
        }

        public Builder setID(int ID) {
            this.ID = ID;
            return this;
        }

        public Builder setID(CircuitDescriptor circuit) {
            this.ID = circuit.generateNextID();
            return this;
        }

        public Builder setDirections(ComponentDirectionInfo directionInfo) {
            this.directionInfo = directionInfo;
            return this;
        }

        public Builder setLogic(QuartusLogic logic) {
            this.logic = logic;
            return this;
        }
        
        public ComponentDescriptor build() {
            return new ComponentDescriptor(componentName, directionInfo, ID, logic);
        }
    }

    /**
     * Método que retorna um objeto de classe genérica pertencente aos componentes do circuito
     * Podem ser -	Input
     * 			 -	Output
     * 			 -	Porta Lógica
     * @param gateType		String com o tipo de porta
     * @param gateID		Int com o ID da porta
     * @return		Objeto genérico de acordo com os parâmetros passados
     */
    public static ComponentDescriptor createPolimorphicComponent(String gateType, int gateID) {
        ComponentInfo info = QuartusCircuitComponents.getComponentInfoByName(gateType);
        if (gateType.equals(InputDescriptor.COMP_NAME))
            return new InputDescriptor(gateID);
        else if (gateType.equals(OutputDescriptor.COMP_NAME))
            return new OutputDescriptor(gateID);
        else
            return new ComponentDescriptor(gateType, info.directionInfo, gateID, QuartusLogics.getLogicByName(gateType));
    }

    /**
     * Método auxiliar que retorna um par,
     * relacionando a String que identifica um componente
     * com seu ID identificador
     * @param componentStr		String que identifica um componente
     * @return		Par de String e Int, que identificam um tipo de componente
     */
    public static Pair<String, Integer> getComponentStrInfo(String componentStr) {
        String[] params = componentStr.split("_");
        String gateType = params[0];
        int gateID = Integer.parseInt(params[1]);
        return new Pair<>(gateType, gateID);
    }
}
