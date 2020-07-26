package io.github.marcuscastelo.quartus.circuit.components.executor;

import io.github.marcuscastelo.quartus.circuit.CircuitDescriptor;
import io.github.marcuscastelo.quartus.circuit.CircuitExecutor;
import io.github.marcuscastelo.quartus.circuit.ComponentConnection;
import io.github.marcuscastelo.quartus.circuit.QuartusBus;
import io.github.marcuscastelo.quartus.circuit.components.InputDescriptor;
import io.github.marcuscastelo.quartus.circuit.components.info.ComponentExecutionInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Classe que permite a mapeação dos Inputs no circuito,
 * possibilitando diferenciá-los pela posição e atribuir
 * corretamente as mudanças e ordem de execução
 */
public class ExecutableWorldInput extends ExecutableComponent {
	//Variáveis auxiliares para mapear os Inputs
    public final World world;
    public final BlockPos pos;

	/**
	 * Construtor padrão, que liga o Input no circuito ao WorldInput
	 * @param world		Mundo que está sendo jogado
	 * @param pos		Posição do bloco no mundo
	 * @param inputDescriptor		Input que está sendo enviado ao bloco do Input no mundo 'real'
	 */
    public ExecutableWorldInput(CircuitExecutor executor, World world, BlockPos pos, InputDescriptor inputDescriptor) {
        super(executor, inputDescriptor, new ComponentExecutionInfo(inputDescriptor.getComponentDirectionInfo()));
        this.world = world;
        this.pos = pos;
    }

	/**
	 * Método que faz o update do WorldInput
	 */
    @Override
    public void updateComponent() {
        QuartusBus inputBus = getExecutionInfo().getInput(Direction.SOUTH).get(0);

        try {
            boolean powered = world.isReceivingRedstonePower(pos);
            inputBus.setValue(powered);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        //Propagate input -> output
        super.updateComponent();
    }
}
