package io.github.marcuscastelo.quartus.circuit;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.circuit.components.CircuitComponent;
import io.github.marcuscastelo.quartus.circuit.components.CircuitInput;
import io.github.marcuscastelo.quartus.circuit.components.CircuitOutput;
import io.github.marcuscastelo.quartus.network.QuartusSerializable;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

import java.util.*;

/**
 * Classe que implementa o circuito, serializando-o (tranformar em bytes para interpretar)
 */
public class QuartusCircuit implements QuartusSerializable<QuartusCircuit, String> {

	//Varáveis que mapeiam os Inputs(entradas), Outputs(saídas) e outros componentes
    private final Map<Integer, CircuitInput> circuitInputs;
    private final Map<Integer, CircuitOutput> circuitOutputs;
    private final Map<Integer, CircuitComponent> otherComponents;

	//Construtor padrão da classe QuartusCircuit, adicionando os componentes ao circuito
    public QuartusCircuit(List<CircuitComponent> components) {
        this();
        for (CircuitComponent component: components) addComponent(component);
    }

	//Construtor da classe QuartusCircuit, que cria HashMap's para Inputs, Outputs e componentes
    public QuartusCircuit() {
        circuitInputs = new HashMap<>();
        circuitOutputs = new HashMap<>();
        otherComponents = new HashMap<>();
    }

	/**
	 * Método que adiciona um componente ao circuito, de acordo com sua classificação:
	 * - Input
	 * - Output
	 * - Componentes gerais
	 * @param component		Componente a ser adicionado
	 */
    public void addComponent(CircuitComponent component) {
        if (component instanceof CircuitInput)
            circuitInputs.putIfAbsent(component.getID(), (CircuitInput) component);
        else if (component instanceof CircuitOutput)
            circuitOutputs.putIfAbsent(component.getID(), (CircuitOutput) component);
        else
            otherComponents.putIfAbsent(component.getID(), component);
    }

	/**
	 * Método que atrela um componente a um identificador ID
	 * @param ID		identificador
	 * @param component		Componente a ser atrelado
	 */
    public void setComponentAtID(int ID, CircuitComponent component) {
        if (component == null) throw new IllegalArgumentException("Component shall not be null");

        CircuitComponent match = otherComponents.getOrDefault(ID, null);

        if (match != null)  {
            otherComponents.put(ID, component);
            return;
        }
        else match = circuitInputs.getOrDefault(ID, null);

        if (match != null) {
            if (component instanceof CircuitInput)
                circuitInputs.put(ID, (CircuitInput) component);
            else Quartus.LOGGER.error("Illegal argument @QuartusCircuit::setComponentAt(): input is wrong type");
            return;
        }
        else match = circuitOutputs.getOrDefault(ID, null);

        if (match != null) {
            if (component instanceof CircuitOutput)
                circuitOutputs.put(ID, (CircuitOutput) component);
            else Quartus.LOGGER.error("Illegal argument @QuartusCircuit::setComponentAt(): output is wrong type");
            return;
        }

        Quartus.LOGGER.warn("QuartusCircuit::setComponentAt should not be used to add new components (comp = " + component.toString() + ")");
        otherComponents.put(ID, component);
    }

	//Métodos que retornam a quantidade de Inputs e Outputs identificados no circuito
    public int getInputCount() { return circuitInputs.size(); }
    public int getOutputCount() { return circuitOutputs.size(); }

	//Métodos que armazenam em uma lista os Inputs e Outputs
    public List<CircuitInput> getInputs() { return new ArrayList<>(this.circuitInputs.values()); }
    public List<CircuitOutput> getOutputs() { return new ArrayList<>(this.circuitOutputs.values()); }
    
	//Método que cria uma ligação entre dois componentes, de acordo com suas entradas e saídas
    public void addLink(Direction AtoBDirection, Direction BtoADirection, CircuitComponent compA, CircuitComponent compB) {
        compA.addConnection(AtoBDirection, new ComponentConnection(ComponentConnection.ConnectionType.OUTPUT, compB.toString(), BtoADirection));
        compB.addConnection(BtoADirection, new ComponentConnection(ComponentConnection.ConnectionType.INPUT, compA.toString(), AtoBDirection));
    }

