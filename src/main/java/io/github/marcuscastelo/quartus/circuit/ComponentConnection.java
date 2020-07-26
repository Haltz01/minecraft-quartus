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

    /**
     * Cria uma conexão entre dois componentes
     * @param type              Tipo de conexão
     * @param connectToCompStr  String que guarda a representação em texto do componente ao qual o componente A se conecta
     * @param BtoADirection     Direção para "fora" do componente B
     */
    public ComponentConnection(ConnectionType type, String connectToCompStr, Direction BtoADirection) {
        this.type = type;
        this.connectToCompStr = connectToCompStr;
        this.BtoADirection = BtoADirection;
    }

	/**
	 * Método auxiliar que retorna o tipo de componente em questão
	 * @return		ConnectionType, que define qual tipo de componente se trata
	 */
    public ConnectionType getType() {
        return type;
    }
}
