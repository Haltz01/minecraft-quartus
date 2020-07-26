package io.github.marcuscastelo.quartus.circuit.components.info;

import io.github.marcuscastelo.quartus.circuit.QuartusLogic;
import io.github.marcuscastelo.quartus.circuit.components.ComponentDescriptor;

/**
 * Classe que atribui as informações dadas a um dado componente
 */
public class ComponentInfo {
	//Variáveis que identificam o componente e atribuem a informação
    public final ComponentDescriptor.Builder componentBuilder;
    public final ComponentDirectionInfo directionInfo;
    public final QuartusLogic componentLogic;

	/**
	 * Construtor padrão da classe ComponentInfo
	 */
    public ComponentInfo(ComponentDescriptor.Builder componentBuilder, ComponentDirectionInfo directionInfo, QuartusLogic componentLogic) {
        this.componentBuilder = componentBuilder;
        this.directionInfo = directionInfo;
        this.componentLogic = componentLogic;
    }
}
