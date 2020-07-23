package io.github.marcuscastelo.quartus.circuit.components;

import com.google.common.collect.ImmutableList;
import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.registry.QuartusLogics;
import net.minecraft.util.math.Direction;

import java.util.Collections;
import java.util.List;

/**
 * Classe que define o Input utilizado pelo Mod
 */
public class QuartusCircuitInput extends QuartusCircuitComponent {
	//Variáveis que definem o tipo e a direção do Input
    public static final String TYPE = "QuartusInput";
    public static final QuartusCircuitComponentDirectionInfo inputDirectionInfo = new QuartusCircuitComponentDirectionInfo(Direction.SOUTH, Direction.NORTH);

	/**
	 * Construtor da Classe Input
	 * @param ID	->	Identificador do componente
	 */
    public QuartusCircuitInput(int ID) {
        super(TYPE, inputDirectionInfo, ID, QuartusLogics.INPUT);
    }

	/**
	 * Construtor da Classe Input, sem uso do identificador
	 */
    public QuartusCircuitInput() {
        super(TYPE, inputDirectionInfo, QuartusLogics.INPUT);
    }

	/**
	 * Método que faz a chamada para dar update no Input no circuito
	 */
    public void updateComponent(QuartusCircuit circuit) {
        super.updateComponent(circuit);
    }
}
