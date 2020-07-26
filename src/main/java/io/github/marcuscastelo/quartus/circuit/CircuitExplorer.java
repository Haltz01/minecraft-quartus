package io.github.marcuscastelo.quartus.circuit;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.QuartusInGameComponent;
import io.github.marcuscastelo.quartus.block.circuit_parts.WireBlock;
import io.github.marcuscastelo.quartus.circuit.components.CircuitComponent;
import io.github.marcuscastelo.quartus.circuit.components.CircuitInput;
import io.github.marcuscastelo.quartus.circuit.components.CircuitOutput;
import io.github.marcuscastelo.quartus.circuit.components.info.ComponentInfo;
import io.github.marcuscastelo.quartus.registry.QuartusCircuitComponents;
import io.github.marcuscastelo.quartus.registry.QuartusLogics;
import io.github.marcuscastelo.quartus.util.DirectionUtils;
import io.github.marcuscastelo.quartus.util.WireConnector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.marcuscastelo.quartus.util.DirectionUtils.getAbsoluteDirection;
import static io.github.marcuscastelo.quartus.util.DirectionUtils.getRelativeDirection;

/**
 * Classe que contém algoritmos de exploração de circuito
 */
public class CircuitExplorer {
	/**
	 * Sub-Classe de dados usada para retornar o resulrado de uma exploração
     * Guarda a posição o bloco encontrado (bloco B) a partir do bloco A (não está presente por ser redundante)
     * Guarda a direção que saiu do bloco A (AtoB)
     * Guarda a direção que chegou no bloco B (BtoA)
     * OBS: se a conexão for feita por meio de um fio, as direções não necessariamente serão opostas
	 */
    public static class ConnectedBlocksInfo {
        public final Direction AtoB;
        public final Direction BtoA;
        public final BlockPos bPos;

		/**
		 * Construtor padrão da Sub-Classe
		 * As variáveis são atribuídas adequadamente,
		 * especificando o caminho/direção a ser seguido
		 */
        public ConnectedBlocksInfo(Direction AtoB, Direction BtoA, BlockPos bPos) {
            this.AtoB = AtoB;
            this.BtoA = BtoA;
            this.bPos = bPos;
        }
    }

    private static boolean isComponent(World world, BlockPos pos) {
        return (world.getBlockState(pos).getBlock() instanceof QuartusInGameComponent);

    }

    private static boolean isWire(World world, BlockPos pos) {
        return (world.getBlockState(pos).getBlock() instanceof WireBlock);

    }

	/**
	 * Método que analisa o circuito a partir de um dado componente do Mod (componente A),
	 * identificando o caminho a ser seguido com os componentes e adicionando-os a uma lista de Conexões
	 * @param world			Mundo que está sendo jogado
	 * @param circuit		TODO: ver oq é isso
	 * @param componentA	Nó/Componente de origem no circuito
	 * @param compAPos		Posição do nó/componente de origem TODO: remover parametro?
	 * @return		Lista com as informações dos nós explorados
	 */
    public static List<ConnectedBlocksInfo> getConnectedNodesInfo(World world, QuartusCircuit circuit, CircuitComponent componentA, BlockPos compAPos) {
        List<ConnectedBlocksInfo> connectedNodesAboluteInfo = new ArrayList<>();

        if (!isWire(world, compAPos) && !isComponent(world,compAPos)) {
            throw new IllegalArgumentException("Trying to get connected nodes from non-node");
        }

        BlockState compABlockState = world.getBlockState(compAPos);
        Direction compAFacingDir = compABlockState.get(Properties.HORIZONTAL_FACING);

        //Para cada direção que puder haver um output no componente A
        for (Direction relativeAtoB: componentA.getPossibleOutputDirections()) {
            //Obtém o blockstate imediatamente adjacente e exclui os que não são blocos do mod

            //Obtém as direções absolutas
            Direction absoluteAtoB = getAbsoluteDirection(compAFacingDir, relativeAtoB);

            //Obtém informações dos vizinhos imediatos
            BlockPos neighborPos = compAPos.offset(absoluteAtoB);
            Block neighborBlock = world.getBlockState(neighborPos).getBlock();

            if (neighborBlock instanceof QuartusInGameComponent){
                connectedNodesAboluteInfo.add(new ConnectedBlocksInfo(relativeAtoB, relativeAtoB.getOpposite(), neighborPos));
            }
            else if (neighborBlock instanceof WireBlock) {
                //Se o vizinho for um fio, siga o fio e obtenha a posição do nó em sua extremidade
                Optional<ConnectedBlocksInfo> throughWireInfo = getNextComponentThroughWires(world, compAPos, absoluteAtoB);
                throughWireInfo.ifPresent(connectedNodesAboluteInfo::add);
            }
        }
        
        return connectedNodesAboluteInfo;
    }

