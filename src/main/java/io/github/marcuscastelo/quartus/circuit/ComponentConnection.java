package io.github.marcuscastelo.quartus.circuit;

import net.minecraft.util.math.Direction;

/**
 * Classe Auxiliar que identifica o tipo de componente,
 * atribuído no Construtor
 */
public class ComponentConnection {
    public enum ConnectionType {
        INPUT, OUTPUT
    }

    public final ConnectionType type;
    public final String connectToCompStr;
	public final Direction BtoADirection;

	//Construtor padrão da Classe ComponentConnection
    public ComponentConnection(ConnectionType type, String connectToCompStr, Direction BtoADirection) {
        this.type = type;
        this.connectToCompStr = connectToCompStr;
        this.BtoADirection = BtoADirection;
    }

	/**
	 * Método auxiliar que retorna o tipo de componente em questão
	 * @return	->	ConnectionType, que define qual tipo de componente se trata
	 */
    public ConnectionType getType() {
        return type;
    }
}
