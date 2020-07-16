package io.github.marcuscastelo.quartus.circuit.analyze;

import io.github.marcuscastelo.quartus.block.QuartusInGameComponent;
import io.github.marcuscastelo.quartus.circuit.CircuitUtils;
import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitInput;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitOutput;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class CircuitCompiler {
    BlockPos startPos, endPos;
    QuartusCircuit circuit;
    World world;

    Queue<BlockPos> explorePoll;
    Map<BlockPos, QuartusCircuitComponent> componentInPos;

    public CircuitCompiler(World world, BlockPos startPos, BlockPos endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.world = world;
        this.circuit = new QuartusCircuit();
        this.explorePoll = new LinkedList<>();
        this.componentInPos = new HashMap<>();
    }

    private void scanCircuitNodes() {
        System.out.println("[Compile] Começando compilação");

        int startX, startY, startZ;
        // TODO: Mudar valores iniciais e finais de X, Y e Z -> devem sempre começar da mesma posição (canto "superior" esquerdo do circuito - olhando para o circuito)
        startX = Math.min(startPos.getX(), endPos.getX());
        startY = Math.min(startPos.getY(), endPos.getY());
        startZ = Math.min(startPos.getZ(), endPos.getZ());

        int endX, endY, endZ;
        endX = Math.max(startPos.getX(), endPos.getX());
        endY = Math.max(startPos.getY(), endPos.getY());
        endZ = Math.max(startPos.getZ(), endPos.getZ());

        if (startY != endY) throw new UnsupportedOperationException("Só se trabalha com y's iguais por enquanto");

        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                for (int y = startY; y <= endY; y++) {
                    BlockPos nodePos = new BlockPos(x,y,z);
                    Block nodeBlock = world.getBlockState(nodePos).getBlock();
                    if (!(nodeBlock instanceof QuartusInGameComponent)) continue;
                    System.out.println("[Compile] Encontrado um " + nodeBlock.getName().asString() + " em " + nodePos);
                    QuartusCircuitComponent node = ((QuartusInGameComponent) nodeBlock).getCircuitComponent();
                    componentInPos.putIfAbsent(nodePos, node);
                    circuit.addComponent(node);
                    if (node instanceof QuartusCircuitInput)
                        explorePoll.add(nodePos);
                }
            }
        }
    }

    private void exploreCircuit() {
        while (explorePoll.peek() != null) {
            BlockPos nodePos = explorePoll.poll();
            System.out.println("Explorando a pos " + nodePos.toString());

            QuartusCircuitComponent component = componentInPos.get(nodePos);
            assert component != null;

            if (component.hasOutputConnections()) {
                System.out.println("Posição já explorada... ignorando!");
                continue;
            }

            // TODO: mais ou menos por aqui deve ser adicionado um tratamento para os extensores e distribuidores!! -> eles não existem de verdade, são só facilitadores do que queremos fazer
            // TODO: extensor só pode ser conectado em GATES!!
            // Guerra vai fazer!!

            // Percorre os fios a partir de um node
            // Retorna 0 ou 1 nodes na maioria dos casos
            // Caso "especial": distribuidor -> a saída de um outro gate gera mais de um fio para vários inputs (de outros gates)
            // Caso "especial": extensores -> aumentam a quantidade de inputs de um gate
            //TODO: resolver parâmetros redundantes
            List<BlockPos> nextNodePositions = CircuitUtils.getConnectedNodesPos(world, circuit, component, nodePos);

            for (BlockPos nextNodePos : nextNodePositions) {
                System.out.println("Vizinho: " + nextNodePos);

                circuit.addLink(component, componentInPos.get(nextNodePos));

                if (componentInPos.get(nextNodePos).hasOutputConnections()) {
                    System.out.println("Vizinho já explorado... ignorando!");
                } else {
                    System.out.println("Adiconando Vizinho: " + nextNodePos.toString());
                    explorePoll.offer(nextNodePos);
                }
            }
        }
        System.out.println("[Explore] Exploracão completa!");
    }

    public QuartusCircuit compile() {
        scanCircuitNodes();
        exploreCircuit();

        return circuit;
    }
}