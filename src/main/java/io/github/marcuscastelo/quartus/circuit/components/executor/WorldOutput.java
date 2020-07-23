package io.github.marcuscastelo.quartus.circuit.components.executor;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.circuit.CircuitUtils;
import io.github.marcuscastelo.quartus.circuit.ComponentConnection;
import io.github.marcuscastelo.quartus.circuit.QuartusBusInfo;
import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitOutput;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Map;

/**
 * Classe que permite a mapeação dos Outputs no circuito,
 * possibilitando diferenciá-los pela posição e atribuir
 * corretamente as mudanças e ordem de execução
 */
public class WorldOutput extends QuartusCircuitOutput {
	//Variáveis auxiliares para mapear os Outputs
    public final World world;
    public final BlockPos pos;

	/**
	 * Construtor padrão, que liga o Output no circuito ao WorldOutput
	 * @param world	->	Mundo que está sendo jogado
	 * @param pos	->	Posição do bloco no mundo
	 * @param inputImport	->	Input que está sendo enviado ao bloco do Input no mundo 'real'
	 */
    public WorldOutput(World world, BlockPos pos, QuartusCircuitOutput outputImport) {
        super(outputImport.getID());
        this.world = world;
        this.pos = pos;

        Map<Direction, List<ComponentConnection>> connectionMap = outputImport.getConnections();
        for (Map.Entry<Direction, List<ComponentConnection>> connectionEntry: connectionMap.entrySet()) {
            Direction direction = connectionEntry.getKey();
            List<ComponentConnection> connectionsToImport = connectionEntry.getValue();
            this.getConnections().get(direction).addAll(connectionsToImport);
        }
    }

	/**
	 * Método que faz o update do WorldOutput
	 */
    @Override
    public void updateComponent(QuartusCircuit circuit) {
        //Propagate input -> output
        super.updateComponent(circuit);
        QuartusBusInfo outputBus = getOutputInfo().get(Direction.NORTH);

        BlockState blockState = world.getBlockState(pos);
        try {
            world.setBlockState(pos, blockState.with(Properties.POWERED, outputBus.equals(QuartusBusInfo.HIGH1b)));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
