package io.github.marcuscastelo.quartus.circuit.components.info;

import io.github.marcuscastelo.quartus.circuit.QuartusLogic;
import io.github.marcuscastelo.quartus.circuit.components.CircuitComponent;

import java.util.function.Supplier;

/**
 * Classe que atribui as informações dadas a um dado componente
 */
public class ComponentInfo {
	//Variáveis que identificam o componente e atribuem a informação
    public final Supplier<CircuitComponent> componentSupplier;
    public final ComponentDirectionInfo directionInfo;
    public final QuartusLogic componentLogic;

	/**
	 * Construtor padrão da classe ComponentInfo
	 */
    public ComponentInfo(Supplier<CircuitComponent> componentSupplier, ComponentDirectionInfo directionInfo, QuartusLogic componentLogic) {
        this.componentSupplier = componentSupplier;
        this.directionInfo = directionInfo;
        this.componentLogic = componentLogic;
    }
}
