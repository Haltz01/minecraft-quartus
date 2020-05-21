package io.github.marcuscastelo.quartus.circuit_logic;

import io.github.marcuscastelo.quartus.Quartus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
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
        for (Direction approachDirection: node.getPossibleOutputDirections()) {
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

            if (connectedNode.isDirectionInput(approachDirection.getOpposite()))
            if (connectedNode.isDirectionInput(approachDirection.getOpposite()))
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

            currPos = currPos.offset(lastApproachDir);

            currBlock = world.getBlockState(currPos).getBlock();
        }

        return new Pair<>(currPos, lastApproachDir);
    }

    public static List<Direction> getHorizontalDirections() {
        return HORIZONTAL_DIRECTIONS;
    }


    ///TEMP TODO: REMOVE

    public static void outlineCompileRegionForClient(World world, BlockPos compilerPos, int size, Block fillBlock) {
        if (!world.isClient) return;

        BlockState fillState = fillBlock.getDefaultState();
        net.minecraft.util.math.Direction direction = world.getBlockState(compilerPos).get(Properties.HORIZONTAL_FACING).getOpposite();

        BlockPos oPos = compilerPos.offset(direction,10);
        world.setBlockState(oPos, fillState);
        for (int s = 1; s <= size/2; s++) {
            world.setBlockState(compilerPos.offset(direction.rotateYClockwise(), s), fillState);
            world.setBlockState(compilerPos.offset(direction.rotateYCounterclockwise(), s), fillState);
            world.setBlockState(oPos.offset(direction.rotateYClockwise(), s), fillState);
            world.setBlockState(oPos.offset(direction.rotateYCounterclockwise(), s), fillState);
        }

        BlockPos l, r;
        l = compilerPos.offset(direction.rotateYCounterclockwise(), size/2);
        r = compilerPos.offset(direction.rotateYClockwise(), size/2);
        for (int d = 1; d < size; d++) {
            world.setBlockState(l.offset(direction, d), fillState);
            world.setBlockState(r.offset(direction, d), fillState);
        }
    }
}
