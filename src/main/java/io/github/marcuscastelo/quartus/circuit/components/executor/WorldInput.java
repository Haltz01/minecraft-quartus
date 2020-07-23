package io.github.marcuscastelo.quartus.circuit.components.executor;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.circuit.ComponentConnection;
import io.github.marcuscastelo.quartus.circuit.QuartusBusInfo;
import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitInput;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

/**
 * Classe que permite a mapeação dos Inputs no circuito,
 * possibilitando diferenciá-los pela posição e atribuir
 * corretamente as mudanças e ordem de execução
 */
public class WorldInput extends QuartusCircuitInput {
	//Variáveis auxiliares para mapear os Inputs
    public final World world;
    public final BlockPos pos;

	/**
	 * Construtor padrão, que liga o Input no circuito ao WorldInput
	 * @param world	->	Mundo que está sendo jogado
	 * @param pos	->	Posição do bloco no mundo
	 * @param inputImport	->	Input que está sendo enviado ao bloco do Input no mundo 'real'
	 */
    public WorldInput(World world, BlockPos pos, QuartusCircuitInput inputImport) {
        super(inputImport.getID());
        this.world = world;
        this.pos = pos;

        Map<Direction, List<ComponentConnection>> connectionMap = inputImport.getConnections();
        for (Map.Entry<Direction, List<ComponentConnection>> connectionEntry: connectionMap.entrySet()) {
            Direction direction = connectionEntry.getKey();
            List<ComponentConnection> connectionsToImport = connectionEntry.getValue();
            this.getConnections().get(direction).addAll(connectionsToImport);
        }
    }

	/**
	 * Método que faz o update do WorldInput
	 */
    @Override
    public void updateComponent(QuartusCircuit circuit) {
        QuartusBusInfo inputBus = getInputInfo().get(Direction.SOUTH);

        try {
            boolean powered = world.isReceivingRedstonePower(pos);
            inputBus.setValue(powered);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        //Propagate input -> output
        super.updateComponent(circuit);
    }
}
