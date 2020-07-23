package io.github.marcuscastelo.quartus.circuit.components;

import com.google.common.collect.ImmutableList;
import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.circuit.*;
import io.github.marcuscastelo.quartus.registry.QuartusLogics;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import sun.awt.X11.XSystemTrayPeer;

import java.sql.Connection;
import java.util.*;

/**
 * Classe que define genericamente um componente do circuito, utilizado no Mod
 */
public class QuartusCircuitComponent {
	/**
	 * Sub-Classe que define a direção que a informação que passa
	 * por um componente deve seguir, de acordo com seus inputs e outputs
	 */
    public static class QuartusCircuitComponentDirectionInfo {
        public final ImmutableList<Direction> possibleInputDirections, possibleOutputDirections;
        public QuartusCircuitComponentDirectionInfo(List<Direction> possibleInputDirections, List<Direction> possibleOutputDirections) {
            this.possibleInputDirections = ImmutableList.copyOf(possibleInputDirections);
            this.possibleOutputDirections = ImmutableList.copyOf(possibleOutputDirections);
        }

        public QuartusCircuitComponentDirectionInfo(Direction possibleInputDirection, List<Direction> possibleOutputDirections) {
            this(Collections.singletonList(possibleInputDirection), possibleOutputDirections);
        }

        public QuartusCircuitComponentDirectionInfo(List<Direction> possibleInputDirections, Direction possibleOutputDirection) {
            this(possibleInputDirections, Collections.singletonList(possibleOutputDirection));
        }

        public QuartusCircuitComponentDirectionInfo(Direction possibleInputDirection, Direction possibleOutputDirection) {
            this(Collections.singletonList(possibleInputDirection), Collections.singletonList(possibleOutputDirection));
        }
    }

    private final QuartusCircuitComponentDirectionInfo possibleDirectionsInfo;
    private Map<Direction, List<ComponentConnection>> connections;

    //TODO: ver como fazer pros extensores
    //TODO: fazer map de lista
    private Map<Direction, QuartusBusInfo> outputInfo;
    private Map<Direction, QuartusBusInfo> inputInfo;

    private QuartusLogic logic;

    public static int LAST_ID = 1;

    private final int ID;
    private final String componentName;

	/**
	 * Contrutor padrão da classe QuartusCircuitComponent
	 * @param componentName	->	Nome do componente
	 * @param possibleDirectionsInfo	->	Possíveis direções que a informação pode seguir
	 * @param ID	->	Identificador do componente
	 */
    public QuartusCircuitComponent(String componentName, QuartusCircuitComponentDirectionInfo possibleDirectionsInfo, int ID) {
        this.componentName = componentName;
        this.connections = new HashMap<>();
        this.inputInfo = new HashMap<>();
        this.outputInfo = new HashMap<>();
        this.logic = null;
        this.possibleDirectionsInfo = possibleDirectionsInfo;

        this.ID = ID;

        final Direction[] HORIZONTAL_DIRECTIONS = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
        for (Direction dir: HORIZONTAL_DIRECTIONS) {
            connections.put(dir, new ArrayList<>());
        }

        for (Direction dir: possibleDirectionsInfo.possibleInputDirections) {
            inputInfo.put(dir, new QuartusBusInfo(false));
        }

        for (Direction dir: possibleDirectionsInfo.possibleOutputDirections) {
            outputInfo.put(dir, new QuartusBusInfo(false));
        }
    }

	//NÃO SEI O QUE FAZEM!!!
	//PLS, SEND HELP
    public QuartusCircuitComponent(String componentName, QuartusCircuitComponentDirectionInfo possibleDirectionsInfo) {
        this(componentName, possibleDirectionsInfo, LAST_ID++);
    }

    public QuartusCircuitComponent(String componentName, QuartusCircuitComponentDirectionInfo possibleDirectionsInfo, QuartusLogic logic) {
        this(componentName, possibleDirectionsInfo);
        this.logic = logic;
    }

    public QuartusCircuitComponent(String componentName, QuartusCircuitComponentDirectionInfo possibleDirectionsInfo, int ID, QuartusLogic logic) {
        this(componentName, possibleDirectionsInfo, ID);
        this.logic = logic;
    }

	//Método que retorna o ID de um componente
    public int getID() { return ID; }

