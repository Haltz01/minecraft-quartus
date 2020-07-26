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
 * Classe que auxilia na conexão dos componentes do jogo que formam um circuito
 */
public class CircuitUtils {
	/**
	 * Sub-Classe para auxiliar na organização da lógica
	 */
    public static class ConnectedNodeInfo {
        public final Direction AtoB, BtoA;
        public final BlockPos bPos;

		/**
		 * Construtor padrão da Sub-Classe
		 * As variáveis são atribuídas adequadamente,
		 * especificando o caminho/direção a ser seguido
		 */
        public ConnectedNodeInfo(Direction AtoB, Direction BtoA, BlockPos bPos) {
            this.AtoB = AtoB;
            this.BtoA = BtoA;
            this.bPos = bPos;
        }
    }

	/**
	 * Método que analisa o circuito a partir de um dado componente do Mod,
	 * identificando o caminho a ser seguido com os componentes e 
	 * retornando uma lista com os dados adquiridos
	 * @param world			Mundo que está sendo jogado
	 * @param circuit		Circuito a ser analisado
	 * @param originNode	Nó/Componente de origem no circuito
	 * @param originPos		Posição do nó/componente de origem
	 * @return		Lista com os nós mapeados
	 */
    public static List<ConnectedNodeInfo> getConnectedNodesInfo(World world, QuartusCircuit circuit, CircuitComponent originNode, BlockPos originPos) {
        List<ConnectedNodeInfo> connectedNodesInfo = new ArrayList<>();

        //Para cada direção de output do nó origen
        for (Direction relativeDirectionOutOfOriginNode: originNode.getPossibleOutputDirections()) {
            //Obtém o blockstate imediatamente adjacente e exclui os que não são blocos do mod
            BlockState originBs = world.getBlockState(originPos);
            if (!(originBs.getBlock() instanceof QuartusInGameComponent) && !(originBs.getBlock() instanceof WireBlock)) continue;

            //Obtém as direções absolutas
            Direction originFacingDir = originBs.get(Properties.HORIZONTAL_FACING);
            Direction absoluteDirectionOutOfOriginNode = getAbsoluteDirection(originFacingDir, relativeDirectionOutOfOriginNode);
            Direction absoluteDirectionOutOfTargetNode;

            //Obtém informações dos vizinhos imediatos
            BlockPos immediateNeighborPos = originPos.offset(absoluteDirectionOutOfOriginNode);
            Block immediateNeighborBlock = world.getBlockState(immediateNeighborPos).getBlock();

            //Variável a ser definida nos ifs a seguir (posição do próximo componente)
            BlockPos targetPos = null;

			//Segue os fios até o próximo nó
            if (immediateNeighborBlock instanceof QuartusInGameComponent){
                //Se o vizinho for um componente, ele é o próprio componente
                targetPos = immediateNeighborPos;
                absoluteDirectionOutOfTargetNode = absoluteDirectionOutOfOriginNode.getOpposite();
            }
            else if (immediateNeighborBlock instanceof WireBlock) {
                //Se o vizinho for um fio, siga o fio e obtenha a posição do nó em sua extremidade
                Pair<BlockPos, Direction> throughWireInfo = getWireEndBlockInfo(world, immediateNeighborPos, absoluteDirectionOutOfOriginNode);

                BlockPos foundBlockPos = throughWireInfo.getLeft();
                absoluteDirectionOutOfTargetNode = throughWireInfo.getRight();

                if (foundBlockPos != null) {
                    Block targetBlock = world.getBlockState(foundBlockPos).getBlock();
                    if (targetBlock instanceof QuartusInGameComponent) {
                        System.out.println("Após os fios, encontrado " + targetBlock);
                        targetPos = foundBlockPos;
                    }
                    else {
                        System.out.println("O FIO ESTÁ DESCONECTADO NO FIM DELE");
                        System.out.println("Acaba em " + foundBlockPos);
                        System.out.println("É um " + targetBlock);
                    }
                }
            } else {
                Quartus.LOGGER.info(String.format("Node com porta desconectada em %s", immediateNeighborPos.toString()));
                continue;
            }

            //Nenhum nó nessa direção
            if (targetPos == null) continue;

			/*
			  Pega o nó na posição alvo e analisa suas entradas e saídas, conectando-os
			 */
            BlockState targetBlockState = world.getBlockState(targetPos);
            Block targetBlock = targetBlockState.getBlock();
            Direction targetFacingDir = targetBlockState.get(Properties.HORIZONTAL_FACING);
            if (targetBlock instanceof QuartusInGameComponent) {
                List<Direction> absolutePossibleInputDirectionsForTarget = ((QuartusInGameComponent) targetBlock).getPossibleInputDirections(targetFacingDir);
                if (absolutePossibleInputDirectionsForTarget.contains(absoluteDirectionOutOfTargetNode)) {
                    Direction relativeDirectionOutOfTargetNode = getRelativeDirection(targetFacingDir, absoluteDirectionOutOfTargetNode);

                    System.out.println(relativeDirectionOutOfOriginNode + " --- " + relativeDirectionOutOfTargetNode + " --- " + targetPos);
                    connectedNodesInfo.add(new ConnectedNodeInfo(relativeDirectionOutOfOriginNode, relativeDirectionOutOfTargetNode, targetPos));
                } else {
                    System.out.println("Saindo do node, conectei em " + absoluteDirectionOutOfTargetNode);
                    System.out.println("Node olhando para " + targetFacingDir);
                    System.out.println(targetPos.toString() + " aproximado por direção ruim");
                }
            }
        }
        return connectedNodesInfo;
    }

	/** TODO: melhorar comentario
	 * Método auxiliar que analisa um fio/wire do circuito e identifica qual direção para seguir
	 * @param world             Mundo em que o circuito é compilado
	 * @param initialPos        Posição do bloco que chamou a função
	 * @param initialDirection  Direção que o bloco que chamou andou para encontrar o fio que está prestes a ser percorrido
	 * @return                  Par de posição e direção do componente que foi encontrado
	 */
    private static Pair<BlockPos, Direction> getWireEndBlockInfo(World world, BlockPos initialPos, Direction initialDirection) {
        BlockPos currPos = initialPos;
        Direction lastDirection;
        Optional<Direction> returnedDirection = WireConnector.getNextDirection(world, currPos, initialDirection);
        if (!returnedDirection.isPresent()) return new Pair<>(null, null);
        Direction currDirection = returnedDirection.get();

        while (true) {
            lastDirection = currDirection;

            //Se chegar num fio não recíproco ou em um beco sem saída
            currPos = WireConnector.navigateWire(world, currPos, currDirection);
            if (currPos == null) break;

            //Se o bloco atual não for mais um fio, encerrar
            if (!(world.getBlockState(currPos).getBlock() instanceof WireBlock)) break;

            //Se estiver em um fio que não reconhece a direção informada (não deveria acontecer, pois o if acima checa)
            returnedDirection = WireConnector.getNextDirection(world, currPos, currDirection);
            if (!returnedDirection.isPresent()) return new Pair<>(null, null);
            currDirection = returnedDirection.get();
        }

        return new Pair<>(currPos, lastDirection.getOpposite());
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