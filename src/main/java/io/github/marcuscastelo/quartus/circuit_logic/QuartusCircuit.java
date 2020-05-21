package io.github.marcuscastelo.quartus.circuit_logic;

import io.github.marcuscastelo.quartus.Quartus;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class QuartusCircuit {
    HashMap<BlockPos, QuartusNode> nodeInPosition;

    World world;
    BlockPos pos;
    public QuartusCircuit(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
        this.nodeInPosition = new HashMap<>();
    }

    public List<QuartusNode> getNodes() {
        return new ArrayList<>(nodeInPosition.values());
    }

    public QuartusNode getNodeAt(BlockPos pos) {
        return nodeInPosition.getOrDefault(pos, null);
    }

    public boolean isNodeAlreadyExplored(QuartusNode node) {
        return node.getOutputs().size() > 0;
    }

    public void addLink(QuartusNode fromNode, QuartusNode toNode) {
        fromNode.addOutput(toNode);
        toNode.addInput(fromNode);
    }

    @Override
    public String toString() {
        StringBuilder repr = new StringBuilder();
        for (QuartusNode node: nodeInPosition.values()) {
            for (QuartusNode outputNode: node.getOutputs()) {
                repr.append(node.toString()).append(" -> ").append(outputNode.toString()).append("\n");
            }
        }
        return repr.toString();
    }
}