    //TODO: tornar mais genérica: atualmente foca apenas em trazer a saída do outro (supondo ser única) para a entrada deste (supondo ser única)
    private void updateInputInfo(QuartusCircuit circuit) {
        forDirection:
        for (Map.Entry<Direction, List<ComponentConnection>> entry: connections.entrySet()) {
            Direction AtoBDirection = entry.getKey();

            //FIXME: suporta apenas uma entrada e uma saída
            List<ComponentConnection> possibleConnections = entry.getValue();
            if (entry.getValue().size() == 0) continue; //nenhuma conexão nessa direção
            ComponentConnection arbitrarilyChosenConnection = null;

            //Pega a primeira conexão de input encontrada (ou desiste da direção se nenhuma for encontrada)
            for (int i = 0; i < possibleConnections.size(); i++) {
                arbitrarilyChosenConnection = entry.getValue().get(i);
                if (arbitrarilyChosenConnection.getType() == ComponentConnection.ConnectionType.INPUT) break;
                if (i == possibleConnections.size()-1) continue forDirection;
            }

            int BID = CircuitUtils.getComponentStrInfo(arbitrarilyChosenConnection.connectToCompStr).getRight();
            QuartusCircuitComponent BComponent = circuit.getComponentByID(BID);

            Direction BtoADirection = arbitrarilyChosenConnection.BtoADirection;

            //Copia o output do B para o input do atual (A)
            this.inputInfo.get(AtoBDirection).setValue(BComponent.getOutputInfo().get(BtoADirection));
        }
    }

	/**
	 * Método que faz a chamada do updateInputInfo de um circuito,
	 * atualizando seus valores de entrada e saída
	 * @param circuit	->	Circuito a ser atualizado
	 */
    public void updateComponent(QuartusCircuit circuit) {
        updateInputInfo(circuit);
        if (logic != null) logic.updateLogic(inputInfo, outputInfo);
    }

	/**
	 * Método que retorna as informações que estão nos outputs de um componente
	 * @return	->	Mapeamento dos Bus's com suas respectivas informações
	 */
	public Map<Direction, QuartusBusInfo> getOutputInfo() { return outputInfo; }
	
	/**
	 * Método que retorna as informações que estão nos inputs de um componente
	 * @return	->	Mapeamento dos Bus's com suas respectivas informações
	 */
	public Map<Direction, QuartusBusInfo> getInputInfo() { return inputInfo; }
	
	/**
	 * Método que verifica se um dado componente possui conexões nas suas saídas
	 * @return	->	Boolean que verifica se há conexões nas saídas
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
	 * @param direction	->	Direção dada para adicionar conexão
	 * @param connection	->	Conexão a ser adicionada
	 */
    public void addConnection(Direction direction, ComponentConnection connection) {
        connections.get(direction).add(connection);
    }

	/**
	 * Método que retorna como String as conexões nos outputs de um componente
	 * @return	->	String com as conexões das saídas do componente
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
	 * @return	->	Lista com as conexões
	 */
    public List<ComponentConnection> getOutputConnections() {
        List<ComponentConnection> outputConnections = new ArrayList<>();
        for (List<ComponentConnection> connectionsPerDir: connections.values())
            for (ComponentConnection connection: connectionsPerDir)
                if (connection.getType() == ComponentConnection.ConnectionType.OUTPUT)
                    outputConnections.add(connection);
        return outputConnections;
    }

	/**
	 * Método que retorna uma lista com as conexões nos inputs de um componente
	 * @return
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
	 * @return	->	Mapeamento das conexões e direções
	 */
    public Map<Direction, List<ComponentConnection>> getConnections() {
        return connections;
    }

	//Métodos que retornam uma lista com as possíveis direções de inputs e outputs, respectivamente
    public final ImmutableList<Direction> getPossibleInputDirections() { return possibleDirectionsInfo.possibleInputDirections; }
    public final ImmutableList<Direction> getPossibleOutputDirections() { return possibleDirectionsInfo.possibleOutputDirections; }

	/**
	 * Método que retorna a toString de um componente, adicionando seu ID
	 * no final da String, facilitando identificação dos componentes
	 * durante compilição e execução
	 */
    @Override
    public String toString() {
        return componentName+"_"+getID();
    }
}
