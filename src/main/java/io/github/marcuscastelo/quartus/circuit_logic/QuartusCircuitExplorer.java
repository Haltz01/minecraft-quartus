package io.github.marcuscastelo.quartus.circuit_logic;

import com.google.common.collect.HashBiMap;
import io.github.marcuscastelo.quartus.Quartus;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class QuartusCircuitExplorer {
    //TODO: encapsulamento aqui e outras classes
    List<QuartusInput> inputs;
    List<QuartusInput> outputs;
    Map<BlockPos, QuartusNode> nodeInPosition;
    Map<QuartusNode, BlockPos> positionOfNode;

    World world;

    public QuartusCircuitExplorer(World world) {
        this.world = world;
        this.nodeInPosition = new HashMap<>();
        this.positionOfNode = new HashMap<>();
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }

    public int getOutputCount() { return outputs.size(); }
    public int getInputCount() { return inputs.size(); }

    public List<QuartusNode> getNodes() {
        return new ArrayList<>(nodeInPosition.values());
    }

    public void addNoteAt(QuartusNode node, BlockPos pos) {
        nodeInPosition.putIfAbsent(pos, node);
        positionOfNode.putIfAbsent(node, pos);
    }

    @Nullable
    public BlockPos getNodePos(QuartusNode node) {
        return positionOfNode.getOrDefault(node, null);
    }

    @Nullable
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
}
