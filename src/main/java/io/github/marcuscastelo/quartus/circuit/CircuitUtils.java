package io.github.marcuscastelo.quartus.circuit;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.QuartusInGameComponent;
import io.github.marcuscastelo.quartus.block.QuartusTransportInfoProvider;
import io.github.marcuscastelo.quartus.circuit.components.CircuitComponent;
import io.github.marcuscastelo.quartus.circuit.components.CircuitInput;
import io.github.marcuscastelo.quartus.circuit.components.CircuitOutput;
import io.github.marcuscastelo.quartus.circuit.components.ComponentInfo;
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
import java.util.function.Function;

public class CircuitUtils {
    public static class ConnectedNodeInfo {
        public final Direction AtoB, BtoA;
        public final BlockPos bPos;

        public ConnectedNodeInfo(Direction AtoB, Direction BtoA, BlockPos bPos) {
            this.AtoB = AtoB;
            this.BtoA = BtoA;
            this.bPos = bPos;
        }
    }

    //Segue os fios até o próximo nó
    public static List<ConnectedNodeInfo> getConnectedNodesInfo(World world, QuartusCircuit circuit, CircuitComponent originNode, BlockPos originPos) {
        List<ConnectedNodeInfo> connectedNodesInfo = new ArrayList<>();

        //Para cada direção de output do nó origen
        for (Direction relativeDirectionOutOfOriginNode: originNode.getPossibleOutputDirections()) {
            //Obtém o blockstate imediatamente adjacente e exclui os que não são blocos do mod
            BlockState originBs = world.getBlockState(originPos);
            if (!(originBs.getBlock() instanceof QuartusInGameComponent) && !(originBs instanceof QuartusTransportInfoProvider)) continue;

            //Obtém as direções absolutas
            Direction originFacingDir = originBs.get(Properties.HORIZONTAL_FACING);
            Direction absoluteDirectionOutOfOriginNode = CircuitUtils.getAbsoluteDirection(originFacingDir, relativeDirectionOutOfOriginNode);
            Direction absoluteDirectionOutOfTargetNode;

            //Obtém informações dos vizinhos imediatos
            BlockPos immediateNeighborPos = originPos.offset(absoluteDirectionOutOfOriginNode);
            Block immediateNeighborBlock = world.getBlockState(immediateNeighborPos).getBlock();

            //Variável a ser definida nos ifs a seguir (posição do próximo componente)
            BlockPos targetPos = null;

            if (immediateNeighborBlock instanceof QuartusInGameComponent){
                //Se o vizinho for um componente, ele é o próprio componente
                targetPos = immediateNeighborPos;
                absoluteDirectionOutOfTargetNode = absoluteDirectionOutOfOriginNode.getOpposite();
            }
            else if (immediateNeighborBlock instanceof QuartusTransportInfoProvider) {
                //Se o vizinho for um fio, siga o fio e obtenha a posição do nó em sua extremidade
                Pair<BlockPos, Direction> throughWireNodeInfo = getTransportDestinationInfo(world, immediateNeighborPos, absoluteDirectionOutOfOriginNode);

                BlockPos foundBlockPos = throughWireNodeInfo.getLeft();
                absoluteDirectionOutOfTargetNode = throughWireNodeInfo.getRight();

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

    private static Pair<BlockPos, Direction> getTransportDestinationInfo(World world, BlockPos initialPos, Direction initialDirection) {
        BlockPos currPos = initialPos;
        Direction lastDirection;
        Direction currDirection = WireConnector.getNextDirection(world, currPos, initialDirection);;

        while (true) {
            lastDirection = currDirection;

            //Se chegar num fio não recíproco ou em um beco sem saída
            currPos = WireConnector.navigateWire(world, currPos, currDirection);
            if (currPos == null) break;

            //Se o bloco atual não for mais um fio, encerrar
            if (!(world.getBlockState(currPos).getBlock() instanceof QuartusTransportInfoProvider)) break;

            //Se estiver em um fio que não reconhece a direção informada (não deveria acontecer, pois o if acima checa)
            currDirection = WireConnector.getNextDirection(world, currPos, currDirection);
            if (currDirection == null) {
                throw new RuntimeException("There is something wrong that is causing navigateWire to allow perpendicular navigation");
            }
        }

        return new Pair<>(currPos, lastDirection.getOpposite());
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

    public static Direction getAbsoluteDirection(Direction facingDir, Direction relativeDirection) {
        Function<Direction, Direction> rotationFunction = getRotationFunction(facingDir);
        return rotationFunction.apply(relativeDirection);
    }

    public static Direction getRelativeDirection(Direction facingDir, Direction absoluteDireciton) {
        Function<Direction, Direction> rotationFunction = getRotationFunction(facingDir);

        //Para reverter uma conversão Relativa -> Absoluta, basta executar 3 vezes a transformação novamente
        Direction relativeDirection = absoluteDireciton;
        for (int i = 0; i < 3; i++)
            relativeDirection = rotationFunction.apply(relativeDirection);
        return relativeDirection;
    }

    public static CircuitComponent createPolimorphicComponent(String gateType, int gateID) {
        ComponentInfo info = QuartusCircuitComponents.getComponentInfoByName(gateType);
        if (gateType.equals(CircuitInput.COMP_NAME))
            return new CircuitInput(gateID);
        else if (gateType.equals(CircuitOutput.COMP_NAME))
            return new CircuitOutput(gateID);
        else
            return new CircuitComponent(gateType, info.directionInfo, gateID, QuartusLogics.getLogicByID(gateType));
    }

    public static Pair<String, Integer> getComponentStrInfo(String componentStr) {
        String[] params = componentStr.split("_");
        String gateType = params[0];
        int gateID = Integer.parseInt(params[1]);
        return new Pair<>(gateType, gateID);
    }
}