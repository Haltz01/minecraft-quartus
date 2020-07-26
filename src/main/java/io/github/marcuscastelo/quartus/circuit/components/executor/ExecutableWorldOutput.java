package io.github.marcuscastelo.quartus.circuit.components.executor;

import io.github.marcuscastelo.quartus.circuit.CircuitExecutor;
import io.github.marcuscastelo.quartus.circuit.ComponentConnection;
import io.github.marcuscastelo.quartus.circuit.QuartusBus;
import io.github.marcuscastelo.quartus.circuit.CircuitDescriptor;
import io.github.marcuscastelo.quartus.circuit.components.OutputDescriptor;
import io.github.marcuscastelo.quartus.circuit.components.info.ComponentExecutionInfo;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Classe que permite a mapeação dos Outputs no circuito,
 * possibilitando diferenciá-los pela posição e atribuir
 * corretamente as mudanças e ordem de execução
 */
public class ExecutableWorldOutput extends ExecutableComponent {
	//Variáveis auxiliares para mapear os Outputs
    public final World world;
    public final BlockPos pos;

	/**
	 * Construtor padrão, que liga o Output no circuito ao WorldOutput
	 * @param world		Mundo que está sendo jogado
	 * @param pos		Posição do bloco no mundo
	 * @param outputImport		Output que está sendo enviado ao bloco do Input no mundo 'real'
	 */
    public ExecutableWorldOutput(CircuitExecutor executor, World world, BlockPos pos, OutputDescriptor outputImport) {
        super(executor, outputImport, new ComponentExecutionInfo(outputImport.getComponentDirectionInfo()));
        this.world = world;
        this.pos = pos;
    }

	/**
	 * Método que faz o update do WorldOutput
	 */
    @Override
    public void updateComponent() {
        //Propagate input -> output
        super.updateComponent();
        QuartusBus outputBus = getExecutionInfo().getOutput(Direction.NORTH).get(0);

        BlockState blockState = world.getBlockState(pos);
        try {
            world.setBlockState(pos, blockState.with(Properties.POWERED, outputBus.equals(QuartusBus.HIGH1b)));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