	/** TODO: melhorar comentario
	 * Método auxiliar que analisa um fio/wire do circuito e identifica qual direção para seguir
	 * @param world             Mundo em que o circuito é compilado
	 * @param compAPos        Posição do bloco que chamou a função
	 * @param absoluteAtoB  Direção que o bloco que chamou andou para encontrar o fio que está prestes a ser percorrido
	 * @return                  Par de posição e direção do componente que foi encontrado
	 */
    private static Optional<ConnectedBlocksInfo> getNextComponentThroughWires(World world, BlockPos compAPos, Direction absoluteAtoB) {
        BlockPos compBPos = compAPos.offset(absoluteAtoB);
        Optional<Direction> returnedDirection = WireConnector.getNextDirection(world, compBPos, absoluteAtoB);
        if (!returnedDirection.isPresent()) return Optional.empty();

        Direction directionToGo = returnedDirection.get();

        while (true) {
            //Se chegar num fio não recíproco ou em um beco sem saída
            compBPos = WireConnector.navigateWire(world, compBPos, directionToGo);
            if (compBPos == null) break;

            //Se o bloco atual não for mais um fio, encerrar
            if (!(world.getBlockState(compBPos).getBlock() instanceof WireBlock)) break;

            //Se estiver em um fio que não reconhece a direção informada (não deveria acontecer, pois o if acima checa)
            returnedDirection = WireConnector.getNextDirection(world, compBPos, directionToGo);
            if (!returnedDirection.isPresent()) return Optional.empty();
            directionToGo = returnedDirection.get();
        }
        if (!isComponent(world, compBPos)) return Optional.empty();

        Direction AFacing = world.getBlockState(compAPos).get(Properties.HORIZONTAL_FACING);
        Direction BFacing = world.getBlockState(compBPos).get(Properties.HORIZONTAL_FACING);

        Direction relativeAtoB = DirectionUtils.getRelativeDirection(AFacing, absoluteAtoB);
        Direction relativeBtoA = DirectionUtils.getRelativeDirection(BFacing, directionToGo.getOpposite());

        return Optional.of(new ConnectedBlocksInfo(relativeAtoB, relativeBtoA, compBPos));
    }

    /**
	 * Método que retorna um objeto de classe genérica pertencente aos componentes do circuito
	 * Podem ser -	Input
	 * 			 -	Output
	 * 			 -	Porta Lógica
	 * @param gateType		String com o tipo de porta
	 * @param gateID		Int com o ID da porta
	 * @return		Objeto genérico de acordo com os parâmetros passados
	 */
    public static CircuitComponent createPolimorphicComponent(String gateType, int gateID) {
        ComponentInfo info = QuartusCircuitComponents.getComponentInfoByName(gateType);
        if (gateType.equals(CircuitInput.COMP_NAME))
            return new CircuitInput(gateID);
        else if (gateType.equals(CircuitOutput.COMP_NAME))
            return new CircuitOutput(gateID);
        else
            return new CircuitComponent(gateType, info.directionInfo, gateID, QuartusLogics.getLogicByName(gateType));
    }

	/**
	 * Método auxiliar que retorna um par,
	 * relacionando a String que identifica um componente
	 * com seu ID identificador
	 * @param componentStr		String que identifica um componente
	 * @return		Par de String e Int, que identificam um tipo de componente
	 */
    public static Pair<String, Integer> getComponentStrInfo(String componentStr) {
        String[] params = componentStr.split("_");
        String gateType = params[0];
        int gateID = Integer.parseInt(params[1]);
        return new Pair<>(gateType, gateID);
    }
}