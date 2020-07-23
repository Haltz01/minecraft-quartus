package io.github.marcuscastelo.quartus.circuit.components;

import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.registry.QuartusLogics;
import net.minecraft.util.math.Direction;

/**
 * Classe que define o Output utilizado pelo Mod
 */
public class QuartusCircuitOutput extends QuartusCircuitComponent {
	//Variáveis que definem o tipo e a direção do Input
    public static final String TYPE = "QuartusOutput";
    public static final QuartusCircuitComponentDirectionInfo outputDirectionInfo = new QuartusCircuitComponentDirectionInfo(Direction.SOUTH, Direction.NORTH);

	/**
	 * Construtor da Classe Output
	 * @param ID	->	Identificador do componente
	 */
    public QuartusCircuitOutput(int ID) {
        super(TYPE, outputDirectionInfo, ID, QuartusLogics.OUTPUT);
    }

	/**
	 * Construtor da Classe Output, sem uso do identificador
	 */
    public QuartusCircuitOutput() {
        super(TYPE, outputDirectionInfo, QuartusLogics.OUTPUT);
    }

	/**
	 * Método que faz a chamada para dar update no Input no circuito
	 */
    public void updateComponent(QuartusCircuit circuit) {
        super.updateComponent(circuit);
    }

}
