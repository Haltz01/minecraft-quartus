package io.github.marcuscastelo.quartus.circuit_logic;

import io.github.marcuscastelo.quartus.Quartus;
import net.minecraft.block.Block;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CircuitUtils {
    public static final List<Direction> HORIZONTAL_DIRECTIONS = Arrays.asList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    //Segue os fios até o próximo nó
    public static List<QuartusNode> getConnectedNodes(QuartusCircuit circuit, QuartusNode node) {
        World world = node.world;
        List<QuartusNode> connectedNodes = new ArrayList<>();
        for (Direction approachDirection: node.getOutputDirections()) {
            BlockPos neighborPos = node.pos.offset(approachDirection);
            Block neighborBlock = world.getBlockState(neighborPos).getBlock();
            QuartusNode connectedNode = null;
            if (neighborBlock instanceof QuartusNodeConvertible){
                connectedNode = circuit.getNodeAt(neighborPos);
            }
            else if (neighborBlock instanceof QuartusTransportInfoProvider) {
                Pair<BlockPos, Direction> throughWireNodeInfo = getTransportDestinationInfo(world, neighborPos, approachDirection);

                if (throughWireNodeInfo == null) {
                    System.out.println("Fios mal conectados foram encontrados na linha " + neighborPos.toShortString());
                    continue;
                };

                BlockPos connectedNodePos = throughWireNodeInfo.getLeft();
                approachDirection = throughWireNodeInfo.getRight();

                if (connectedNodePos != null) {
                    Block connectedNodeBlock = world.getBlockState(connectedNodePos).getBlock();
                    if (connectedNodeBlock instanceof QuartusNodeConvertible) {
                        System.out.println("Após os fios, encontrado " + connectedNodeBlock);
                        connectedNode = circuit.getNodeAt(connectedNodePos);
                    }
                    else {
                        System.out.println("O FIO ESTÁ DESCONECTADO NO FIM DELE");
                        System.out.println("Acaba em " + connectedNodePos);
                        System.out.println("É um " + connectedNodeBlock);
                    }
                }
            } else {
                Quartus.LOGGER.info(String.format("Node com porta desconectada em %s", neighborPos.toString()));
                continue;
            }

            //Nenhum nó nessa direção
            if (connectedNode == null) continue;

            if (connectedNode.getInputDirections().contains(approachDirection.getOpposite()))
                connectedNodes.add(connectedNode);
            else
                System.out.println(connectedNode.getNodeType() + " aproximado por direção ruim");

        }
        return connectedNodes;
    }

    private static Pair<BlockPos, Direction> getTransportDestinationInfo(World world, BlockPos initialPos, Direction initialApproach) {
        Block currBlock = world.getBlockState(initialPos).getBlock();
        BlockPos currPos = initialPos;
        Direction lastApproachDir = initialApproach;
        while (currBlock instanceof QuartusTransportInfoProvider) {
            try {
                lastApproachDir = ((QuartusTransportInfoProvider) currBlock).nextDirection(world, currPos, lastApproachDir);
            } catch (Exception e) {
                System.out.println("Exception em: " + currPos);
                e.printStackTrace();
                return new Pair<>(currPos, initialApproach);
            }
            if (lastApproachDir == null) return null;

            System.out.println("*** POSICAO: " + currPos);
            System.out.println("*** ANDANDO NO FIO PARA" + lastApproachDir.asString());
            currPos = currPos.offset(lastApproachDir);
            System.out.println("*** POSICAO: " + currPos);

            currBlock = world.getBlockState(currPos).getBlock();
        }

        return new Pair<>(currPos, lastApproachDir);
    }

    public static List<Direction> getHorizontalDirections() {
        return HORIZONTAL_DIRECTIONS;
    }
}
