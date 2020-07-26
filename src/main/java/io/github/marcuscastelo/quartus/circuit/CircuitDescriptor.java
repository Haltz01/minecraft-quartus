package io.github.marcuscastelo.quartus.circuit;

import com.google.common.collect.ImmutableList;
import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.circuit.components.ComponentDescriptor;
import io.github.marcuscastelo.quartus.circuit.components.OutputDescriptor;
import io.github.marcuscastelo.quartus.circuit.components.InputDescriptor;
import io.github.marcuscastelo.quartus.network.QuartusSimetricSerializer;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

import java.util.*;

/**
 * Classe que implementa o circuito, serializando-o (tranformar em bytes para interpretar)
 */
public class CircuitDescriptor {

	//Varáveis que mapeiam os Inputs(entradas), Outputs(saídas) e outros componentes
    private final Map<Integer, InputDescriptor> circuitInputs;
    private final Map<Integer, OutputDescriptor> circuitOutputs;
    private final Map<Integer, ComponentDescriptor> otherComponents;
    private int lastID;

	//Construtor padrão da classe QuartusCircuit, adicionando os componentes ao circuito
    public CircuitDescriptor(List<ComponentDescriptor> components) {
        this();
        for (ComponentDescriptor component: components) addComponent(component);
    }

	//Construtor da classe QuartusCircuit, que cria HashMap's para Inputs, Outputs e componentes
    public CircuitDescriptor() {
        circuitInputs = new HashMap<>();
        circuitOutputs = new HashMap<>();
        otherComponents = new HashMap<>();
        this.lastID = 1;
    }

    public int generateNextID() {
        return lastID++;
    }

	/**
	 * Método que adiciona um componente ao circuito, de acordo com sua classificação:
	 * - Input
	 * - Output
	 * - Componentes gerais
	 * @param component		Componente a ser adicionado
	 */
    public void addComponent(ComponentDescriptor component) {
        if (component instanceof InputDescriptor)
            circuitInputs.putIfAbsent(component.getID(), (InputDescriptor) component);
        else if (component instanceof OutputDescriptor)
            circuitOutputs.putIfAbsent(component.getID(), (OutputDescriptor) component);
        else
            otherComponents.putIfAbsent(component.getID(), component);
    }

	/**
	 * Método que atrela um componente a um identificador ID
	 * @param ID		identificador
	 * @param component		Componente a ser atrelado
	 */
    public void setComponentAtID(int ID, ComponentDescriptor component) {
        if (component == null) throw new IllegalArgumentException("Component shall not be null");

        ComponentDescriptor match = otherComponents.getOrDefault(ID, null);

        if (match != null)  {
            otherComponents.put(ID, component);
            return;
        }
        else match = circuitInputs.getOrDefault(ID, null);

        if (match != null) {
            if (component instanceof InputDescriptor)
                circuitInputs.put(ID, (InputDescriptor) component);
            else Quartus.LOGGER.error("Illegal argument @QuartusCircuit::setComponentAt(): input is wrong type");
            return;
        }
        else match = circuitOutputs.getOrDefault(ID, null);

        if (match != null) {
            if (component instanceof OutputDescriptor)
                circuitOutputs.put(ID, (OutputDescriptor) component);
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
    public ImmutableList<InputDescriptor> getInputsList() { return ImmutableList.copyOf(this.circuitInputs.values()); }
    public ImmutableList<OutputDescriptor> getOutputsList() { return ImmutableList.copyOf(this.circuitOutputs.values()); }
    public ImmutableList<ComponentDescriptor> getOtherComponentsList() { return ImmutableList.copyOf(otherComponents.values()); }

    //Método que cria uma ligação entre dois componentes, de acordo com suas entradas e saídas
    public void addLink(Direction AtoBDirection, Direction BtoADirection, ComponentDescriptor compA, ComponentDescriptor compB) {
        compA.addConnection(AtoBDirection, new ComponentConnection(ComponentConnection.ConnectionType.OUTPUT, compB.toString(), BtoADirection));
        compB.addConnection(BtoADirection, new ComponentConnection(ComponentConnection.ConnectionType.INPUT, compA.toString(), AtoBDirection));
    }

	/**
	 * Método que retorna um componente de acordo com seu Identificador ID
	 * @param ID		Identificador de referência
	 * @return		Componente retornado de acordo com o parâmetro ID
	 */
    @Nullable
    public ComponentDescriptor getComponentByID(int ID) {
        ComponentDescriptor component = otherComponents.getOrDefault(ID, null);
        if (component == null) component = circuitInputs.getOrDefault(ID, null);
        if (component == null) component = circuitOutputs.getOrDefault(ID, null);
        if (component == null) throw new RuntimeException("Unknown component of ID " + ID);
        return component;
    }

    public static class Serializer implements QuartusSimetricSerializer<CircuitDescriptor, String> {
        /**
         * Método que serializa o circuito com base nos seus circuitos.
         * Retorna uma string, o circuito serializado, que indica
         * quais componentes ligam entre si
         */
        @Override
        public String serialize(CircuitDescriptor circuitDescriptor) {
            StringBuilder str = new StringBuilder();
            Queue<ComponentDescriptor> componentsToPrint = new LinkedList<>(circuitDescriptor.circuitInputs.values());

            List<ComponentDescriptor> alreadyPrintedComponents = new ArrayList<>();
            while (!componentsToPrint.isEmpty()) {
                ComponentDescriptor component = componentsToPrint.poll();
                if (alreadyPrintedComponents.contains(component)) continue;
                alreadyPrintedComponents.add(component);

                str.append(component.getOutputConnectionsString());
                for (ComponentConnection connection: component.getOutputConnections())
                    componentsToPrint.add(circuitDescriptor.getComponentByID(ComponentDescriptor.getComponentStrInfo(connection.connectToCompStr).getRight()));
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
        public CircuitDescriptor unserialize(String serial) {
            CircuitDescriptor descriptor = new CircuitDescriptor();
            Map<Integer, ComponentDescriptor> initializedGates = new HashMap<>();
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

                Pair<String, Integer> fromGateInfo = ComponentDescriptor.getComponentStrInfo(fromGateStr);
                Pair<String, Integer> toGateInfo = ComponentDescriptor.getComponentStrInfo(toGateStr);

                String fromGateType = fromGateInfo.getLeft();
                int fromGateID = fromGateInfo.getRight();

                String toGateType = toGateInfo.getLeft();
                int toGateID = toGateInfo.getRight();

                ComponentDescriptor fromComp = initializedGates.getOrDefault(fromGateID, null);
                ComponentDescriptor toComp = initializedGates.getOrDefault(toGateID, null);

                if (fromComp == null) {
                    fromComp = ComponentDescriptor.createPolimorphicComponent(fromGateType, fromGateID);
                    initializedGates.putIfAbsent(fromGateID, fromComp);
                    descriptor.addComponent(fromComp);
                }

                if (toComp == null) {
                    toComp = ComponentDescriptor.createPolimorphicComponent(toGateType, toGateID);
                    initializedGates.putIfAbsent(toGateID, toComp);
                    descriptor.addComponent(toComp);
                }
                descriptor.addLink(directionAtoB, directionBtoA, fromComp, toComp);
            }
            return descriptor;
        }
    }

	/**
	 * Método que retorna a string serializada do circuito
	 */
    @Override
    public String toString() {
        return new Serializer().serialize(this);
    }

    public String serialize() {
        return new Serializer().serialize(this);
    }
}
