package io.github.marcuscastelo.quartus.block;

import io.github.marcuscastelo.quartus.circuit.CircuitDescriptor;
import io.github.marcuscastelo.quartus.circuit.components.ComponentDescriptor;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

import java.util.List;

/**
 * Interface que auxilia nas verificações e identificações dos blocos desenvolvidos
 */
public interface QuartusInGameComponent {
	/**
	 * Assinatura do método que retorna a direção que um dado BlockState tem
	 * @param state		BlockState do bloco
	 * @return		Direção que está virado
	 */
	Direction getFacingDirection(BlockState state);
	
	/**
	 * Assinarura de método que retorna o componente do circuito
	 * @return		Componente do circuito
	 */
	ComponentDescriptor createCircuitComponent(CircuitDescriptor circuit);
	
	/**
	 * Assinatura do método que retorna uma lista com as direções de cada Input da sequência
	 * @return		Lista com direções de cada Input
	 */
	List<Direction> getPossibleInputDirections(Direction facingDirection);
	
	/**
	 * Assinatura do método que retorna uma lista com as direções de cada Output da sequência
	 * @return		Lista com direções de cada Output
	 */
    List<Direction> getPossibleOutputDirections(Direction facingDirection);
}
