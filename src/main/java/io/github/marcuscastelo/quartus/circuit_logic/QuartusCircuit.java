package io.github.marcuscastelo.quartus.circuit_logic;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.registry.QuartusNodes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class QuartusCircuit {
    //TODO: encapsulamento aqui e outras classes
    List<QuartusInput> inputsPos;
    List<QuartusInput> outPutsPos;
    HashMap<BlockPos, QuartusNode> nodeInPosition;

    public QuartusCircuit() {
        this.nodeInPosition = new HashMap<>();
        this.inputsPos = new ArrayList<>();
        this.outPutsPos = new ArrayList<>();
    }

    public static QuartusCircuit of(String circuitDescription) {
        QuartusCircuit circuit = new QuartusCircuit();
        String[] connections = circuitDescription.split("\n");
        for (String connection : connections) {
            String[] nodes = connection.split("->");

//            String[] nodePartsA = nodes[0].split("@");
//            String nodeTypeA = nodePartsA[0];
//            String nodePosA = nodePartsA[1];
//            QuartusNode nodeA = QuartusNodes.instantiateByType(nodeTypeA);
        }
        return circuit;
    }

    public int getOutputCount() { return outPutsPos.size(); }
    public int getInputCount() { return inputsPos.size(); }

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
                repr.append(node.toString()).append("->").append(outputNode.toString()).append("\n");
            }
        }
        return repr.toString();
    }
}