	/**
	 * Método que retorna um componente de acordo com seu Identificador ID
	 * @param ID		Identificador de referência
	 * @return		Componente retornado de acordo com o parâmetro ID
	 */
    @Nullable
    public CircuitComponent getComponentByID(int ID) {
        CircuitComponent component = otherComponents.getOrDefault(ID, null);
        if (component == null) component = circuitInputs.getOrDefault(ID, null);
        if (component == null) component = circuitOutputs.getOrDefault(ID, null);
        return component;
    }

	//Método que atualiza a saída dos Inputs
	//A saída do Input é a mesma que a sua entrada do mundo "real"
    private void updateInputs() {
        for (CircuitInput input: circuitInputs.values()) {
            input.updateComponent(this);
        }
    }

	//Método que atualiza as saídas dos componentes
	//De acordo com cada entrada e tipo de componente
	//a saída calculada
    private void updateComponents() {
        for (CircuitComponent component: otherComponents.values()) {
            component.updateComponent(this);
        }
    }

	//Método que atualiza a saída dos Outputs
	//A saída para o mundo "real" é a mesma que a entrada
    private void updateOutputs() {
        for (CircuitOutput output: circuitOutputs.values()) {
            output.updateComponent(this);
        }
    }

	//Método que faz a chamada de atualização para os componentes do circuito
    public void updateCircuit() {
        updateInputs();
        updateComponents();
        updateOutputs();
    }

	/**
	 * Método que serializa o circuito com base nos seus circuitos.
	 * Retorna uma string, o circuito serializado, que indica
	 * quais componentes ligam entre si
	 */
    @Override
    public String serialize() {
        StringBuilder str = new StringBuilder();
        Queue<CircuitComponent> componentsToPrint = new LinkedList<>(circuitInputs.values());

        List<CircuitComponent> alreadyPrintedComponents = new ArrayList<>();
        while (!componentsToPrint.isEmpty()) {
            CircuitComponent component = componentsToPrint.poll();
            if (alreadyPrintedComponents.contains(component)) continue;
            alreadyPrintedComponents.add(component);

            str.append(component.getOutputConnectionsString());
            for (ComponentConnection connection: component.getOutputConnections())
                componentsToPrint.add(getComponentByID(CircuitUtils.getComponentStrInfo(connection.connectToCompStr).getRight()));
        }

        return str.toString();
    }


	/**
	 * Método reverso da Unserialize.
	 * A partir de uma string serializada, faz os splits
	 * e de acordo com as strings resultantes identifica
	 * os componentes e os liga
	 */
    @Override
    public void unserialize(String serial) {
        Map<Integer, CircuitComponent> initializedGates = new HashMap<>();
        String[] lines = serial.split("\n");
        for (String line: lines) {
            if (!line.contains("/") || !line.contains("->")) continue;
            String[] sides = line.split("/");

            String fromGateStr = sides[0].trim();
            String toGateStr = sides[2].trim();

            String directionInfo = sides[1];
            String[] directions = directionInfo.split("->");
            String directionAtoBStr = directions[0].trim();
            String directionBtoAStr = directions[1].trim();

            Direction directionAtoB = Direction.byName(directionAtoBStr);
            Direction directionBtoA = Direction.byName(directionBtoAStr);

            Pair<String, Integer> fromGateInfo = CircuitUtils.getComponentStrInfo(fromGateStr);
            Pair<String, Integer> toGateInfo = CircuitUtils.getComponentStrInfo(toGateStr);

            String fromGateType = fromGateInfo.getLeft();
            int fromGateID = fromGateInfo.getRight();

            String toGateType = toGateInfo.getLeft();
            int toGateID = toGateInfo.getRight();

            CircuitComponent fromComp = initializedGates.getOrDefault(fromGateID, null);
            CircuitComponent toComp = initializedGates.getOrDefault(toGateID, null);

            if (fromComp == null) {
                fromComp = CircuitUtils.createPolimorphicComponent(fromGateType, fromGateID);
                initializedGates.putIfAbsent(fromGateID, fromComp);
                addComponent(fromComp);
            }

            if (toComp == null) {
                toComp = CircuitUtils.createPolimorphicComponent(toGateType, toGateID);
                initializedGates.putIfAbsent(toGateID, toComp);
                addComponent(toComp);
            }
            addLink(directionAtoB, directionBtoA, fromComp, toComp);
        }
    }

	/**
	 * Método que retorna a string serializada do circuito
	 */
    @Override
    public String toString() {
        return serialize();
    }
}
