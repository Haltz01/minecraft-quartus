package io.github.marcuscastelo.quartus.circuit_logic;

import io.github.marcuscastelo.quartus.Quartus;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuartusCircuit {
    HashMap<BlockPos, QuartusNode> nodeInPosition;
    HashMap<QuartusNode, List<QuartusNode>> nodeConnections;

    World world;
    BlockPos pos;
    public QuartusCircuit(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
        this.nodeConnections = new HashMap<>();
        this.nodeInPosition = new HashMap<>();
    }

    public HashMap<QuartusNode, List<QuartusNode>> getNodeConnections() {
        return nodeConnections;
    }

    public QuartusNode getNodeAt(BlockPos pos) {
        return nodeInPosition.getOrDefault(pos, null);
    }

    public boolean isNodeAlreadyExplored(QuartusNode node) {
        return nodeConnections.containsKey(node);
    }

    public void addLink(QuartusNode fromNode, QuartusNode toNode) {
        nodeConnections.putIfAbsent(fromNode, new ArrayList<>());

        if (nodeConnections.get(fromNode).contains(toNode)) return;

        nodeConnections.get(fromNode).add(toNode);

        fromNode.addOutput(toNode);
        toNode.addInput(fromNode);
    }

    @Override
    public String toString() {
        StringBuilder repr = new StringBuilder();
        for (Map.Entry<QuartusNode, List<QuartusNode>> entry: getNodeConnections().entrySet()) {
            for (QuartusNode destNode: entry.getValue())
                repr.append(entry.getKey().toString()).append(" -> ").append(destNode.toString()).append('\n');
        }
        return repr.toString();
    }
}
