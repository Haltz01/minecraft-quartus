package io.github.marcuscastelo.quartus.circuit;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.QuartusInGameComponent;
import io.github.marcuscastelo.quartus.block.QuartusTransportInfoProvider;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CircuitUtils {
    public static final List<Direction> HORIZONTAL_DIRECTIONS = Arrays.asList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    //Segue os fios até o próximo nó
    public static List<BlockPos> getConnectedNodesPos(World world, QuartusCircuit circuit, QuartusCircuitComponent node, BlockPos nodePos) {
        List<BlockPos> connectedNodesPos = new ArrayList<>();
        for (Direction approachRelativeDirection: node.getPossibleOutputDirections()) {
            BlockState blockState = world.getBlockState(nodePos);
            if (!(blockState.getBlock() instanceof HorizontalFacingBlock)) continue;

            Direction facingDir = blockState.get(Properties.HORIZONTAL_FACING);
            Direction approachDirection = CircuitUtils.getAbsoluteDirection(facingDir, approachRelativeDirection);

            BlockPos neighborPos = nodePos.offset(approachDirection);
            Block neighborBlock = world.getBlockState(neighborPos).getBlock();
            BlockPos connectedNodePos = null;
            if (neighborBlock instanceof QuartusInGameComponent){
                connectedNodePos = neighborPos;
            }
            else if (neighborBlock instanceof QuartusTransportInfoProvider) {
                Pair<BlockPos, Direction> throughWireNodeInfo = getTransportDestinationInfo(world, neighborPos, approachDirection);

                if (throughWireNodeInfo == null) {
                    System.out.println("Fios mal conectados foram encontrados na linha " + neighborPos.toShortString());
                    continue;
                }

                BlockPos foundBlockPos = throughWireNodeInfo.getLeft();
                approachDirection = throughWireNodeInfo.getRight();

                if (foundBlockPos != null) {
                    Block connectedNodeBlock = world.getBlockState(foundBlockPos).getBlock();
                    if (connectedNodeBlock instanceof QuartusInGameComponent) {
                        System.out.println("Após os fios, encontrado " + connectedNodeBlock);
                        connectedNodePos = foundBlockPos;
                    }
                    else {
                        System.out.println("O FIO ESTÁ DESCONECTADO NO FIM DELE");
                        System.out.println("Acaba em " + foundBlockPos);
                        System.out.println("É um " + connectedNodeBlock);
                    }
                }
            } else {
                Quartus.LOGGER.info(String.format("Node com porta desconectada em %s", neighborPos.toString()));
                continue;
            }

            //Nenhum nó nessa direção
            if (connectedNodePos == null) continue;
            BlockState connectedNodeBlockState = world.getBlockState(connectedNodePos);
            Block connectedNodeBlock = connectedNodeBlockState.getBlock();
            Direction connectedBlockFacingDir = connectedNodeBlockState.get(Properties.HORIZONTAL_FACING);
            if (connectedNodeBlock instanceof QuartusInGameComponent) {
                List<Direction> relativePossibleInputDirections = ((QuartusInGameComponent) connectedNodeBlock).getPossibleInputDirections();
                List<Direction> absolutePossibleInputDirections = relativePossibleInputDirections.stream().map(direction -> getAbsoluteDirection(connectedBlockFacingDir, direction)).collect(Collectors.toList());
                if (absolutePossibleInputDirections.contains(approachDirection.getOpposite())) {
                    connectedNodesPos.add(connectedNodePos);
                } else {
                    System.out.println("Aproximando virado para " + approachDirection);
                    System.out.println("Node olhando para " + connectedBlockFacingDir);
                    System.out.println(connectedNodePos.toString() + " aproximado por direção ruim");
                }
            }
        }
        return connectedNodesPos;
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

    private static Function<Direction, Direction> getRotationFunction(Direction facingDir) {
        if (facingDir == Direction.NORTH) return direction -> direction;
        else if (facingDir == Direction.EAST) return Direction::rotateYClockwise;
        else if (facingDir == Direction.SOUTH) return Direction::getOpposite;
        else if (facingDir == Direction.WEST) return Direction::rotateYCounterclockwise;
        else throw new IllegalArgumentException("Unknown direction: " + facingDir);
    }

    public static Direction getAbsoluteDirection(Direction facingDir, Direction direction) {
        Function<Direction, Direction> rotationFunction = getRotationFunction(facingDir);
        return rotationFunction.apply(direction);
    }
}