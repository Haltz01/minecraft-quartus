package io.github.marcuscastelo.quartus.circuit_logic;

import com.sun.org.apache.bcel.internal.generic.RET;
import io.github.marcuscastelo.quartus.Quartus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
                Pair<BlockPos, Direction> throughWireNodeInfo = getNodePosThroughWire(neighborPos);
                assert throughWireNodeInfo != null;

                BlockPos connectedNodePos = throughWireNodeInfo.getLeft();
                approachDirection = throughWireNodeInfo.getRight();

                if (connectedNodePos != null) {
                    Block connectedNodeBlock = world.getBlockState(connectedNodePos).getBlock();
                    if (connectedNodeBlock instanceof QuartusNodeConvertible)
                        connectedNode = circuit.getNodeAt(connectedNodePos);
                }
            } else {
                Quartus.LOGGER.info(String.format("Node com porta desconectada em %s", neighborPos.toString()));
                continue;
            }


            try{
                if (connectedNode.getInputDirections().contains(approachDirection.getOpposite()))
                    connectedNodes.add(connectedNode);
            } catch (NullPointerException npe) {
                System.out.println("Node q deu merda: " + connectedNode);
                npe.printStackTrace();
            }

        }
        return connectedNodes;
    }

    private static Pair<BlockPos, Direction> getNodePosThroughWire(BlockPos wirePos) {
        Quartus.LOGGER.warn("NÃO USE FIOS AINDA, N TEM O CÓDIGO");
        return null;
    }

    public static List<Direction> getHorizontalDirections() {
        return HORIZONTAL_DIRECTIONS;
    }
}
